package com.example.FitnessCenterApp.mapper;

import com.example.FitnessCenterApp.controller.membership.CreateMembershipRequest;
import com.example.FitnessCenterApp.controller.membership.MembershipDto;
import com.example.FitnessCenterApp.model.MembershipDB;

public class MembershipMapper {

    public static MembershipDto fromDB(MembershipDB membershipDB) {
        MembershipDto membershipDto = new MembershipDto();
        membershipDto.setName(membershipDB.getName());
        membershipDto.setPrice(membershipDB.getPrice());
        membershipDto.setDuration(membershipDB.getDuration());
        membershipDto.setTimeUnit(membershipDB.getTimeUnit());
        membershipDto.setSessionsAvailable(membershipDB.getSessionsAvailable());

        return membershipDto;
    }

    public static MembershipDB toDB(MembershipDto membershipDto) {
        MembershipDB membershipDB = new MembershipDB();
        membershipDB.setName(membershipDto.getName());
        membershipDB.setPrice(membershipDto.getPrice());
        membershipDB.setDuration(membershipDto.getDuration());
        membershipDB.setTimeUnit(membershipDto.getTimeUnit());
        membershipDB.setSessionsAvailable(membershipDto.getSessionsAvailable());

        return membershipDB;
    }

    public static MembershipDB toDB(CreateMembershipRequest createMembershipRequest) {
        MembershipDB membershipDB = new MembershipDB();
        membershipDB.setName(createMembershipRequest.getName());
        membershipDB.setPrice(createMembershipRequest.getPrice());
        membershipDB.setDuration(createMembershipRequest.getDuration());
        membershipDB.setTimeUnit(createMembershipRequest.getTimeUnit());
        membershipDB.setSessionsAvailable(createMembershipRequest.getSessionsAvailable());

        return membershipDB;
    }
}
