package com.akstack.foundation.service.validation;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
@AllArgsConstructor
@Service
public class IdentificationValidationService {

    private final List<IdentificationValidator> validators;
    private final DefaultIdentificationValidator defaultValidator;

    public boolean validate(String identificationType, String identificationNumber) {
        IdentificationValidator validator = findValidator(identificationType);
        return validator.isValid(identificationNumber);
    }

    public String format(String identificationType, String identificationNumber) {
        IdentificationValidator validator = findValidator(identificationType);
        return validator.format(identificationNumber);
    }

    private IdentificationValidator findValidator(String identificationType) {
        return validators.stream()
                .filter(v -> !(v instanceof DefaultIdentificationValidator))
                .filter(v -> v.supports(identificationType))
                .findFirst()
                .orElse(defaultValidator);
    }
}
