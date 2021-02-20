package com.vaadin.tutorial.crm.backend.entities;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.LinkedList;
import java.util.List;

@Entity
@Table(name = "BANKS_TABLE")
public class Bank {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotNull
    @NotEmpty
    private String name;

    @OneToMany(mappedBy = "bank", fetch = FetchType.LAZY, cascade = CascadeType.MERGE, targetEntity = Client.class)
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<Client> clientList = new LinkedList<>();

    @OneToMany(mappedBy = "bank", fetch = FetchType.LAZY, cascade = CascadeType.MERGE, targetEntity = Credit.class)
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<Credit> creditList = new LinkedList<>();

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Client> getClientList() {
        return clientList;
    }

    public void setClientList(List<Client> clientList) {
        this.clientList = clientList;
    }

    public List<Credit> getCreditList() {
        return creditList;
    }

    public void setCreditList(List<Credit> creditList) {
        this.creditList = creditList;
    }
}
