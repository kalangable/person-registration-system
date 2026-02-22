package com.akstack.foundation.service.validation;

import org.springframework.stereotype.Component;

@Component
public class DefaultIdentificationValidator implements IdentificationValidator {

    @Override
    public boolean supports(String identificationType) {
        // This is the fallback validator, it supports all types
        return true;
    }

    @Override
    public boolean isValid(String identificationNumber) {
        // Basic validation: not null and not blank
        return identificationNumber != null && !identificationNumber.isBlank();
    }

    @Override
    public String format(String identificationNumber) {
        // No formatting for unknown types
        return identificationNumber;
    }
}
