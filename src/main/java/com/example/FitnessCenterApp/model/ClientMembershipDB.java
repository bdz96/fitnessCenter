package com.example.FitnessCenterApp.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Date;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ClientMembershipDB {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "client_id")
    private ClientDB clientDB;

    @ManyToOne
    @JoinColumn(name = "membership_id")
    private MembershipDB membershipDB;

    private LocalDate createdAt;
    private LocalDate expiresAt; // change u date kad bude logika, dok je testno ok
}
