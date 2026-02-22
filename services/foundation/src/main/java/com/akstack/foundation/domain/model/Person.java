package com.akstack.foundation.domain.model;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "person")
@PrimaryKeyJoinColumn(name = "id")
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

    // Getters and Setters
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getIdentificationType() {
        return identificationType;
    }

    public void setIdentificationType(String identificationType) {
        this.identificationType = identificationType;
    }

    public String getIdentificationNumber() {
        return identificationNumber;
    }

    public void setIdentificationNumber(String identificationNumber) {
        this.identificationNumber = identificationNumber;
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
