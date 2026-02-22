package com.akstack.foundation.service.validation;

import org.springframework.stereotype.Component;

@Component
public class EinValidator implements IdentificationValidator {

    @Override
    public boolean supports(String identificationType) {
        return "EIN".equalsIgnoreCase(identificationType);
    }

    @Override
    public boolean isValid(String identificationNumber) {
        if (identificationNumber == null) {
            return false;
        }

        // Remove formatting
        String ein = identificationNumber.replaceAll("[^0-9]", "");

        // Check length
        if (ein.length() != 9) {
            return false;
        }

        // Check for valid prefix (first two digits)
        // Valid EIN prefixes range from 01-99 (excluding some reserved ranges)
        int prefix = Integer.parseInt(ein.substring(0, 2));
        if (prefix == 0 || prefix == 7 || prefix == 8 || prefix == 9 || 
            prefix == 17 || prefix == 18 || prefix == 19 || prefix == 28 || 
            prefix == 29 || prefix == 49 || prefix >= 80) {
            return false;
        }

        return true;
    }

    @Override
    public String format(String identificationNumber) {
        if (identificationNumber == null) {
            return null;
        }
        String ein = identificationNumber.replaceAll("[^0-9]", "");
        if (ein.length() != 9) {
            return identificationNumber;
        }
        return ein.replaceAll("(\\d{2})(\\d{7})", "$1-$2");
    }
}
