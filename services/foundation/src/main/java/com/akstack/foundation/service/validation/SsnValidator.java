package com.akstack.foundation.service.validation;

import org.springframework.stereotype.Component;

@Component
public class SsnValidator implements IdentificationValidator {

    @Override
    public boolean supports(String identificationType) {
        return "SSN".equalsIgnoreCase(identificationType);
    }

    @Override
    public boolean isValid(String identificationNumber) {
        if (identificationNumber == null) {
            return false;
        }

        // Remove formatting
        String ssn = identificationNumber.replaceAll("[^0-9]", "");

        // Check length
        if (ssn.length() != 9) {
            return false;
        }

        // Check for known invalid SSNs
        // Area number cannot be 000, 666, or 900-999
        int area = Integer.parseInt(ssn.substring(0, 3));
        if (area == 0 || area == 666 || area >= 900) {
            return false;
        }

        // Group number cannot be 00
        int group = Integer.parseInt(ssn.substring(3, 5));
        if (group == 0) {
            return false;
        }

        // Serial number cannot be 0000
        int serial = Integer.parseInt(ssn.substring(5, 9));
        if (serial == 0) {
            return false;
        }

        return true;
    }

    @Override
    public String format(String identificationNumber) {
        if (identificationNumber == null) {
            return null;
        }
        String ssn = identificationNumber.replaceAll("[^0-9]", "");
        if (ssn.length() != 9) {
            return identificationNumber;
        }
        return ssn.replaceAll("(\\d{3})(\\d{2})(\\d{4})", "$1-$2-$3");
    }
}
