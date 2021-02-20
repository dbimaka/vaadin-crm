package com.vaadin.tutorial.crm.backend.services;

import com.vaadin.tutorial.crm.backend.entities.Client;
import com.vaadin.tutorial.crm.backend.repositories.ClientRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class ClientService {
        private static final Logger logger = Logger.getLogger(ClientService.class.getName());
        private ClientRepository clientRepository;

        public ClientService(ClientRepository clientRepository) {
            this.clientRepository = clientRepository;
        }

        public List<Client> findAll() {
            return clientRepository.findAll();
        }

        public void delete(Client client) {
            clientRepository.delete(client);
        }

        public void save(Client client) {
            if (client == null) {
                logger.log(Level.SEVERE, "Client is null.");
                return;
            }
            clientRepository.save(client);
        }

    }
