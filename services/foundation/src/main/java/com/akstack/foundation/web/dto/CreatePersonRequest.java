package com.akstack.foundation.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

@Data
public class CreatePersonRequest {

    @NotBlank(message = "First name is required")
    @Size(max = 100, message = "First name must not exceed 100 characters")
    private String firstName;

    @Size(max = 100, message = "Middle name must not exceed 100 characters")
    private String middleName;

    @NotBlank(message = "Last name is required")
    @Size(max = 100, message = "Last name must not exceed 100 characters")
    private String lastName;

    private LocalDate dateOfBirth;

    @Size(max = 50, message = "Identification type must not exceed 50 characters")
    private String identificationType;

    @Size(max = 100, message = "Identification number must not exceed 100 characters")
    private String identificationNumber;

}
