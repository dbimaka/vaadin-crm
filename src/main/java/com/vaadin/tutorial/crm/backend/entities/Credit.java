package com.vaadin.tutorial.crm.backend.entities;

import javax.persistence.*;

@Entity
@Table(name = "CREDITS_TABLE")
public class Credit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private int limit;


    private double interestRate;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.MERGE, targetEntity = Bank.class)
    @JoinColumn(name = "bank_id")
    private Bank bank;


    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public double getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(double interestRate) {
        this.interestRate = interestRate;
    }


    @Override
    public String toString() {
        String format = "Credit with limit of %s and IR of %s %%";
        return String.format(format, limit, interestRate);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Bank getBank() {
        return bank;
    }

    public void setBank(Bank bank) {
        this.bank = bank;
    }
}
