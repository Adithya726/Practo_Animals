package com.pawcare.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record SlideDto(
        @NotBlank(message = "Slide type is required")
        @Pattern(regexp = "HOOK|CONTENT|CTA", message = "Invalid slide type")
        String kind,

        @NotBlank(message = "Every slide needs a headline")
        @Size(max = 160, message = "Keep headlines under 160 characters")
        String headline,

        @Size(max = 600, message = "Keep slide text under 600 characters")
        String body,

        @Size(max = 300, message = "Keep the visual note under 300 characters")
        String visualNote,

        @Size(max = 30, message = "Invalid icon")
        String icon) {
}
