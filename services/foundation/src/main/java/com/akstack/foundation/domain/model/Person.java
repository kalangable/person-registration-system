package com.akstack.foundation.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

@Entity
@Table(name = "person")
@PrimaryKeyJoinColumn(name = "id")
@EqualsAndHashCode(callSuper = false)
@Data
public class Person extends Party {

    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;

    @Column(name = "middle_name", length = 100)
    private String middleName;

    @Column(name = "last_name", nullable = false, length = 100)
    private String lastName;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column(name = "primary_identification_type", length = 20)
    private String identificationType;

    @Column(name = "primary_identification_document", length = 50)
    private String identificationNumber;

    // Constructors
    public Person() {
        super(PartyType.PERSON);
    }

    public Person(String firstName, String lastName) {
        super(PartyType.PERSON);
        this.firstName = firstName;
        this.lastName = lastName;
    }

    // Business methods
    public String getFullName() {
        StringBuilder fullName = new StringBuilder();
        fullName.append(firstName);

        if (middleName != null && !middleName.isBlank()) {
            fullName.append(" ").append(middleName);
        }

        fullName.append(" ").append(lastName);
        return fullName.toString();
    }
}
