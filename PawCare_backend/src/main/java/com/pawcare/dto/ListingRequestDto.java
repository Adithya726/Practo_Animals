package com.pawcare.dto;

import com.pawcare.model.ProviderCategory;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ListingRequestDto {

    @NotBlank(message = "Business name is required")
    @Size(max = 120, message = "Business name is too long")
    private String businessName;

    @NotNull(message = "Choose a category")
    private ProviderCategory category;

    @NotBlank(message = "City is required")
    @Size(max = 60, message = "City is too long")
    private String city;

    @NotBlank(message = "Area is required")
    @Size(max = 80, message = "Area is too long")
    private String area;

    @NotBlank(message = "Address is required")
    @Size(max = 250, message = "Address is too long")
    private String address;

    @NotBlank(message = "Contact name is required")
    @Size(max = 80, message = "Contact name is too long")
    private String contactName;

    @NotBlank(message = "Phone is required")
    @Pattern(regexp = "^[+]?[0-9 -]{8,16}$", message = "Enter a valid phone number")
    private String phone;

    @NotBlank(message = "Email is required")
    @Email(message = "Enter a valid email")
    @Size(max = 254, message = "Email is too long")
    private String email;

    @Size(max = 1000, message = "Keep the description under 1000 characters")
    private String about;

    @Min(value = 0, message = "Fee cannot be negative")
    @Max(value = 100000, message = "Fee looks too high")
    private int feeFrom;

    private boolean open24x7;

    private boolean homeVisit;
}
