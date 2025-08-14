package com.example.FitnessCenterApp.controller;

import com.example.FitnessCenterApp.controller.clientMembership.ClientMembershipDto;
import com.example.FitnessCenterApp.controller.clientMembership.CreateClientMembershipRequest;
import com.example.FitnessCenterApp.controller.clientMembership.SessionsRemainingDto;
import com.example.FitnessCenterApp.service.clientMemberships.ClientMembershipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class ClientMembershipController {

    public static final String BASE_PATH = "/client-memberships";
    public static final String ACTIVE_MEMBERSHIP_PATH = "/active-membership/{clientId}";
    public static final String SESSIONS_REMAINING_PATH = "/sessions-remaining/{clientId}";
    public static final String USE_SESSION_PATH = "/use-session/{clientId}";

    private final ClientMembershipService clientMembershipService;

    @Autowired
    public ClientMembershipController(ClientMembershipService clientMembershipService) {
        this.clientMembershipService = clientMembershipService;
    }

    @PostMapping(BASE_PATH)
    @ResponseStatus(HttpStatus.CREATED)
    public ClientMembershipDto addMembershipToClient(@RequestBody CreateClientMembershipRequest request) {
        return clientMembershipService.assignMembershipToClient(request);
    }

    @GetMapping(ACTIVE_MEMBERSHIP_PATH)
    public ResponseEntity<String> isClientMembershipActive(@PathVariable Integer clientId) {
        boolean active = clientMembershipService.isClientMembershipActive(clientId);
        if (active) {
            return ResponseEntity.ok("Client has an active membership.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Client does not have an active membership.");
        }
    }

    @GetMapping(SESSIONS_REMAINING_PATH)
    public SessionsRemainingDto getRemainingSessions(@PathVariable Integer clientId) {
        Integer remainingSessions = clientMembershipService.getRemainingSessions(clientId);
        return new SessionsRemainingDto(remainingSessions);
    }

    @PostMapping(USE_SESSION_PATH)
    public SessionsRemainingDto useSession(@PathVariable Integer clientId) {
        Integer remainingSessions = clientMembershipService.useSession(clientId);
        return new SessionsRemainingDto(remainingSessions);
    }
}
