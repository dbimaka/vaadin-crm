package com.vaadin.tutorial.crm.backend.services;

import com.vaadin.tutorial.crm.backend.entities.Bank;
import com.vaadin.tutorial.crm.backend.repositories.BankRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class BankService {
    private static final Logger logger = Logger.getLogger(BankService.class.getName());

    private BankRepository bankRepository;

    public BankService(BankRepository bankRepository) {
        this.bankRepository = bankRepository;
    }

    public List<Bank> findAll() {
        return bankRepository.findAll();
    }

    public void delete(Bank bank) {
        bankRepository.delete(bank);
    }

    public void save(Bank bank) {
        if (bank == null) {
            logger.log(Level.SEVERE, "Bank is null.");
            return;
        }
        bankRepository.save(bank);
    }
}
