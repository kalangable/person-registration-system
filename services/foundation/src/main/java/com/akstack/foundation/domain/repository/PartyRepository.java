package com.akstack.foundation.domain.repository;

import com.akstack.foundation.domain.model.Party;
import com.akstack.foundation.domain.model.PartyType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PartyRepository extends JpaRepository<Party, Long> {

    // Find by party number
    Optional<Party> findByPartyNumber(String partyNumber);

    // Find by party number excluding deleted
    @Query("SELECT p FROM Party p WHERE p.partyNumber = :partyNumber AND p.isDeleted = false")
    Optional<Party> findByPartyNumberAndNotDeleted(@Param("partyNumber") String partyNumber);

    // Find all active parties (not deleted)
    @Query("SELECT p FROM Party p WHERE p.isDeleted = false")
    List<Party> findAllNotDeleted();

    // Find all active parties by type
    @Query("SELECT p FROM Party p WHERE p.partyType = :partyType AND p.isDeleted = false")
    List<Party> findByPartyTypeAndNotDeleted(@Param("partyType") PartyType partyType);

    // Find all active and inactive parties
    @Query("SELECT p FROM Party p WHERE p.isActive = :isActive AND p.isDeleted = false")
    List<Party> findByIsActiveAndNotDeleted(@Param("isActive") Boolean isActive);

    // Check if party number exists
    boolean existsByPartyNumber(String partyNumber);

    // Check if party number exists excluding deleted
    @Query("SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END FROM Party p WHERE p.partyNumber = :partyNumber AND p.isDeleted = false")
    boolean existsByPartyNumberAndNotDeleted(@Param("partyNumber") String partyNumber);
}
