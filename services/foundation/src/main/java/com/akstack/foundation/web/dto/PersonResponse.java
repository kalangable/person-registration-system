package com.akstack.foundation.web.dto;

import com.akstack.foundation.domain.model.PartyType;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class PersonResponse {

    private Long id;
    private String partyNumber;
    private PartyType partyType;
    private String firstName;
    private String middleName;
    private String lastName;
    private String fullName;
    private LocalDate dateOfBirth;
    private String identificationType;
    private String identificationNumber;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
