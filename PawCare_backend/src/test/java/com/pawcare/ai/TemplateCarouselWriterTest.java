package com.pawcare.ai;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.pawcare.dto.CarouselGenerateRequest;
import com.pawcare.dto.SlideDto;

class TemplateCarouselWriterTest {

    private final TemplateCarouselWriter writer = new TemplateCarouselWriter();

    private static CarouselGenerateRequest request(String idea, String notes, int slides) {
        CarouselGenerateRequest r = new CarouselGenerateRequest();
        r.setIdea(idea);
        r.setNotes(notes);
        r.setPlatform("INSTAGRAM");
        r.setTone("FRIENDLY");
        r.setTheme("SUNRISE");
        r.setSlideCount(slides);
        return r;
    }

    @Test
    void notesBecomeContentSlidesBetweenHookAndCta() {
        CarouselDraft draft = writer.write(request(
                "5 signs your dog needs a vet today",
                "1. Not eating: skipping two meals in a row is a red flag\n- Vomiting - more than twice in a day\nLimping",
                7));

        List<SlideDto> slides = draft.slides();
        assertThat(slides).hasSize(5);
        assertThat(slides.get(0).kind()).isEqualTo("HOOK");
        assertThat(slides.get(4).kind()).isEqualTo("CTA");
        assertThat(slides.get(1).headline()).isEqualTo("Not eating");
        assertThat(slides.get(1).body()).isEqualTo("Skipping two meals in a row is a red flag");
        assertThat(slides.get(2).headline()).isEqualTo("Vomiting");
        assertThat(slides.get(3).headline()).isEqualTo("Limping");
        assertThat(slides.get(3).body()).isEmpty();
        assertThat(slides.get(0).body()).startsWith("3 quick points");
        assertThat(draft.generatedBy()).isEqualTo("TEMPLATE");
    }

    @Test
    void withoutNotesItFillsTheRequestedSlideCount() {
        CarouselDraft draft = writer.write(request("How students can use AI to study smarter", null, 8));
        assertThat(draft.slides()).hasSize(8);
        assertThat(draft.hashtags()).contains("#AI");
    }

    @Test
    void extraNotesAreCappedToTheSlideCount() {
        String notes = String.join("\n", "a: 1", "b: 2", "c: 3", "d: 4", "e: 5", "f: 6", "g: 7");
        assertThat(writer.write(request("Too many points for five slides", notes, 5)).slides()).hasSize(5);
    }

    @Test
    void customCallToActionIsUsedOnLastSlide() {
        CarouselGenerateRequest r = request("Vaccination schedule for puppies", null, 5);
        r.setCta("Book a vet on PawCare");
        List<SlideDto> slides = writer.write(r).slides();
        assertThat(slides.get(slides.size() - 1).body()).isEqualTo("Book a vet on PawCare");
    }

    @Test
    void iconsComeFromKeywordsAndShortKeysMatchWholeWords() {
        assertThat(TemplateCarouselWriter.iconFor("Vaccination for puppies")).isEqualTo("syringe");
        assertThat(TemplateCarouselWriter.iconFor("Learn AI tools")).isEqualTo("book");
        assertThat(TemplateCarouselWriter.iconFor("Using AI well")).isEqualTo("sparkles");
        // Priority follows the keyword list, not word order: the dog wins over "signs".
        assertThat(TemplateCarouselWriter.iconFor("5 signs your dog needs a vet")).isEqualTo("stethoscope");
        assertThat(TemplateCarouselWriter.iconFor("5 signs your dog is happy")).isEqualTo("dog");
        // "said" contains "ai" but is not the word AI
        assertThat(TemplateCarouselWriter.iconFor("she said hello")).isEqualTo("sparkles");
        assertThat(TemplateCarouselWriter.iconFor("Emergency signs")).isEqualTo("siren");
    }

    @Test
    void truncateCutsOnAWordBoundary() {
        assertThat(TemplateCarouselWriter.truncate("one two three four", 12)).isEqualTo("one two\u2026");
        assertThat(TemplateCarouselWriter.truncate("short", 12)).isEqualTo("short");
    }

    @Test
    void topicIsTheFirstSentenceWithoutPunctuation() {
        assertThat(TemplateCarouselWriter.topicOf("  keep your cat cool in summer. It matters!"))
                .isEqualTo("Keep your cat cool in summer");
    }
}
