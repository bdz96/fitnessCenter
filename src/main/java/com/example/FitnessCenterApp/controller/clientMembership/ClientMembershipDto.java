package com.example.FitnessCenterApp.controller.clientMembership;

import com.example.FitnessCenterApp.controller.client.ClientDto;
import com.example.FitnessCenterApp.controller.membership.MembershipDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ClientMembershipDto {
    private ClientDto client;
    private MembershipDto membership;
    private LocalDate  createdAt;
    private LocalDate expiresAt;
    private Integer sessionsRemaining;
}
