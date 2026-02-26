package com.akstack.foundation.domain.model;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;

class OrganizationTest {

    @Test
    void shouldCreateOrganizationWithRequiredFields() {
        // Given
        String legalName = "Empresa XYZ Ltda";

        // When
        Organization organization = new Organization(legalName);

        // Then
        assertThat(organization.getLegalName()).isEqualTo(legalName);
        assertThat(organization.getPartyType()).isEqualTo(PartyType.ORGANIZATION);
        assertThat(organization.getIsActive()).isTrue();
        assertThat(organization.getIsDeleted()).isFalse();
    }

    @Test
    void shouldCreateOrganizationWithAllFields() {
        // Given
        String legalName = "Empresa XYZ Ltda";
        String tradeName = "XYZ Corp";
        String brandName = "XYZ";
        LocalDate foundingDate = LocalDate.of(2020, 1, 1);
        String identificationType = "CNPJ";
        String identificationNumber = "12345678000123";

        // When
        Organization organization = new Organization();
        organization.setLegalName(legalName);
        organization.setTradeName(tradeName);
        organization.setBrandName(brandName);
        organization.setFoundingDate(foundingDate);
        organization.setIdentificationType(identificationType);
        organization.setIdentificationNumber(identificationNumber);

        // Then
        assertThat(organization.getLegalName()).isEqualTo(legalName);
        assertThat(organization.getTradeName()).isEqualTo(tradeName);
        assertThat(organization.getBrandName()).isEqualTo(brandName);
        assertThat(organization.getFoundingDate()).isEqualTo(foundingDate);
        assertThat(organization.getIdentificationType()).isEqualTo(identificationType);
        assertThat(organization.getIdentificationNumber()).isEqualTo(identificationNumber);
    }

    @Test
    void shouldReturnTradeNameAsDisplayNameWhenPresent() {
        // Given
        Organization organization = new Organization("Empresa XYZ Ltda");
        organization.setTradeName("XYZ Corp");

        // When
        String displayName = organization.getDisplayName();

        // Then
        assertThat(displayName).isEqualTo("XYZ Corp");
    }

    @Test
    void shouldReturnLegalNameAsDisplayNameWhenTradeNameIsNull() {
        // Given
        Organization organization = new Organization("Empresa XYZ Ltda");
        organization.setTradeName(null);

        // When
        String displayName = organization.getDisplayName();

        // Then
        assertThat(displayName).isEqualTo("Empresa XYZ Ltda");
    }

    @Test
    void shouldReturnLegalNameAsDisplayNameWhenTradeNameIsEmpty() {
        // Given
        Organization organization = new Organization("Empresa XYZ Ltda");
        organization.setTradeName("");

        // When
        String displayName = organization.getDisplayName();

        // Then
        assertThat(displayName).isEqualTo("Empresa XYZ Ltda");
    }

    @Test
    void shouldReturnLegalNameAsDisplayNameWhenTradeNameIsBlank() {
        // Given
        Organization organization = new Organization("Empresa XYZ Ltda");
        organization.setTradeName("   ");

        // When
        String displayName = organization.getDisplayName();

        // Then
        assertThat(displayName).isEqualTo("Empresa XYZ Ltda");
    }
}
