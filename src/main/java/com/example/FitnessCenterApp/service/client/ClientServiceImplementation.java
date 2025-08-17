package com.example.FitnessCenterApp.service.client;

import com.example.FitnessCenterApp.controller.client.ClientDto;
import com.example.FitnessCenterApp.controller.client.CreateClientRequest;
import com.example.FitnessCenterApp.exception.EmailAlreadyExistsException;
import com.example.FitnessCenterApp.exception.ResourceNotFoundException;
import com.example.FitnessCenterApp.mapper.ClientMapper;
import com.example.FitnessCenterApp.model.ClientDB;
import com.example.FitnessCenterApp.repository.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ClientServiceImplementation implements ClientService {
    private final ClientRepository clientRepository;
    private final ClientMapper clientMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    public ClientServiceImplementation(ClientRepository clientRepository, ClientMapper clientMapper) {
        this.clientRepository = clientRepository;
        this.clientMapper = clientMapper;
    }

    @Override
    public ClientDto saveClient(CreateClientRequest createClientRequest) {
        if (clientRepository.existsByEmail(createClientRequest.getEmail())) {
            throw new EmailAlreadyExistsException("Email already exists");
        }

        ClientDB client = clientMapper.toDB(createClientRequest);
        client.setPassword(passwordEncoder.encode(createClientRequest.getPassword()));

        ClientDB savedClient = clientRepository.save(client);
        return clientMapper.fromDB(savedClient);
    }

    @Override
    public List<ClientDto> getAllClients() {
        return clientRepository.findAll().stream()
                .map(clientMapper::fromDB)
                .collect(Collectors.toList());
    }

    @Override
    public ClientDto getClientById(Integer id) {
        ClientDB client = clientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Client not found with id: " + id));
        return clientMapper.fromDB(client);
    }

    @Override
    public void deleteClient(Integer id) {
        if (!clientRepository.existsById(id)) {
            throw new ResourceNotFoundException("Client not found with id: " + id);
        }
        clientRepository.deleteById(id);
    }

    @Override
    public ClientDto updateClient(Integer id, CreateClientRequest request) {
        ClientDB existing = clientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Client not found with id: " + id));

        if (!existing.getEmail().equals(request.getEmail()) &&
                clientRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException("Email already exists");
        }

        existing.setFirstName(request.getFirstName());
        existing.setLastName(request.getLastName());
        existing.setDateOfBirth(request.getDateOfBirth());
        existing.setEmail(request.getEmail());
        existing.setPhoneNumber(request.getPhoneNumber());

        ClientDB updated = clientRepository.save(existing);
        return clientMapper.fromDB(updated);
    }
}