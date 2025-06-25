package com.example.FitnessCenterApp.controller.clientMembership;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateClientMembershipRequest {
    private Integer clientId;
    private Integer membershipId;

}
