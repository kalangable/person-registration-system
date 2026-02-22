package com.akstack.foundation.service.validation;

import org.springframework.stereotype.Component;

@Component
public class CpfValidator implements IdentificationValidator {

    @Override
    public boolean supports(String identificationType) {
        return "CPF".equalsIgnoreCase(identificationType);
    }

    @Override
    public boolean isValid(String identificationNumber) {
        if (identificationNumber == null) {
            return false;
        }

        // Remove formatting
        String cpf = identificationNumber.replaceAll("[^0-9]", "");

        // Check length
        if (cpf.length() != 11) {
            return false;
        }

        // Check for known invalid CPFs (all digits the same)
        if (cpf.matches("(\\d)\\1{10}")) {
            return false;
        }

        // Validate check digits
        try {
            // First check digit
            int sum = 0;
            for (int i = 0; i < 9; i++) {
                sum += Character.getNumericValue(cpf.charAt(i)) * (10 - i);
            }
            int firstCheckDigit = 11 - (sum % 11);
            if (firstCheckDigit >= 10) firstCheckDigit = 0;

            if (Character.getNumericValue(cpf.charAt(9)) != firstCheckDigit) {
                return false;
            }

            // Second check digit
            sum = 0;
            for (int i = 0; i < 10; i++) {
                sum += Character.getNumericValue(cpf.charAt(i)) * (11 - i);
            }
            int secondCheckDigit = 11 - (sum % 11);
            if (secondCheckDigit >= 10) secondCheckDigit = 0;

            return Character.getNumericValue(cpf.charAt(10)) == secondCheckDigit;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public String format(String identificationNumber) {
        if (identificationNumber == null) {
            return null;
        }
        String cpf = identificationNumber.replaceAll("[^0-9]", "");
        if (cpf.length() != 11) {
            return identificationNumber;
        }
        return cpf.replaceAll("(\\d{3})(\\d{3})(\\d{3})(\\d{2})", "$1.$2.$3-$4");
    }
}
