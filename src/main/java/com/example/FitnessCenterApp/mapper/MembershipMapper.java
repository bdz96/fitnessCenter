package com.example.FitnessCenterApp.mapper;

import com.example.FitnessCenterApp.controller.membership.CreateMembershipRequest;
import com.example.FitnessCenterApp.controller.membership.MembershipDto;
import com.example.FitnessCenterApp.model.MembershipDB;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface MembershipMapper {

    MembershipDto fromDB(MembershipDB membershipDB);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "clients", ignore = true)
    MembershipDB toDB(MembershipDto membershipDto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "clients", ignore = true)
    MembershipDB toDB(CreateMembershipRequest createMembershipRequest);
}
