package com.example.FitnessCenterApp.controller;

import com.example.FitnessCenterApp.controller.clientMembership.ClientMembershipDto;
import com.example.FitnessCenterApp.controller.clientMembership.CreateClientMembershipRequest;
import com.example.FitnessCenterApp.controller.clientMembership.SessionsRemainingDto;
import com.example.FitnessCenterApp.service.clientMemberships.ClientMembershipService;
import jakarta.persistence.EntityNotFoundException;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class ClientMembershipController {
    @Autowired
    ClientMembershipService clientMembershipService;

    @PostMapping(value = "/client-memberships")
    public ResponseEntity<ClientMembershipDto> addMembershipToClient(@RequestBody CreateClientMembershipRequest createClientMembershipRequest) {
        try {
            ClientMembershipDto newClientMembership = clientMembershipService.assignMembershipToClient(createClientMembershipRequest);
            return new ResponseEntity<>(newClientMembership, HttpStatus.CREATED);
        } catch (EntityNotFoundException ex) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (IllegalStateException ex) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        } catch (ConstraintViolationException ex) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/active-membership/{clientId}")
    public ResponseEntity<String> isClientMembershipActive(@PathVariable Integer clientId) {
        boolean active = clientMembershipService.isClientMembershipActive(clientId);
        if (active) {
            return new ResponseEntity<>("Client has an active membership.", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Client does not have an active membership.", HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/sessions-remaining/{clientId}")
    public ResponseEntity<SessionsRemainingDto> getRemainingSessions(@PathVariable Integer clientId) {
        try {
            Integer remainingSessions = clientMembershipService.getRemainingSessions(clientId);
            return ResponseEntity.ok(new SessionsRemainingDto(remainingSessions));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PostMapping("/use-session/{clientId}")
    public ResponseEntity<SessionsRemainingDto> useSession(@PathVariable Integer clientId) {
        try {
            Integer remainingSessions = clientMembershipService.useSession(clientId);
            return ResponseEntity.ok(new SessionsRemainingDto(remainingSessions));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
}
