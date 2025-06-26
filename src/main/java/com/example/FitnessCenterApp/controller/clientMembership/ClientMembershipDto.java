package com.example.FitnessCenterApp.controller.clientMembership;

import com.example.FitnessCenterApp.controller.client.ClientDto;
import com.example.FitnessCenterApp.controller.membership.MembershipDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ClientMembershipDto {
    private ClientDto client;
    private MembershipDto membership;
    private Date createdAt;
    private Date expiresAt;
    private Integer sessionsRemaining;
}
