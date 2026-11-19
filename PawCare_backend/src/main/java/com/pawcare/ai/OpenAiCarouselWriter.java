package com.pawcare.ai;

import java.net.http.HttpClient;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import com.pawcare.dto.CarouselGenerateRequest;
import com.pawcare.dto.SlideDto;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

/**
 * Writes the carousel with an OpenAI-compatible chat model. The prompt asks for
 * strict JSON in the same shape the template writer produces, so the rest of
 * the pipeline (editing, rendering, export) does not care which writer ran.
 * Works with any OpenAI-compatible endpoint by changing OPENAI_BASE_URL.
 */
@Component
public class OpenAiCarouselWriter implements CarouselWriter {

    private static final Logger log = LoggerFactory.getLogger(OpenAiCarouselWriter.class);

    static final String SYSTEM_PROMPT = """
            You are a senior social media strategist and copywriter for an Indian founder who runs
            an animal-care discovery platform and an AI-skills education brand.
            Turn the founder's raw idea into a carousel. Research the topic from your knowledge,
            keep facts accurate and practical, and never invent statistics or medical claims.
            Structure: slide 1 is a HOOK that stops the scroll; middle slides are CONTENT with one
            idea each; the last slide is a CTA. Headlines max 9 words. Body max 28 words.
            For every slide add a short visualNote (layout and imagery direction for the designer)
            and an icon chosen from: paw, dog, cat, stethoscope, syringe, heart, siren, bowl,
            droplet, sparkles, workflow, wrench, wallet, clock, book, briefcase, palette, chart,
            alert, eye, check, lightbulb, bookmark.
            Reply with JSON only:
            {"title": string, "caption": string, "hashtags": string,
             "slides": [{"kind": "HOOK"|"CONTENT"|"CTA", "headline": string, "body": string,
                         "visualNote": string, "icon": string}]}
            """;

    private final String apiKey;
    private final String model;
    private final RestClient restClient;
    private final ObjectMapper objectMapper;

    public OpenAiCarouselWriter(
            @Value("${app.ai.openai-api-key:}") String apiKey,
            @Value("${app.ai.model}") String model,
            @Value("${app.ai.base-url}") String baseUrl,
            @Value("${app.ai.timeout-seconds}") int timeoutSeconds,
            ObjectMapper objectMapper) {

        this.apiKey = apiKey == null ? "" : apiKey.trim();
        this.model = model;
        this.objectMapper = objectMapper;

        JdkClientHttpRequestFactory factory = new JdkClientHttpRequestFactory(
                HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10)).build());
        factory.setReadTimeout(Duration.ofSeconds(timeoutSeconds));

        this.restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .requestFactory(factory)
                .build();
    }

    @Override
    public boolean isAvailable() {
        return !apiKey.isEmpty();
    }

    @Override
    public CarouselDraft write(CarouselGenerateRequest request) {
        String userPrompt = """
                Idea: %s
                Founder's notes (optional, one point per line): %s
                Platform: %s (%s)
                Tone: %s
                Total slides: %d (1 hook, %d content, 1 CTA)
                Call to action for the last slide: %s
                """.formatted(
                request.getIdea(),
                request.getNotes() == null || request.getNotes().isBlank() ? "none" : request.getNotes(),
                request.getPlatform(),
                "LINKEDIN".equals(request.getPlatform())
                        ? "professional audience, insight-led, 4:5 PDF carousel"
                        : "young pet parents and students, warm and visual, 4:5 image carousel",
                request.getTone(),
                request.getSlideCount(),
                request.getSlideCount() - 2,
                request.getCta() == null || request.getCta().isBlank() ? "save and share" : request.getCta());

        Map<String, Object> body = Map.of(
                "model", model,
                "temperature", 0.7,
                "response_format", Map.of("type", "json_object"),
                "messages", List.of(
                        Map.of("role", "system", "content", SYSTEM_PROMPT),
                        Map.of("role", "user", "content", userPrompt)));

        String raw = restClient.post()
                .uri("/chat/completions")
                .header("Authorization", "Bearer " + apiKey)
                .contentType(MediaType.APPLICATION_JSON)
                .body(body)
                .retrieve()
                .body(String.class);

        JsonNode content = objectMapper.readTree(
                objectMapper.readTree(raw).path("choices").path(0).path("message").path("content").asString());

        return toDraft(content, request.getSlideCount());
    }

    /** Validates the model's JSON and clamps it to the limits the editor enforces. */
    CarouselDraft toDraft(JsonNode json, int expectedSlides) {
        List<SlideDto> slides = new ArrayList<>();
        for (JsonNode s : json.path("slides")) {
            String headline = s.path("headline").asString("").trim();
            if (headline.isEmpty()) {
                continue;
            }
            String kind = s.path("kind").asString("CONTENT").toUpperCase();
            if (!List.of("HOOK", "CONTENT", "CTA").contains(kind)) {
                kind = "CONTENT";
            }
            slides.add(new SlideDto(
                    kind,
                    TemplateCarouselWriter.truncate(headline, 150),
                    TemplateCarouselWriter.truncate(s.path("body").asString("").trim(), 580),
                    TemplateCarouselWriter.truncate(s.path("visualNote").asString("").trim(), 290),
                    s.path("icon").asString("sparkles")));
        }
        if (slides.size() < 3) {
            throw new IllegalStateException("The AI returned too few slides");
        }
        if (slides.size() > expectedSlides + 2) {
            slides = new ArrayList<>(slides.subList(0, expectedSlides + 2));
        }
        log.info("AI wrote {} slides with {}", slides.size(), model);

        return new CarouselDraft(
                TemplateCarouselWriter.truncate(json.path("title").asString("Untitled carousel"), 120),
                slides,
                TemplateCarouselWriter.truncate(json.path("caption").asString(""), 590),
                TemplateCarouselWriter.truncate(json.path("hashtags").asString(""), 290),
                model);
    }
}
