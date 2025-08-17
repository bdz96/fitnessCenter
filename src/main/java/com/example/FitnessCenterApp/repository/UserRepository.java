package com.example.FitnessCenterApp.repository;

import com.example.FitnessCenterApp.model.UserDB;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserDB, Integer> {

    Optional<UserDB> findByEmail(String email);
}
