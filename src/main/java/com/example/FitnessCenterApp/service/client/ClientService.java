package com.example.FitnessCenterApp.service.client;

import com.example.FitnessCenterApp.controller.client.ClientDto;
import com.example.FitnessCenterApp.controller.client.CreateClientRequest;

import java.util.List;

public interface ClientService {
    List<ClientDto> getAllClients();

    ClientDto saveClient(CreateClientRequest createClientRequest);

}
