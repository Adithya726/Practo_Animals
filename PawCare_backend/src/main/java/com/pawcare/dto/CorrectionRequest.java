package com.pawcare.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CorrectionRequest {

    @NotNull(message = "Provider is required")
    private Long providerId;

    @NotBlank(message = "Choose what is wrong")
    @Pattern(regexp = "PHONE|ADDRESS|HOURS|FEE|SERVICES|CLOSED|OTHER", message = "Choose what is wrong")
    private String field;

    @NotBlank(message = "Tell us the correct details")
    @Size(min = 5, max = 500, message = "Details must be 5 to 500 characters")
    private String details;

    @Email(message = "Enter a valid email")
    @Size(max = 254, message = "Email is too long")
    private String reporterEmail;
}
