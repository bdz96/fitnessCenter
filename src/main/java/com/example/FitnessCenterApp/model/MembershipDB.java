package com.example.FitnessCenterApp.model;

import com.example.FitnessCenterApp.model.enums.TimeUnit;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "memberships")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MembershipDB {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "name")
    private String name;
    @Column(name = "price")
    private double price;
    @Column(name = "duration")
    private String duration;
    @Column(name = "time_unit")
    @Enumerated(EnumType.STRING)
    private TimeUnit timeUnit;
    @Column(name = "sessions_available")
    private Integer sessionsAvailable;

    @OneToMany(mappedBy = "membershipDB", cascade = CascadeType.ALL)
    private List<ClientMembershipDB> clients = new ArrayList<>();
}
