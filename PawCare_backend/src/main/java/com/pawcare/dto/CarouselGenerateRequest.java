package com.pawcare.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/** Step 1 of the workflow: the founder's raw idea plus a few settings. */
@Data
public class CarouselGenerateRequest {

    @NotBlank(message = "Write your idea in a sentence or two")
    @Size(min = 10, max = 300, message = "Keep the idea between 10 and 300 characters")
    private String idea;

    /** Optional rough notes, one point per line. The engine turns each into a slide. */
    @Size(max = 2000, message = "Keep the notes under 2000 characters")
    private String notes;

    @NotBlank(message = "Choose a platform")
    @Pattern(regexp = "INSTAGRAM|LINKEDIN", message = "Choose Instagram or LinkedIn")
    private String platform;

    @NotBlank(message = "Choose a tone")
    @Pattern(regexp = "FRIENDLY|EXPERT|BOLD", message = "Choose a tone")
    private String tone;

    @NotBlank(message = "Choose a theme")
    @Pattern(regexp = "SUNRISE|FOREST|MIDNIGHT|CANDY", message = "Choose a theme")
    private String theme;

    @Min(value = 5, message = "Use at least 5 slides")
    @Max(value = 10, message = "Use at most 10 slides")
    private int slideCount = 7;

    /** Call to action on the last slide, for example "Follow @pawcare for more". */
    @Size(max = 80, message = "Keep the call to action under 80 characters")
    private String cta;
}
