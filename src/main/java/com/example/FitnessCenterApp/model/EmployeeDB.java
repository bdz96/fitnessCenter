package com.example.FitnessCenterApp.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Date;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@DiscriminatorValue("2")
public class EmployeeDB extends UserDB {
    @Column(name = "hire_date")
    private Date hireDate;
    @Column(name = "termination_date")
    private Date terminationDate;
}