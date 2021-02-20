package com.vaadin.tutorial.crm.ui.views;

import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.shared.Registration;
import com.vaadin.tutorial.crm.backend.entities.Bank;
import com.vaadin.tutorial.crm.backend.entities.Credit;

import java.util.List;
import java.util.Objects;

public class CreditForm extends FormLayout {
    private Credit credit;
    IntegerField limit = new IntegerField("Credit limit");
    NumberField interestRate = new NumberField("Interest rate");
    ComboBox<Bank> bank = new ComboBox<>("Bank");

    Button save = new Button("Save");
    Button delete = new Button("Delete");
    Button close = new Button("Cancel");

    Binder<Credit> binder = new BeanValidationBinder<>(Credit.class);

    public CreditForm(List<Bank> banks) {
        addClassName("credit-form");
        setSizeFull();
        binder.bindInstanceFields(this);
        binder.forField(limit)
                .withValidator(Objects::nonNull, "Limit cannot be null")
                .withValidator(integer -> integer > 0, "Limit cannot be 0 or lower")
                .bind(Credit::getLimit, Credit::setLimit);
        binder.forField(interestRate)
                .withValidator(Objects::nonNull, "Interest rate cannot be null")
                .withValidator(aDouble -> aDouble > 0, "Interest rate cannot be 0 or lower")
                .bind(Credit::getInterestRate, Credit::setInterestRate);
        bank.setItems(banks);
        bank.setItemLabelGenerator(Bank::getName);

        add(limit,
                interestRate,
                bank,
                createButtonsLayout());
    }

    public void setCredit(Credit credit) {
        this.credit = credit;
        binder.readBean(credit);
    }

    private HorizontalLayout createButtonsLayout() {
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        delete.addThemeVariants(ButtonVariant.LUMO_ERROR);
        close.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        save.addClickShortcut(Key.ENTER);
        close.addClickShortcut(Key.ESCAPE);

        save.addClickListener(event -> validateAndSave());
        delete.addClickListener(event -> fireEvent(new DeleteEvent(this, credit)));
        close.addClickListener(event -> fireEvent(new CloseEvent(this)));
        return new HorizontalLayout(save, delete, close);
    }

    private void validateAndSave() {
        try {
            binder.writeBean(credit);
            fireEvent(new SaveEvent(this, credit));
        } catch (ValidationException e) {
            e.printStackTrace();
        }
    }

    public static abstract class CreditFormEvent extends ComponentEvent<CreditForm> {
        private Credit credit;

        public CreditFormEvent(CreditForm source, Credit credit) {
            super(source, false);
            this.credit = credit;
        }

        public Credit getCredit() {
            return credit;
        }
    }

    public static class SaveEvent extends CreditFormEvent {
        SaveEvent(CreditForm source, Credit credit) {
            super(source, credit);
        }
    }

    public static class DeleteEvent extends CreditFormEvent {
        DeleteEvent(CreditForm source, Credit credit) {
            super(source, credit);
        }
    }

    public static class CloseEvent extends CreditFormEvent {
        CloseEvent(CreditForm source) {
            super(source, null);
        }
    }

    public <T extends ComponentEvent<?>> Registration addListener(Class<T> eventType, ComponentEventListener<T> listener) {
        return getEventBus().addListener(eventType, listener);
    }
}

