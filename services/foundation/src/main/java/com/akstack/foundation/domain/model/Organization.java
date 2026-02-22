package com.akstack.foundation.domain.model;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "organization")
@PrimaryKeyJoinColumn(name = "id")
public class Organization extends Party {

    @Column(name = "legal_name", nullable = false, length = 200)
    private String legalName;

    @Column(name = "trade_name", length = 200)
    private String tradeName;

    @Column(name = "brand_name", length = 200)
    private String brandName;

    @Column(name = "founding_date")
    private LocalDate foundingDate;

    @Column(name = "primary_identification_type", length = 20)
    private String identificationType;

    @Column(name = "primary_identification_document", length = 50)
    private String identificationNumber;

    // Constructors
    public Organization() {
        super(PartyType.ORGANIZATION);
    }

    public Organization(String legalName) {
        super(PartyType.ORGANIZATION);
        this.legalName = legalName;
    }

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

    // Business methods
    public String getDisplayName() {
        return tradeName != null && !tradeName.isBlank() ? tradeName : legalName;
    }
}
