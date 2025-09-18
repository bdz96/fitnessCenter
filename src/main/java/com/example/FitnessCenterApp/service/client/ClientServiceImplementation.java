package com.example.FitnessCenterApp.service.client;

import com.example.FitnessCenterApp.controller.client.ClientDto;
import com.example.FitnessCenterApp.controller.client.CreateClientRequest;
import com.example.FitnessCenterApp.mapper.ClientMapper;
import com.example.FitnessCenterApp.model.ClientDB;
import com.example.FitnessCenterApp.repository.ClientRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class ClientServiceImplementation implements ClientService {

    private static final Logger log = LoggerFactory.getLogger(ClientServiceImplementation.class);

    private final ClientRepository clientRepository;
    private final ClientMapper clientMapper;
    private final BCryptPasswordEncoder passwordEncoder;

    public ClientServiceImplementation(ClientRepository clientRepository,
                                       ClientMapper clientMapper,
                                       BCryptPasswordEncoder passwordEncoder) {
        this.clientRepository = clientRepository;
        this.clientMapper = clientMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public ClientDto saveClient(CreateClientRequest request) {
        log.info("Creating new client with email={}", request.getEmail());

        ClientDB client = clientMapper.toDB(request);

        client.setPassword(passwordEncoder.encode(request.getPassword()));

        ClientDB saved = clientRepository.save(client);
        log.info("Client created with id={}", saved.getId());

        return clientMapper.fromDB(saved);
    }

    @Override
    public List<ClientDto> getAllClients() {
        return clientRepository.findAll()
                .stream()
                .map(clientMapper::fromDB)
                .toList();
    }

    @Override
    public ClientDto getClientById(Integer id) {
        return clientRepository.findById(id)
                .map(clientMapper::fromDB)
                .orElseThrow(() -> {
                    log.warn("Client not found with id={}", id);
                    return new ResponseStatusException(HttpStatus.NOT_FOUND, "Client not found");
                });
    }

    @Override
    public void deleteClient(Integer id) {
        if (!clientRepository.existsById(id)) {
            log.warn("Attempted to delete non-existent client with id={}", id);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Client not found");
        }
        clientRepository.deleteById(id);
        log.info("Deleted client with id={}", id);
    }

    @Override
    public ClientDto updateClient(Integer id, CreateClientRequest request) {
        ClientDB existing = clientRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Client not found with id={}", id);
                    return new ResponseStatusException(HttpStatus.NOT_FOUND, "Client not found");
                });

        log.info("Updating client with id={}", id);

        existing.setFirstName(request.getFirstName());
        existing.setLastName(request.getLastName());
        existing.setDateOfBirth(request.getDateOfBirth());
        existing.setEmail(request.getEmail());
        existing.setPhoneNumber(request.getPhoneNumber());

        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            existing.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        ClientDB updated = clientRepository.save(existing);
        log.info("Client updated with id={}", id);

        return clientMapper.fromDB(updated);
    }
}
