package com.akstack.foundation.domain.repository;

import com.akstack.foundation.domain.model.Organization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrganizationRepository extends JpaRepository<Organization, Long> {

    // Find all organizations excluding deleted
    @Query("SELECT o FROM Organization o WHERE o.isDeleted = false")
    List<Organization> findAllNotDeleted();

    // Find by identification
    @Query("SELECT o FROM Organization o WHERE o.identificationType = :identificationType AND o.identificationNumber = :identificationNumber AND o.isDeleted = false")
    Optional<Organization> findByIdentification(@Param("identificationType") String identificationType, 
                                                 @Param("identificationNumber") String identificationNumber);

    // Find by legal name
    @Query("SELECT o FROM Organization o WHERE o.legalName = :legalName AND o.isDeleted = false")
    List<Organization> findByLegalNameAndNotDeleted(@Param("legalName") String legalName);

    // Find by trade name
    @Query("SELECT o FROM Organization o WHERE o.tradeName = :tradeName AND o.isDeleted = false")
    List<Organization> findByTradeNameAndNotDeleted(@Param("tradeName") String tradeName);

    // Check if identification exists
    @Query("SELECT CASE WHEN COUNT(o) > 0 THEN true ELSE false END FROM Organization o WHERE o.identificationType = :identificationType AND o.identificationNumber = :identificationNumber AND o.isDeleted = false")
    boolean existsByIdentificationAndNotDeleted(@Param("identificationType") String identificationType, 
                                                 @Param("identificationNumber") String identificationNumber);
}
