package com.example.FitnessCenterApp.service.client;

import com.example.FitnessCenterApp.controller.client.ClientDto;
import com.example.FitnessCenterApp.controller.client.CreateClientRequest;
import com.example.FitnessCenterApp.mapper.ClientMapper;
import com.example.FitnessCenterApp.model.ClientDB;
import com.example.FitnessCenterApp.repository.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ClientServiceImplementation implements ClientService {
    @Autowired
    ClientRepository clientRepository;

    public ClientDto saveClient (CreateClientRequest createClientRequest){
        ClientDB mappedClient = ClientMapper.toDB(createClientRequest);
        ClientDB savedClient = clientRepository.save(mappedClient);
        return ClientMapper.fromDB(savedClient);
    }

    public List<ClientDto> getAllClients (){
        List<ClientDB> clients = clientRepository.findAll();

        return clients.stream()
                .map(ClientMapper::fromDB)
                .collect(Collectors.toList());

    }
}
