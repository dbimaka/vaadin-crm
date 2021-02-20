package com.vaadin.tutorial.crm.backend.services;

import com.vaadin.tutorial.crm.backend.entities.Credit;
import com.vaadin.tutorial.crm.backend.repositories.CreditRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class CreditService {
    private static final Logger logger = Logger.getLogger(CreditService.class.getName());
    private CreditRepository creditRepository;

    public CreditService(CreditRepository creditRepository) {
        this.creditRepository = creditRepository;
    }

    public List<Credit> findAll() {
        return creditRepository.findAll();
    }

    public void delete(Credit credit) {
        creditRepository.delete(credit);
    }

    public void save(Credit credit) {
        if (credit == null) {
            logger.log(Level.SEVERE, "Credit is null.");
            return;
        }
        creditRepository.saveAndFlush(credit);
    }
}
