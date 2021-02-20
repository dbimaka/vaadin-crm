package com.vaadin.tutorial.crm.ui.views;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.tutorial.crm.backend.entities.Bank;
import com.vaadin.tutorial.crm.backend.entities.Credit;
import com.vaadin.tutorial.crm.backend.services.BankService;
import com.vaadin.tutorial.crm.backend.services.CreditService;
import com.vaadin.tutorial.crm.ui.MainLayout;

@Route(value = "credits", layout = MainLayout.class)
@PageTitle("Credits | Application")
public class CreditListView extends VerticalLayout {
    private CreditService creditService;
    private Grid<Credit> grid = new Grid<>(Credit.class);
    private CreditForm creditForm;

    public CreditListView(CreditService creditService, BankService bankService) {
        this.creditService = creditService;
        addClassName("credit-list-view");
        setSizeFull();
        configureGrid();

        creditForm = new CreditForm(bankService.findAll());
        creditForm.addListener(CreditForm.SaveEvent.class, this::saveCredit);
        creditForm.addListener(CreditForm.DeleteEvent.class, this::deleteCredit);
        creditForm.addListener(CreditForm.CloseEvent.class, event -> closeEditor());

        Div content = new Div(grid, creditForm);
        content.addClassName("content");
        content.setSizeFull();
        add(getToolBar(), content);
        updateList();
        closeEditor();
    }

    private void configureGrid() {
        grid.addClassName("credit-grid");
        grid.setSizeFull();
        grid.removeColumnByKey("bank");
        grid.setColumns("limit", "interestRate");
        grid.addColumn(credit -> {
            Bank bank = credit.getBank();
            return bank == null ? "-" : bank.getName();
        }).setHeader("Bank");
        grid.getColumns().forEach(creditColumn -> creditColumn.setAutoWidth(true));
        grid.asSingleSelect().addValueChangeListener(event -> editCredit(event.getValue()));
    }

    private void saveCredit(CreditForm.SaveEvent event) {
        creditService.save(event.getCredit());
        updateList();
        closeEditor();
    }

    private void deleteCredit(CreditForm.DeleteEvent event) {
        creditService.delete(event.getCredit());
        updateList();
        closeEditor();
    }

    private void updateList() {
        grid.setItems(creditService.findAll());
    }

    private void editCredit(Credit credit) {
        if (credit == null) {
            closeEditor();
        } else {
            creditForm.setCredit(credit);
            creditForm.setVisible(true);
            addClassName("editing");
        }
    }

    void addCredit() {
        grid.asSingleSelect().clear();
        editCredit(new Credit());
        removeClassName("editing");
    }

    private void closeEditor() {
        creditForm.setCredit(null);
        creditForm.setVisible(false);
        removeClassName("editing");
    }

    private HorizontalLayout getToolBar() {
        Button addCreditButton = new Button("Add credit");
        addCreditButton.addClickListener(click -> addCredit());

        HorizontalLayout toolbar = new HorizontalLayout(addCreditButton);
        toolbar.addClassName("toolbar");
        return toolbar;
    }
}
