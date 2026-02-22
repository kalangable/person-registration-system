package com.akstack.foundation.service.validation;

import org.springframework.stereotype.Component;

@Component
public class CnpjValidator implements IdentificationValidator {

    @Override
    public boolean supports(String identificationType) {
        return "CNPJ".equalsIgnoreCase(identificationType);
    }

    @Override
    public boolean isValid(String identificationNumber) {
        if (identificationNumber == null) {
            return false;
        }

        // Remove formatting
        String cnpj = identificationNumber.replaceAll("[^0-9]", "");

        // Check length
        if (cnpj.length() != 14) {
            return false;
        }

        // Check for known invalid CNPJs (all digits the same)
        if (cnpj.matches("(\\d)\\1{13}")) {
            return false;
        }

        // Validate check digits
        try {
            // First check digit
            int[] weights1 = {5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};
            int sum = 0;
            for (int i = 0; i < 12; i++) {
                sum += Character.getNumericValue(cnpj.charAt(i)) * weights1[i];
            }
            int firstCheckDigit = sum % 11;
            firstCheckDigit = firstCheckDigit < 2 ? 0 : 11 - firstCheckDigit;

            if (Character.getNumericValue(cnpj.charAt(12)) != firstCheckDigit) {
                return false;
            }

            // Second check digit
            int[] weights2 = {6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};
            sum = 0;
            for (int i = 0; i < 13; i++) {
                sum += Character.getNumericValue(cnpj.charAt(i)) * weights2[i];
            }
            int secondCheckDigit = sum % 11;
            secondCheckDigit = secondCheckDigit < 2 ? 0 : 11 - secondCheckDigit;

            return Character.getNumericValue(cnpj.charAt(13)) == secondCheckDigit;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public String format(String identificationNumber) {
        if (identificationNumber == null) {
            return null;
        }
        String cnpj = identificationNumber.replaceAll("[^0-9]", "");
        if (cnpj.length() != 14) {
            return identificationNumber;
        }
        return cnpj.replaceAll("(\\d{2})(\\d{3})(\\d{3})(\\d{4})(\\d{2})", "$1.$2.$3/$4-$5");
    }
}
