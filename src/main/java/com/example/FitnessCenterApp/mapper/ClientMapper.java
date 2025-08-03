package com.example.FitnessCenterApp.mapper;

import com.example.FitnessCenterApp.controller.client.ClientDto;
import com.example.FitnessCenterApp.controller.client.CreateClientRequest;
import com.example.FitnessCenterApp.model.ClientDB;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ClientMapper {

    ClientDto fromDB(ClientDB client);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "memberships", ignore = true)
    ClientDB toDB(ClientDto clientDto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "memberships", ignore = true)
    ClientDB toDB(CreateClientRequest createClientRequest);
}