package com.example.FitnessCenterApp.mapper;

import com.example.FitnessCenterApp.controller.clientMembership.ClientMembershipDto;
import com.example.FitnessCenterApp.model.ClientDB;
import com.example.FitnessCenterApp.model.ClientMembershipDB;
import com.example.FitnessCenterApp.model.MembershipDB;

import java.sql.Date;
import java.time.LocalDate;

public class ClientMembershipMapper {

    public static ClientMembershipDto fromDB(ClientMembershipDB clientMembershipDB) {
        ClientMembershipDto clientMembershipDto = new ClientMembershipDto();

        clientMembershipDto.setClient(ClientMapper.fromDB(clientMembershipDB.getClientDB()));
        clientMembershipDto.setMembership(MembershipMapper.fromDB(clientMembershipDB.getMembershipDB()));
        clientMembershipDto.setCreatedAt(Date.valueOf(clientMembershipDB.getCreatedAt()));
        clientMembershipDto.setExpiresAt(Date.valueOf(clientMembershipDB.getExpiresAt()));
        clientMembershipDto.setSessionsRemaining(clientMembershipDB.getSessionsRemaining());
        return clientMembershipDto;
    }

    public static ClientMembershipDB toDB(ClientMembershipDto clientMembershipDto) {
        ClientMembershipDB clientMembershipDB = new ClientMembershipDB();

        clientMembershipDB.setClientDB(ClientMapper.toDB(clientMembershipDto.getClient()));
        clientMembershipDB.setMembershipDB(MembershipMapper.toDB(clientMembershipDto.getMembership()));
        clientMembershipDB.setCreatedAt(clientMembershipDto.getCreatedAt().toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate());
        clientMembershipDB.setExpiresAt(clientMembershipDto.getExpiresAt().toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate());
        clientMembershipDB.setSessionsRemaining(clientMembershipDto.getSessionsRemaining());

        return clientMembershipDB;
    }

    public static ClientMembershipDB toDB(ClientDB client, MembershipDB membership, LocalDate createdAt, LocalDate expiresAt) {
        ClientMembershipDB clientMembershipDB = new ClientMembershipDB();

        clientMembershipDB.setClientDB(client);
        clientMembershipDB.setMembershipDB(membership);
        clientMembershipDB.setCreatedAt(createdAt);
        clientMembershipDB.setExpiresAt(expiresAt);
        clientMembershipDB.setSessionsRemaining(membership.getSessionsAvailable());

        return clientMembershipDB;
    }

}