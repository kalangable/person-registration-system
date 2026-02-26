package com.akstack.foundation.domain.model;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;

class PersonTest {

    @Test
    void shouldCreatePersonWithRequiredFields() {
        // Given
        String firstName = "João";
        String lastName = "Silva";

        // When
        Person person = new Person(firstName, lastName);

        // Then
        assertThat(person.getFirstName()).isEqualTo(firstName);
        assertThat(person.getLastName()).isEqualTo(lastName);
        assertThat(person.getPartyType()).isEqualTo(PartyType.PERSON);
        assertThat(person.getIsActive()).isTrue();
        assertThat(person.getIsDeleted()).isFalse();
    }

    @Test
    void shouldCreatePersonWithAllFields() {
        // Given
        String firstName = "João";
        String middleName = "Carlos";
        String lastName = "Silva";
        LocalDate dateOfBirth = LocalDate.of(1990, 1, 15);
        String identificationType = "CPF";
        String identificationNumber = "12345678901";

        // When
        Person person = new Person();
        person.setFirstName(firstName);
        person.setMiddleName(middleName);
        person.setLastName(lastName);
        person.setDateOfBirth(dateOfBirth);
        person.setIdentificationType(identificationType);
        person.setIdentificationNumber(identificationNumber);

        // Then
        assertThat(person.getFirstName()).isEqualTo(firstName);
        assertThat(person.getMiddleName()).isEqualTo(middleName);
        assertThat(person.getLastName()).isEqualTo(lastName);
        assertThat(person.getDateOfBirth()).isEqualTo(dateOfBirth);
        assertThat(person.getIdentificationType()).isEqualTo(identificationType);
        assertThat(person.getIdentificationNumber()).isEqualTo(identificationNumber);
    }

    @Test
    void shouldGenerateFullNameWithoutMiddleName() {
        // Given
        Person person = new Person("João", "Silva");

        // When
        String fullName = person.getFullName();

        // Then
        assertThat(fullName).isEqualTo("João Silva");
    }

    @Test
    void shouldGenerateFullNameWithMiddleName() {
        // Given
        Person person = new Person();
        person.setFirstName("João");
        person.setMiddleName("Carlos");
        person.setLastName("Silva");

        // When
        String fullName = person.getFullName();

        // Then
        assertThat(fullName).isEqualTo("João Carlos Silva");
    }

    @Test
    void shouldHandleNullMiddleNameInFullName() {
        // Given
        Person person = new Person();
        person.setFirstName("João");
        person.setMiddleName(null);
        person.setLastName("Silva");

        // When
        String fullName = person.getFullName();

        // Then
        assertThat(fullName).isEqualTo("João Silva");
    }

    @Test
    void shouldHandleEmptyMiddleNameInFullName() {
        // Given
        Person person = new Person();
        person.setFirstName("João");
        person.setMiddleName("");
        person.setLastName("Silva");

        // When
        String fullName = person.getFullName();

        // Then
        assertThat(fullName).isEqualTo("João Silva");
    }

    @Test
    void shouldHandleBlankMiddleNameInFullName() {
        // Given
        Person person = new Person();
        person.setFirstName("João");
        person.setMiddleName("   ");
        person.setLastName("Silva");

        // When
        String fullName = person.getFullName();

        // Then
        assertThat(fullName).isEqualTo("João Silva");
    }
}
