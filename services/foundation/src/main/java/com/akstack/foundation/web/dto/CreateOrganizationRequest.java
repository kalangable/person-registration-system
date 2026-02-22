package com.akstack.foundation.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

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

    // Getters and Setters
    public String getLegalName() {
        return legalName;
    }

    public void setLegalName(String legalName) {
        this.legalName = legalName;
    }

    public String getTradeName() {
        return tradeName;
    }

    public void setTradeName(String tradeName) {
        this.tradeName = tradeName;
    }

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public LocalDate getFoundingDate() {
        return foundingDate;
    }

    public void setFoundingDate(LocalDate foundingDate) {
        this.foundingDate = foundingDate;
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
}
