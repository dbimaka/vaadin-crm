package com.vaadin.tutorial.crm.ui.views;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.tutorial.crm.backend.entities.Bank;
import com.vaadin.tutorial.crm.backend.entities.Client;
import com.vaadin.tutorial.crm.backend.services.BankService;
import com.vaadin.tutorial.crm.backend.services.ClientService;
import com.vaadin.tutorial.crm.ui.MainLayout;

@Route(value = "clients", layout = MainLayout.class)
@PageTitle("Clients | Application")
public class ClientListView extends VerticalLayout {
    private ClientService clientService;
    private Grid<Client> grid = new Grid<>(Client.class);
    private ClientForm clientForm;

    public ClientListView(ClientService clientService, BankService bankService) {
        this.clientService = clientService;
        addClassName("client-list-view");
        setSizeFull();
        configureGrid();
        clientForm = new ClientForm(bankService.findAll());
        clientForm.addListener(ClientForm.SaveEvent.class, this::saveClient);
        clientForm.addListener(ClientForm.DeleteEvent.class, this::deleteClient);
        clientForm.addListener(ClientForm.CloseEvent.class, e -> closeEditor());
        Div content = new Div(grid, clientForm);
        content.addClassName("content");
        content.setSizeFull();
        add(getToolBar(), content);
        updateList();
        closeEditor();
    }


    private void saveClient(ClientForm.SaveEvent event) {
        clientService.save(event.getClient());
        updateList();
        closeEditor();
    }

    private void deleteClient(ClientForm.DeleteEvent event) {
        clientService.delete(event.getClient());
        updateList();
        closeEditor();
    }

    private void configureGrid() {
        grid.addClassName("client-grid");
        grid.setSizeFull();
        grid.removeColumnByKey("bank");
        grid.setColumns("firstName", "lastName", "phone", "email", "passport");
        grid.addColumn(client -> {
            Bank bank = client.getBank();
            return bank == null ? "-" : bank.getName();
        }).setHeader("Bank");
        grid.getColumns().forEach(clientColumn -> clientColumn.setAutoWidth(true));
        grid.asSingleSelect().addValueChangeListener(event -> editClient(event.getValue()));
    }

    public void editClient(Client client) {
        if (client == null) {
            closeEditor();
        } else {
            clientForm.setClient(client);
            clientForm.setVisible(true);
            addClassName("editing");
        }
    }

    private void closeEditor() {
        clientForm.setClient(null);
        clientForm.setVisible(false);
        removeClassName("editing");
    }

    private void updateList() {
        grid.setItems(clientService.findAll());
    }

    private HorizontalLayout getToolBar() {
        Button addClientButton = new Button("Add client");
        addClientButton.addClickListener(click -> addClient());

        HorizontalLayout toolbar = new HorizontalLayout(addClientButton);
        toolbar.addClassName("toolbar");
        return toolbar;
    }

    void addClient() {
        grid.asSingleSelect().clear();
        editClient(new Client());
        removeClassName("editing");
    }
}
