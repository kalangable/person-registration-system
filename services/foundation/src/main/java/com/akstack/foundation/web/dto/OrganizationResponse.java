package com.akstack.foundation.web.dto;

import com.akstack.foundation.domain.model.PartyType;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class OrganizationResponse {

    private Long id;
    private String partyNumber;
    private PartyType partyType;
    private String legalName;
    private String tradeName;
    private String brandName;
    private LocalDate foundingDate;
    private String identificationType;
    private String identificationNumber;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
