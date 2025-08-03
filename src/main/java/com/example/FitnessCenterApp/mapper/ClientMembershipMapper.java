package com.example.FitnessCenterApp.mapper;

import com.example.FitnessCenterApp.controller.clientMembership.ClientMembershipDto;
import com.example.FitnessCenterApp.model.ClientDB;
import com.example.FitnessCenterApp.model.ClientMembershipDB;
import com.example.FitnessCenterApp.model.MembershipDB;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.LocalDate;

@Mapper(componentModel = "spring", uses = {ClientMapper.class, MembershipMapper.class})
public interface ClientMembershipMapper {

    @Mapping(source = "clientDB", target = "client")
    @Mapping(source = "membershipDB", target = "membership")
    ClientMembershipDto fromDB(ClientMembershipDB clientMembershipDB);

    @Mapping(target = "id", ignore = true)
    @Mapping(source = "client", target = "clientDB")
    @Mapping(source = "membership", target = "membershipDB")
    ClientMembershipDB toDB(ClientMembershipDto clientMembershipDto);

    default ClientMembershipDB toDB(ClientDB client, MembershipDB membership, LocalDate createdAt, LocalDate expiresAt) {
        ClientMembershipDB clientMembershipDB = new ClientMembershipDB();
        clientMembershipDB.setClientDB(client);
        clientMembershipDB.setMembershipDB(membership);
        clientMembershipDB.setCreatedAt(createdAt);
        clientMembershipDB.setExpiresAt(expiresAt);
        clientMembershipDB.setSessionsRemaining(membership.getSessionsAvailable());
        return clientMembershipDB;
    }
}