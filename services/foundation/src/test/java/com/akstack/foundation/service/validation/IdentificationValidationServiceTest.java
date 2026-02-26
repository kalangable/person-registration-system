package com.akstack.foundation.service.validation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class IdentificationValidationServiceTest {

    @Mock
    private List<IdentificationValidator> validators;

    @Mock
    private DefaultIdentificationValidator defaultValidator;

    @Mock
    private CpfValidator cpfValidator;

    @InjectMocks
    private IdentificationValidationService validationService;

    @Test
    void shouldValidateUsingSpecificValidator() {
        // Given
        String identificationType = "CPF";
        String identificationNumber = "12345678909";

        when(validators.stream()).thenReturn(java.util.stream.Stream.of(cpfValidator));
        when(cpfValidator.supports(identificationType)).thenReturn(true);
        when(cpfValidator.isValid(identificationNumber)).thenReturn(true);

        // When
        boolean result = validationService.validate(identificationType, identificationNumber);

        // Then
        assertThat(result).isTrue();
    }

    @Test
    void shouldValidateUsingDefaultValidatorWhenNoSpecificValidatorFound() {
        // Given
        String identificationType = "UNKNOWN";
        String identificationNumber = "123456789";

        when(validators.stream()).thenReturn(java.util.stream.Stream.of(cpfValidator));
        when(cpfValidator.supports(identificationType)).thenReturn(false);
        when(defaultValidator.isValid(identificationNumber)).thenReturn(true);

        // When
        boolean result = validationService.validate(identificationType, identificationNumber);

        // Then
        assertThat(result).isTrue();
    }

    @Test
    void shouldFormatUsingSpecificValidator() {
        // Given
        String identificationType = "CPF";
        String identificationNumber = "12345678909";
        String expectedFormatted = "123.456.789-09";

        when(validators.stream()).thenReturn(java.util.stream.Stream.of(cpfValidator));
        when(cpfValidator.supports(identificationType)).thenReturn(true);
        when(cpfValidator.format(identificationNumber)).thenReturn(expectedFormatted);

        // When
        String result = validationService.format(identificationType, identificationNumber);

        // Then
        assertThat(result).isEqualTo(expectedFormatted);
    }

    @Test
    void shouldFormatUsingDefaultValidatorWhenNoSpecificValidatorFound() {
        // Given
        String identificationType = "UNKNOWN";
        String identificationNumber = "123456789";
        String expectedFormatted = "123456789";

        when(validators.stream()).thenReturn(java.util.stream.Stream.of(cpfValidator));
        when(cpfValidator.supports(identificationType)).thenReturn(false);
        when(defaultValidator.format(identificationNumber)).thenReturn(expectedFormatted);

        // When
        String result = validationService.format(identificationType, identificationNumber);

        // Then
        assertThat(result).isEqualTo(expectedFormatted);
    }
}
