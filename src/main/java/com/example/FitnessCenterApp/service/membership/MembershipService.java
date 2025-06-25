package com.example.FitnessCenterApp.service.membership;

import com.example.FitnessCenterApp.controller.membership.CreateMembershipRequest;
import com.example.FitnessCenterApp.controller.membership.MembershipDto;

import java.util.List;

public interface MembershipService {
    List<MembershipDto> getAllMemberships();

    MembershipDto saveMembership(CreateMembershipRequest createMembershipRequest);
}
