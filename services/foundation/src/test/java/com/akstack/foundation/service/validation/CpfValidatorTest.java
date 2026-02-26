package com.akstack.foundation.service.validation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import static org.assertj.core.api.Assertions.assertThat;

class CpfValidatorTest {

    private final CpfValidator validator = new CpfValidator();

    @Test
    void shouldSupportCpfIdentificationType() {
        // Then
        assertThat(validator.supports("CPF")).isTrue();
        assertThat(validator.supports("cpf")).isTrue();
        assertThat(validator.supports("Cpf")).isTrue();
    }

    @Test
    void shouldNotSupportOtherIdentificationTypes() {
        // Then
        assertThat(validator.supports("CNPJ")).isFalse();
        assertThat(validator.supports("SSN")).isFalse();
        assertThat(validator.supports(null)).isFalse();
        assertThat(validator.supports("")).isFalse();
    }

    @Test
    void shouldValidateValidCpf() {
        // Given
        String validCpf = "12345678909"; // Valid CPF for testing

        // When & Then
        assertThat(validator.isValid(validCpf)).isTrue();
    }

    @Test
    void shouldValidateValidCpfWithFormatting() {
        // Given
        String validCpf = "123.456.789-09";

        // When & Then
        assertThat(validator.isValid(validCpf)).isTrue();
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "11111111111", // All same digits
        "12345678901", // Invalid check digits
        "123456789",   // Too short
        "123456789012", // Too long
        "",            // Empty
        "abc123def456" // Non-numeric
    })
    void shouldInvalidateInvalidCpf(String invalidCpf) {
        // When & Then
        assertThat(validator.isValid(invalidCpf)).isFalse();
    }

    @Test
    void shouldReturnFalseForNullCpf() {
        // When & Then
        assertThat(validator.isValid(null)).isFalse();
    }

    @Test
    void shouldFormatCpf() {
        // Given
        String unformattedCpf = "12345678909";

        // When
        String formatted = validator.format(unformattedCpf);

        // Then
        assertThat(formatted).isEqualTo("123.456.789-09");
    }

    @Test
    void shouldFormatAlreadyFormattedCpf() {
        // Given
        String formattedCpf = "123.456.789-09";

        // When
        String result = validator.format(formattedCpf);

        // Then
        assertThat(result).isEqualTo("123.456.789-09");
    }

    @Test
    void shouldHandleNullInFormat() {
        // When & Then
        assertThat(validator.format(null)).isNull();
    }
}
