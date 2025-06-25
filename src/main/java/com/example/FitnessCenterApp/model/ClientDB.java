package com.example.FitnessCenterApp.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@DiscriminatorValue("1")
public class ClientDB extends UserDB {
    @OneToMany(mappedBy = "clientDB", cascade = CascadeType.ALL)
    private List<ClientMembershipDB> memberships = new ArrayList<>();
}
