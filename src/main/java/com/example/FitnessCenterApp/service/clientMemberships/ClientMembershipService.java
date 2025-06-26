package com.example.FitnessCenterApp.service.clientMemberships;

import com.example.FitnessCenterApp.controller.clientMembership.ClientMembershipDto;
import com.example.FitnessCenterApp.controller.clientMembership.CreateClientMembershipRequest;

public interface ClientMembershipService {

    ClientMembershipDto assignMembershipToClient(CreateClientMembershipRequest createClientMembershipRequest);

    boolean isClientMembershipActive(Integer clientId);

    Integer getRemainingSessions(Integer clientId);

    Integer useSession(Integer clientId);

}
