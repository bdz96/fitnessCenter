package com.example.FitnessCenterApp.repository;

import com.example.FitnessCenterApp.model.ClientDB;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClientRepository extends JpaRepository<ClientDB, Integer> {
}