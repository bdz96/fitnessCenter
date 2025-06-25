package com.example.FitnessCenterApp.repository;

import com.example.FitnessCenterApp.model.ClientMembershipDB;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ClientMembershipRepository extends JpaRepository<ClientMembershipDB, Integer> {

    @Query("SELECT cm FROM ClientMembershipDB cm WHERE cm.clientDB.id = :clientId AND cm.expiresAt >= CURRENT_DATE")
    ClientMembershipDB findActiveMembershipByClientId(@Param("clientId") Integer clientId);
}
