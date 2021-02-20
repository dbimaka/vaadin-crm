package com.vaadin.tutorial.crm.backend.entities;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "PAYMENTS_TABLE")
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private LocalDate paymentDate;
    private double paymentSum;
    private double creditBodySum;
    private double creditPercentSum;
    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.MERGE, targetEntity = Offer.class)
    @JoinColumn(name = "offer_id")
    private Offer offerMapped;

    public LocalDate getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(LocalDate paymentDate) {
        this.paymentDate = paymentDate;
    }

    public double getPaymentSum() {
        return paymentSum;
    }

    public void setPaymentSum(double paymentSum) {
        this.paymentSum = paymentSum;
    }

    public double getCreditBodySum() {
        return creditBodySum;
    }

    public void setCreditBodySum(double creditBodySum) {
        this.creditBodySum = creditBodySum;
    }

    public double getCreditPercentSum() {
        return creditPercentSum;
    }

    public void setCreditPercentSum(double creditPercentSum) {
        this.creditPercentSum = creditPercentSum;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Offer getOffer() {
        return offerMapped;
    }

    public void setOffer(Offer offerMapped) {
        this.offerMapped = offerMapped;
    }
}
