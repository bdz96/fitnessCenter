package com.example.FitnessCenterApp.service.clientMemberships;

import com.example.FitnessCenterApp.controller.clientMembership.ClientMembershipDto;
import com.example.FitnessCenterApp.controller.clientMembership.CreateClientMembershipRequest;
import com.example.FitnessCenterApp.exception.ClientAlreadyHasActiveMembershipException;
import com.example.FitnessCenterApp.mapper.ClientMembershipMapper;
import com.example.FitnessCenterApp.model.ClientDB;
import com.example.FitnessCenterApp.model.ClientMembershipDB;
import com.example.FitnessCenterApp.model.MembershipDB;
import com.example.FitnessCenterApp.model.enums.TimeUnit;
import com.example.FitnessCenterApp.repository.ClientMembershipRepository;
import com.example.FitnessCenterApp.repository.ClientRepository;
import com.example.FitnessCenterApp.repository.MembershipRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class ClientMembershipServiceImplementation implements ClientMembershipService {
    private final ClientRepository clientRepository;
    private final MembershipRepository membershipRepository;
    private final ClientMembershipRepository clientMembershipRepository;
    private final ClientMembershipMapper clientMembershipMapper;

    @Autowired
    public ClientMembershipServiceImplementation(ClientRepository clientRepository,
                                                 MembershipRepository membershipRepository,
                                                 ClientMembershipRepository clientMembershipRepository,
                                                 ClientMembershipMapper clientMembershipMapper) {
        this.clientRepository = clientRepository;
        this.membershipRepository = membershipRepository;
        this.clientMembershipRepository = clientMembershipRepository;
        this.clientMembershipMapper = clientMembershipMapper;
    }

    @Override
    public ClientMembershipDto assignMembershipToClient(CreateClientMembershipRequest request) {

        ClientDB clientDB = clientRepository.findById(request.getClientId())
                .orElseThrow(() -> new EntityNotFoundException("Client not found"));

        MembershipDB membershipDB = membershipRepository.findById(request.getMembershipId())
                .orElseThrow(() -> new EntityNotFoundException("Membership not found"));

        if (isClientMembershipActive(request.getClientId())) {
            throw new ClientAlreadyHasActiveMembershipException("Client already has an active membership.");
        }

        LocalDate createdAtDate = LocalDate.now();
        LocalDate expiresAtDate = calculateEndDate(createdAtDate, membershipDB);

        ClientMembershipDB mappedClientMembership = clientMembershipMapper.toDB(clientDB, membershipDB, createdAtDate, expiresAtDate);
        ClientMembershipDB savedClientMembership = clientMembershipRepository.save(mappedClientMembership);

        return clientMembershipMapper.fromDB(savedClientMembership);
    }

    public LocalDate calculateEndDate(LocalDate createdAtDate, MembershipDB membershipDB) {
        int duration = Integer.parseInt(membershipDB.getDuration());
        return switch (membershipDB.getTimeUnit()) {
            case DAYS -> createdAtDate.plusDays(duration);
            case WEEKS -> createdAtDate.plusWeeks(duration);
            case MONTHS -> createdAtDate.plusMonths(duration);
            case YEARS -> createdAtDate.plusYears(duration);
        };
    }

    @Override
    public boolean isClientMembershipActive(Integer clientId) {
        return clientMembershipRepository.findActiveMembershipByClientId(clientId) != null;
    }

    @Override
    public Integer getRemainingSessions(Integer clientId) {
        return getActiveMembershipOrThrow(clientId).getSessionsRemaining();
    }

    @Override
    @Transactional
    public Integer useSession(Integer clientId) {
        ClientMembershipDB membership = getActiveMembershipOrThrow(clientId);

        if (membership.getSessionsRemaining() == null || membership.getSessionsRemaining() <= 0) {
            throw new IllegalStateException("No remaining sessions.");
        }

        membership.setSessionsRemaining(membership.getSessionsRemaining() - 1);
        return membership.getSessionsRemaining();
    }

    private ClientMembershipDB getActiveMembershipOrThrow(Integer clientId) {
        ClientMembershipDB membership = clientMembershipRepository.findActiveMembershipByClientId(clientId);

        if (membership == null) {
            throw new EntityNotFoundException("No active membership found for client.");
        }

        if (membership.getExpiresAt() == null || membership.getExpiresAt().isBefore(LocalDate.now())) {
            throw new IllegalStateException("Membership expired.");
        }

        return membership;
    }
}