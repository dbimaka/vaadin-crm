package com.vaadin.tutorial.crm.ui.views;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.tutorial.crm.backend.entities.Bank;
import com.vaadin.tutorial.crm.backend.services.BankService;
import com.vaadin.tutorial.crm.backend.services.ClientService;
import com.vaadin.tutorial.crm.backend.services.CreditService;
import com.vaadin.tutorial.crm.ui.MainLayout;

@Route(value = "banks", layout = MainLayout.class)
@PageTitle("Banks | Application")
public class BankListView extends VerticalLayout {
    private Grid<Bank> grid = new Grid<>(Bank.class);
    private BankForm bankForm;
    private BankService bankService;
    private ClientService clientService;
    private CreditService creditService;

    public BankListView(BankService bankService, ClientService clientService, CreditService creditService) {
        this.bankService = bankService;
        addClassName("bank-list-view");
        setSizeFull();
        configureGrid();
        bankForm = new BankForm(clientService.findAll(), creditService.findAll());
        bankForm.addListener(BankForm.SaveEvent.class, this::saveBank);
        bankForm.addListener(BankForm.DeleteEvent.class, this::deleteBank);
        bankForm.addListener(BankForm.CloseEvent.class, e -> closeEditor());
        Div content = new Div(grid, bankForm);
        content.addClassName("content");
        content.setSizeFull();
        add(getToolBar(), content);
        updateList();
        closeEditor();
    }

    private void saveBank(BankForm.SaveEvent event) {
        bankService.save(event.getBank());
        updateList();
        closeEditor();
    }

    private void deleteBank(BankForm.DeleteEvent event) {
        bankService.delete(event.getBank());
        updateList();
        closeEditor();
    }

    private void configureGrid() {
        grid.addClassName("bank-grid");
        grid.setColumns("name");
        grid.asSingleSelect().addValueChangeListener(event -> editBank(event.getValue()));
    }

    public void editBank(Bank bank) {
        if (bank == null) {
            closeEditor();
        } else {
            bankForm.setBank(bank);
            bankForm.clientGrid.setItems(bank.getClientList());
            bankForm.creditGrid.setItems(bank.getCreditList());
            bankForm.setVisible(true);
            addClassName("editing");
        }
    }

    private void closeEditor() {
        bankForm.setBank(null);
        bankForm.setVisible(false);
        removeClassName("editing");
    }

    private void updateList() {
        grid.setItems(bankService.findAll());
    }

    private HorizontalLayout getToolBar() {
        Button addBankButton = new Button("Add bank");
        addBankButton.addClickListener(click -> addBank());

        HorizontalLayout toolbar = new HorizontalLayout(addBankButton);
        toolbar.addClassName("toolbar");
        return toolbar;
    }

    void addBank() {
        grid.asSingleSelect().clear();
        editBank(new Bank());
        removeClassName("editing");
    }
}
