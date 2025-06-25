package com.example.FitnessCenterApp.service.clientMemberships;

import com.example.FitnessCenterApp.controller.clientMembership.ClientMembershipDto;
import com.example.FitnessCenterApp.controller.clientMembership.CreateClientMembershipRequest;
import com.example.FitnessCenterApp.mapper.ClientMembershipMapper;
import com.example.FitnessCenterApp.model.ClientDB;
import com.example.FitnessCenterApp.model.ClientMembershipDB;
import com.example.FitnessCenterApp.model.MembershipDB;
import com.example.FitnessCenterApp.model.enums.TimeUnit;
import com.example.FitnessCenterApp.repository.ClientMembershipRepository;
import com.example.FitnessCenterApp.repository.ClientRepository;
import com.example.FitnessCenterApp.repository.MembershipRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class ClientMembershipServiceImplementation implements ClientMembershipService {
    @Autowired
    ClientRepository clientRepository;

    @Autowired
    MembershipRepository membershipRepository;

    @Autowired
    ClientMembershipRepository clientMembershipRepository;

    // assign membership to client
    @Override
    public ClientMembershipDto assignMembershipToClient(CreateClientMembershipRequest createClientMembershipRequest) {

        if (isClientMembershipActive(createClientMembershipRequest.getClientId())) {
            throw new IllegalStateException("Client already has an active membership.");
        }

        ClientDB clientDB = clientRepository.findById(createClientMembershipRequest.getClientId())
                .orElseThrow(() -> new EntityNotFoundException("Client not found"));
        MembershipDB membershipDB = membershipRepository.findById(createClientMembershipRequest.getMembershipId())
                .orElseThrow(() -> new EntityNotFoundException("Membership not found"));

        LocalDate createdAtDate = LocalDate.now();
        LocalDate expiresAtDate = calculateEndDate(createdAtDate, membershipDB);

        ClientMembershipDB mappedClientMembership = ClientMembershipMapper.toDB(clientDB, membershipDB, createdAtDate, expiresAtDate); // no req model for now as it's not needed can be added per need
        ClientMembershipDB savedClientMembership = clientMembershipRepository.save(mappedClientMembership);

        return ClientMembershipMapper.fromDB(savedClientMembership);

    }

   // calculate time from posting date + duration based on time unit
    public LocalDate calculateEndDate(LocalDate createdAtDate, MembershipDB membershipDB) {
        int duration = Integer.parseInt(membershipDB.getDuration());
        TimeUnit timeUnit = membershipDB.getTimeUnit();

        return switch (timeUnit) {
            case DAYS -> createdAtDate.plusDays(duration);
            case WEEKS -> createdAtDate.plusWeeks(duration);
            case MONTHS -> createdAtDate.plusMonths(duration);
            case YEARS -> createdAtDate.plusYears(duration);
        };
    }

    // check does client already have active membership
    @Override
    public boolean isClientMembershipActive(Integer clientId) {
        return clientMembershipRepository.findActiveMembershipByClientId(clientId) != null;
    }
}