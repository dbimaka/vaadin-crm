package com.vaadin.tutorial.crm.ui;

import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.HighlightConditions;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.tutorial.crm.ui.views.*;

@CssImport("./styles/shared-styles.css")
public class MainLayout extends AppLayout {
    public MainLayout() {
        createHeader();
        createDrawer();
    }

    private void createDrawer() {
        RouterLink clientsListLink = new RouterLink("Clients list", ClientListView.class);
        clientsListLink.setHighlightCondition(HighlightConditions.sameLocation());
        RouterLink creditsListLink = new RouterLink("Credits list", CreditListView.class);
        creditsListLink.setHighlightCondition(HighlightConditions.sameLocation());
        RouterLink banksListLink =  new RouterLink("Banks list", BankListView.class);
        banksListLink.setHighlightCondition(HighlightConditions.sameLocation());
        RouterLink offerListLink = new RouterLink("Credit offers list", OfferListView.class);
        offerListLink.setHighlightCondition(HighlightConditions.sameLocation());
        addToDrawer(new VerticalLayout(clientsListLink), new VerticalLayout(creditsListLink), new VerticalLayout(banksListLink), new VerticalLayout(offerListLink));
    }

    private void createHeader() {
        H1 logo = new H1("Application");
        logo.addClassName("logo");

        HorizontalLayout header = new HorizontalLayout(new DrawerToggle(), logo);

        header.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        header.setWidth("100%");
        header.addClassName("header");

        addToNavbar(header);
    }
}
