package com.pawcare.dto;

import com.pawcare.model.LeadType;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class LeadRequest {

    @NotBlank(message = "Name is required")
    @Size(max = 80, message = "Name is too long")
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Enter a valid email")
    @Size(max = 254, message = "Email is too long")
    private String email;

    @NotBlank(message = "Phone is required")
    @Pattern(regexp = "^[+]?[0-9 -]{8,16}$", message = "Enter a valid phone number")
    private String phone;

    @NotBlank(message = "Tell us if you are a student or a professional")
    @Pattern(regexp = "STUDENT|PROFESSIONAL", message = "Choose student or professional")
    private String background;

    /** Optional for an enquiry, required for a registration. */
    private Long courseId;

    @NotNull(message = "Lead type is required")
    private LeadType type;

    @Size(max = 500, message = "Keep the message under 500 characters")
    private String message;

    @Size(max = 40, message = "Source is too long")
    private String source;
}
