package com.akstack.foundation.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

@Entity
@Table(name = "organization")
@PrimaryKeyJoinColumn(name = "id")
@EqualsAndHashCode(callSuper = false)
@Data
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

    // Business methods
    public String getDisplayName() {
        return tradeName != null && !tradeName.isBlank() ? tradeName : legalName;
    }
}
