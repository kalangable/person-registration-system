package com.akstack.foundation.domain.repository;

import com.akstack.foundation.domain.model.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PersonRepository extends JpaRepository<Person, Long> {

    // Find all persons excluding deleted
    @Query("SELECT p FROM Person p WHERE p.isDeleted = false")
    List<Person> findAllNotDeleted();

    // Find by identification
    @Query("SELECT p FROM Person p WHERE p.identificationType = :identificationType AND p.identificationNumber = :identificationNumber AND p.isDeleted = false")
    Optional<Person> findByIdentification(@Param("identificationType") String identificationType, 
                                          @Param("identificationNumber") String identificationNumber);

    // Find by first and last name
    @Query("SELECT p FROM Person p WHERE p.firstName = :firstName AND p.lastName = :lastName AND p.isDeleted = false")
    List<Person> findByFirstNameAndLastNameAndNotDeleted(@Param("firstName") String firstName, 
                                                          @Param("lastName") String lastName);

    // Check if identification exists
    @Query("SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END FROM Person p WHERE p.identificationType = :identificationType AND p.identificationNumber = :identificationNumber AND p.isDeleted = false")
    boolean existsByIdentificationAndNotDeleted(@Param("identificationType") String identificationType, 
                                                 @Param("identificationNumber") String identificationNumber);
}
