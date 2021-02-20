package com.vaadin.tutorial.crm.backend.services;

import com.vaadin.tutorial.crm.backend.entities.Offer;
import com.vaadin.tutorial.crm.backend.repositories.OfferRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class OfferService {
    private static final Logger logger = Logger.getLogger(OfferService.class.getName());
    private OfferRepository offerRepository;

    public OfferService(OfferRepository offerRepository) {
        this.offerRepository = offerRepository;
    }

    public List<Offer> findAll() {
        return offerRepository.findAll();
    }

    public void delete(Offer offer) {
        offerRepository.delete(offer);
    }

    public void save(Offer offer) {
        if (offer == null) {
            logger.log(Level.SEVERE, "Credit offer is null.");
            return;
        }
        offerRepository.saveAndFlush(offer);
    }
}
