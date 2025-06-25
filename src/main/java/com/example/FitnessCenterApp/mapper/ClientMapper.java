package com.example.FitnessCenterApp.mapper;

import com.example.FitnessCenterApp.controller.client.ClientDto;
import com.example.FitnessCenterApp.controller.client.CreateClientRequest;
import com.example.FitnessCenterApp.model.ClientDB;

public class ClientMapper {

    // convert Client into ClientDto
    public static ClientDto fromDB(ClientDB client) {
        ClientDto clientDto = new ClientDto();
        clientDto.setFirstName(client.getFirstName());
        clientDto.setLastName(client.getLastName());
        clientDto.setDateOfBirth(client.getDateOfBirth());
        clientDto.setEmail(client.getEmail());
        clientDto.setPhoneNumber(client.getPhoneNumber());

        return clientDto;
    }

    // response object to Client
    public static ClientDB toDB(ClientDto clientDto) {
        ClientDB client = new ClientDB();
        client.setFirstName(clientDto.getFirstName());
        client.setLastName(clientDto.getLastName());
        client.setDateOfBirth(clientDto.getDateOfBirth());
        client.setEmail(clientDto.getEmail());
        client.setPhoneNumber(clientDto.getPhoneNumber());

        return client;
    }

    // convert CreateRequestClient into Client
    public static ClientDB toDB(CreateClientRequest createClientRequest) {
        ClientDB client = new ClientDB();
        client.setFirstName(createClientRequest.getFirstName());
        client.setLastName(createClientRequest.getLastName());
        client.setDateOfBirth(createClientRequest.getDateOfBirth());
        client.setEmail(createClientRequest.getEmail());
        client.setPhoneNumber(createClientRequest.getPhoneNumber());

        return client;
    }

}
