package com.vaadin.tutorial.crm.ui.views;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.tutorial.crm.backend.entities.Offer;
import com.vaadin.tutorial.crm.backend.services.ClientService;
import com.vaadin.tutorial.crm.backend.services.CreditService;
import com.vaadin.tutorial.crm.backend.services.OfferService;
import com.vaadin.tutorial.crm.backend.services.PaymentService;
import com.vaadin.tutorial.crm.ui.MainLayout;

@Route(value = "creditOffers", layout = MainLayout.class)
@PageTitle("Credit offers | Application")
public class OfferListView extends VerticalLayout {
    private final Grid<Offer> grid = new Grid<>(Offer.class);
    private final OfferForm offerForm;
    private final OfferService offerService;
    private final PaymentService paymentService;

    public OfferListView(OfferService offerService, PaymentService paymentService, ClientService clientService, CreditService creditService) {
        this.offerService = offerService;
        this.paymentService = paymentService;
        setSizeFull();
        configureGrid();
        addClassName("credit-offer-list-view");
        offerForm = new OfferForm(clientService.findAll(), creditService.findAll(), paymentService);
        offerForm.addListener(OfferForm.SaveEvent.class, this::saveCreditOffer);
        offerForm.addListener(OfferForm.DeleteEvent.class, this::deleteCreditOffer);
        offerForm.addListener(OfferForm.CloseEvent.class, e -> closeEditor());
        Div content = new Div(grid, offerForm);
        content.addClassName("content");
        content.setSizeFull();
        add(getToolBar(), content);
        updateList();
        closeEditor();
    }

    private void saveCreditOffer(OfferForm.SaveEvent event) {
        offerService.save(event.getOffer());
        updateList();
        closeEditor();
    }

    private void deleteCreditOffer(OfferForm.DeleteEvent event) {
        offerService.delete(event.getOffer());
        updateList();
        closeEditor();
    }

    private void configureGrid() {
        grid.addClassName("credit-offer-grid");
        grid.setColumns("client", "credit", "creditSum", "creditTerm");
        grid.asSingleSelect().addValueChangeListener(event -> editCreditOffer(event.getValue()));
    }

    public void editCreditOffer(Offer offer) {
        if (offer == null) {
            closeEditor();
        } else {
            offerForm.setCreditOffer(offer);
            offerForm.paymentGrid.setItems(offer.getPaymentList());
            offerForm.generatePayments.setEnabled(true);
            offerForm.setVisible(true);
            addClassName("editing");
        }
    }

    private void closeEditor() {
        offerForm.setCreditOffer(null);
        offerForm.setVisible(false);
        grid.asSingleSelect().clear();
        removeClassName("editing");
    }

    private void updateList() {
        grid.setItems(offerService.findAll());
    }

    private HorizontalLayout getToolBar() {
        Button addCreditOfferButton = new Button("Add credit offer");
        addCreditOfferButton.addClickListener(click -> addCreditOffer());

        HorizontalLayout toolbar = new HorizontalLayout(addCreditOfferButton);
        toolbar.addClassName("toolbar");
        return toolbar;
    }

    void addCreditOffer() {
        grid.asSingleSelect().clear();
        editCreditOffer(new Offer());
        removeClassName("editing");
    }
}
