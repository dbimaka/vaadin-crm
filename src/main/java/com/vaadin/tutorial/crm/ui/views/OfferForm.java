package com.vaadin.tutorial.crm.ui.views;

import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.shared.Registration;
import com.vaadin.tutorial.crm.backend.entities.Client;
import com.vaadin.tutorial.crm.backend.entities.Credit;
import com.vaadin.tutorial.crm.backend.entities.Offer;
import com.vaadin.tutorial.crm.backend.entities.Payment;
import com.vaadin.tutorial.crm.backend.services.PaymentService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class OfferForm extends FormLayout {
    private Offer offer;
    private final PaymentService paymentService;
    ComboBox<Client> client = new ComboBox<>("Client");
    ComboBox<Credit> credit = new ComboBox<>("Credit");
    IntegerField creditSum = new IntegerField("Credit sum");
    IntegerField creditTerm = new IntegerField("Credit term in months");
    Grid<Payment> paymentGrid = new Grid<>(Payment.class);
    Button save = new Button("Save");
    Button delete = new Button("Delete");
    Button close = new Button("Cancel");
    Button generatePayments = new Button("Generate payment schedule");

    Binder<Offer> binder = new BeanValidationBinder<>(Offer.class);

    public OfferForm(List<Client> clients, List<Credit> credits, PaymentService paymentService) {
        addClassName("credit-offer-form");
        this.paymentService = paymentService;
        setSizeFull();
        binder.bindInstanceFields(this);
        binder.forField(client)
                .withValidator(Objects::nonNull, "Client cannot be null")
                .bind(Offer::getClient, Offer::setClient);
        binder.forField(credit)
                .withValidator(Objects::nonNull, "Credit cannot be null")
                .bind(Offer::getCredit, Offer::setCredit);
        binder.forField(creditSum)
                .withValidator(Objects::nonNull, "Credit sum cannot be null")
                .withValidator(integer -> integer > 0, "Credit sum cannot be 0 or lower")
                .withValidator(integer -> integer <= credit.getValue().getLimit(), "Credit sum cannot exceed credit limit")
                .bind(Offer::getCreditSum, Offer::setCreditSum);
        binder.forField(creditTerm)
                .withValidator(Objects::nonNull, "Credit term cannot be null")
                .withValidator(integer -> integer > 0, "Credit term cannot be 0 or lower")
                .bind(Offer::getCreditTerm, Offer::setCreditTerm);
        client.setItems(clients);
        client.setItemLabelGenerator(Client::toString);
        credit.setItems(credits);
        credit.setItemLabelGenerator(Credit::toString);
        paymentGrid.setColumns("paymentDate", "paymentSum", "creditBodySum", "creditPercentSum");
        add(client,
                credit,
                creditSum,
                creditTerm,
                paymentGrid,
                createButtonsLayout());
    }

    private HorizontalLayout createButtonsLayout() {
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        delete.addThemeVariants(ButtonVariant.LUMO_ERROR);
        close.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        generatePayments.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        save.addClickShortcut(Key.ENTER);
        close.addClickShortcut(Key.ESCAPE);

        save.addClickListener(event -> validateAndSave());
        delete.addClickListener(event -> fireEvent(new DeleteEvent(this, offer)));
        close.addClickListener(event -> fireEvent(new CloseEvent(this)));
        generatePayments.addClickListener(click -> {
            if (offer.getCredit() == null && offer.getCreditSum() == 0 && offer.getCreditTerm() == 0) {
                Notification.show("Save the credit offer entity first!");
            } else if (!offer.getPaymentList().isEmpty()) {
                Notification.show("Payment list already generated!");
                generatePayments.setEnabled(false);
            } else {
                List<Payment> paymentSchedule = generatePaymentsSchedule(offer);
                for (Payment payment : paymentSchedule) {
                    paymentService.save(payment);
                }
                fireEvent(new SaveEvent(this, offer));
            }
        });
        return new HorizontalLayout(save, delete, close, generatePayments);
    }

    private List<Payment> generatePaymentsSchedule(Offer offer) {

        double creditSum = offer.getCreditSum();
        //Calculating monthly payment
        double monthlyInterest = offer.getCredit().getInterestRate() / (12 * 100); //p
        double termInMonths = offer.getCreditTerm();
        double monthlyPayment = offer.getCreditSum() * (monthlyInterest + monthlyInterest / (Math.pow((1 + monthlyInterest), termInMonths) - 1));

        double balance = creditSum;
        double interest;
        double principal;
        LocalDate startDate = LocalDate.now().with(TemporalAdjusters.firstDayOfNextMonth());
        List<Payment> paymentList = new LinkedList<>();

        for (int i = 1, j = 0; i <= termInMonths; i++, j++) {
            Payment payment = new Payment();
            interest = balance * monthlyInterest;
            LocalDate paymentDate = startDate.plusMonths(j).with((TemporalAdjusters.firstDayOfNextMonth()));
            payment.setPaymentDate(paymentDate);
            payment.setPaymentSum(round(monthlyPayment, 4));
            principal = monthlyPayment - interest;
            balance = balance - principal;
            payment.setCreditPercentSum(round(principal, 4));
            payment.setCreditBodySum(round(balance, 4));
            payment.setOffer(offer);
            paymentList.add(payment);
        }
        return paymentList;
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    public void setCreditOffer(Offer offer) {
        this.offer = offer;
        binder.readBean(offer);
    }

    private void validateAndSave() {
        try {
            binder.writeBean(offer);
            fireEvent(new SaveEvent(this, offer));
        } catch (ValidationException e) {
            e.printStackTrace();
        }
    }


    public static abstract class OfferFormEvent extends ComponentEvent<OfferForm> {
        private Offer offer;

        public OfferFormEvent(OfferForm source, Offer offer) {
            super(source, false);
            this.offer = offer;
        }

        public Offer getOffer() {
            return offer;
        }
    }

    public static class SaveEvent extends OfferFormEvent {
        SaveEvent(OfferForm source, Offer offer) {
            super(source, offer);
        }
    }

    public static class DeleteEvent extends OfferFormEvent {
        DeleteEvent(OfferForm source, Offer offer) {
            super(source, offer);
        }
    }

    public static class CloseEvent extends OfferFormEvent {
        CloseEvent(OfferForm source) {
            super(source, null);
        }
    }

    public <T extends ComponentEvent<?>> Registration addListener
            (Class<T> eventType, ComponentEventListener<T> listener) {
        return getEventBus().addListener(eventType, listener);
    }
}
