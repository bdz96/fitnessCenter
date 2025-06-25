package com.example.FitnessCenterApp.controller;

import com.example.FitnessCenterApp.controller.client.ClientDto;
import com.example.FitnessCenterApp.controller.client.CreateClientRequest;
import com.example.FitnessCenterApp.service.client.ClientService;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ClientController {
    @Autowired
    ClientService clientService;

    @PostMapping(value = "/clients")
    public ResponseEntity<ClientDto> add(@RequestBody CreateClientRequest createClientRequest) {
        try {
            ClientDto newClient = clientService.saveClient(createClientRequest);
            return new ResponseEntity<>(newClient, HttpStatus.CREATED);
        } catch (ConstraintViolationException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(value = "/clients")
    public ResponseEntity<List<ClientDto>> getClients() {
        List<ClientDto> clientList = clientService.getAllClients();
        return new ResponseEntity<>(clientList, HttpStatus.OK);

    }
}
