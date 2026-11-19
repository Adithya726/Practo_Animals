package com.pawcare.dto;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/** Step 6 of the workflow: the founder's edits, saved before export. */
@Data
public class CarouselUpdateRequest {

    @NotBlank(message = "Title is required")
    @Size(max = 255, message = "Title is too long")
    private String title;

    @NotBlank(message = "Choose a theme")
    @Pattern(regexp = "SUNRISE|FOREST|MIDNIGHT|CANDY", message = "Choose a theme")
    private String theme;

    @Size(max = 600, message = "Keep the caption under 600 characters")
    private String caption;

    @Size(max = 300, message = "Too many hashtags")
    private String hashtags;

    @Valid
    @Size(min = 3, max = 12, message = "A carousel needs 3 to 12 slides")
    private List<SlideDto> slides;
}
