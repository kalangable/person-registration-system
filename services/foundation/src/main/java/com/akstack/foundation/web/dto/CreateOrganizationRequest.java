package com.akstack.foundation.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

@Data

public class CreateOrganizationRequest {

    @NotBlank(message = "Legal name is required")
    @Size(max = 200, message = "Legal name must not exceed 200 characters")
    private String legalName;

    @Size(max = 200, message = "Trade name must not exceed 200 characters")
    private String tradeName;

    @Size(max = 200, message = "Brand name must not exceed 200 characters")
    private String brandName;

    private LocalDate foundingDate;

    @Size(max = 50, message = "Identification type must not exceed 50 characters")
    private String identificationType;

    @Size(max = 100, message = "Identification number must not exceed 100 characters")
    private String identificationNumber;

}
