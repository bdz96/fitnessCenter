package com.example.FitnessCenterApp.repository;

import com.example.FitnessCenterApp.model.MembershipDB;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MembershipRepository extends JpaRepository<MembershipDB, Integer> {
}
