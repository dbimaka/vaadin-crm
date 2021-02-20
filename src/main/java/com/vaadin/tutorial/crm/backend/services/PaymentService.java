package com.vaadin.tutorial.crm.backend.services;

import com.vaadin.tutorial.crm.backend.entities.Payment;
import com.vaadin.tutorial.crm.backend.repositories.PaymentRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class PaymentService {
    private static final Logger logger = Logger.getLogger(PaymentService.class.getName());
    private PaymentRepository paymentRepository;

    public PaymentService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    public List<Payment> findAll() {
        return paymentRepository.findAll();
    }

    public void delete(Payment payment) {
        paymentRepository.delete(payment);
    }

    public void save(Payment payment) {
        if (payment == null) {
            logger.log(Level.SEVERE, "Payment is null.");
            return;
        }
        paymentRepository.saveAndFlush(payment);
    }
}
