package com.pawcare.ai;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Component;

import com.pawcare.dto.CarouselGenerateRequest;
import com.pawcare.dto.SlideDto;

/**
 * Offline writer that needs no API key. It applies the same storytelling
 * structure the AI prompt asks for (hook, one idea per slide, call to action),
 * turns the founder's notes into slides, and picks an icon per slide from
 * keywords. It keeps the studio usable without a key, and it is the fallback
 * when the AI call fails.
 */
@Component
public class TemplateCarouselWriter implements CarouselWriter {

    static final int HEADLINE_MAX = 70;

    /** Keyword to icon name, checked in order (the frontend maps names to lucide icons). */
    private static final List<Map.Entry<String, String>> ICONS = List.of(
            // Specific topics first
            Map.entry("emergency", "siren"), Map.entry("breath", "siren"), Map.entry("ambulance", "siren"),
            Map.entry("vaccin", "syringe"), Map.entry("vomit", "alert"), Map.entry("bloat", "alert"),
            Map.entry("swollen", "alert"), Map.entry("blood", "alert"), Map.entry("pain", "heart"),
            Map.entry("limp", "heart"), Map.entry("health", "heart"), Map.entry("eating", "bowl"),
            Map.entry("meal", "bowl"), Map.entry("food", "bowl"), Map.entry("diet", "bowl"),
            Map.entry("water", "droplet"), Map.entry("vet", "stethoscope"), Map.entry("prompt", "sparkles"),
            Map.entry("automat", "workflow"), Map.entry("workflow", "workflow"), Map.entry("money", "wallet"),
            Map.entry("cost", "wallet"), Map.entry("price", "wallet"), Map.entry("time", "clock"),
            Map.entry("daily", "clock"), Map.entry("hour", "clock"), Map.entry("design", "palette"),
            Map.entry("data", "chart"), Map.entry("growth", "chart"), Map.entry("career", "briefcase"),
            Map.entry("job", "briefcase"), Map.entry("learn", "book"), Map.entry("skill", "book"),
            Map.entry("study", "book"), Map.entry("ai", "sparkles"), Map.entry("tool", "wrench"),
            // Then animals
            Map.entry("dog", "dog"), Map.entry("puppy", "dog"), Map.entry("puppies", "dog"),
            Map.entry("cat", "cat"), Map.entry("kitten", "cat"), Map.entry("pet", "paw"), Map.entry("animal", "paw"),
            // Then generic words
            Map.entry("mistake", "alert"), Map.entry("avoid", "alert"), Map.entry("sign", "eye"),
            Map.entry("check", "check"), Map.entry("tip", "lightbulb"));

    @Override
    public boolean isAvailable() {
        return true;
    }

    @Override
    public CarouselDraft write(CarouselGenerateRequest request) {
        String topic = topicOf(request.getIdea());
        String tone = request.getTone();
        int contentSlots = request.getSlideCount() - 2;

        List<String[]> points = parseNotes(request.getNotes());
        if (points.isEmpty()) {
            points = framework(topic);
        }
        int contentCount = Math.min(contentSlots, points.size());

        List<SlideDto> slides = new ArrayList<>();
        slides.add(hook(topic, tone, contentCount));

        for (int i = 0; i < contentCount; i++) {
            String[] point = points.get(i);
            String headline = truncate(point[0], HEADLINE_MAX);
            slides.add(new SlideDto(
                    "CONTENT",
                    headline,
                    point[1],
                    "Number badge " + (i + 1) + ", large headline, one supporting line, icon top-right",
                    iconFor(point[0] + " " + point[1])));
        }

        String cta = request.getCta() == null || request.getCta().isBlank()
                ? "Save this and share it with someone who needs it"
                : request.getCta().trim();
        slides.add(new SlideDto(
                "CTA",
                ctaHeadline(tone),
                cta,
                "Brand colour background, logo, handle and a clear save/share prompt",
                "bookmark"));

        String hookLine = slides.get(0).headline();
        String caption = hookLine + "\n\n" + (slides.size() - 2) + " points worth saving. Swipe through \u2192\n\n" + cta;

        return new CarouselDraft(
                truncate(topic, 120),
                slides,
                caption,
                hashtags(request.getIdea(), request.getPlatform()),
                "TEMPLATE");
    }

    /** The first sentence of the idea, without a trailing full stop. */
    static String topicOf(String idea) {
        String trimmed = idea.trim().replaceAll("\\s+", " ");
        int end = trimmed.indexOf(". ");
        String first = end > 0 ? trimmed.substring(0, end) : trimmed;
        first = first.replaceAll("[.!?]+$", "");
        return Character.toUpperCase(first.charAt(0)) + first.substring(1);
    }

    private static SlideDto hook(String topic, String tone, int count) {
        String headline = switch (tone) {
            case "EXPERT" -> truncate(topic, 70);
            case "BOLD" -> "Stop scrolling. " + truncate(topic, 54);
            default -> truncate(topic, 70);
        };
        String body = switch (tone) {
            case "EXPERT" -> "What most people miss, in " + count + " points.";
            case "BOLD" -> count + " things to get right, starting today.";
            default -> count + " quick points. Swipe through and save it for later.";
        };
        return new SlideDto("HOOK", headline, body,
                "Big bold hook, high-contrast background, swipe arrow bottom-right", iconFor(topic));
    }

    private static String ctaHeadline(String tone) {
        return switch (tone) {
            case "EXPERT" -> "Found this useful?";
            case "BOLD" -> "Don't scroll past this.";
            default -> "Liked this? Keep it handy.";
        };
    }

    /**
     * One point per line. "Headline: detail" or "Headline - detail" splits into
     * headline and body; a plain line becomes the headline alone.
     */
    static List<String[]> parseNotes(String notes) {
        List<String[]> points = new ArrayList<>();
        if (notes == null || notes.isBlank()) {
            return points;
        }
        for (String raw : notes.split("\\R")) {
            String line = raw.trim().replaceFirst("^([-*\u2022]|\\d+[.)])\\s*", "");
            if (line.isEmpty()) {
                continue;
            }
            int split = line.indexOf(':');
            if (split < 0) {
                split = line.indexOf(" - ");
            }
            if (split > 0 && split < line.length() - 1) {
                String head = line.substring(0, split).trim();
                String body = line.substring(split + (line.charAt(split) == ':' ? 1 : 3)).trim();
                points.add(new String[] { capitalise(head), capitalise(body) });
            } else {
                points.add(new String[] { capitalise(line), "" });
            }
        }
        return points;
    }

    /**
     * A teaching structure used when the founder gives no notes. It is deliberately
     * generic; notes (or the AI writer) give topic-specific slides.
     */
    private static List<String[]> framework(String topic) {
        return List.of(
                new String[] { "Why this matters", "Small decisions here add up. Getting the basics right early saves time, money and stress." },
                new String[] { "The common mistake", "Waiting until something goes wrong. A little planning up front beats fixing problems later." },
                new String[] { "Start with one small step", "Pick the easiest change you can make this week and do just that." },
                new String[] { "Build a simple routine", "Same day, same time, same checklist. Consistency beats intensity." },
                new String[] { "Ask the right expert", "One good conversation with a professional saves hours of guessing online." },
                new String[] { "Track what changes", "A two-line note each week shows you what is working." },
                new String[] { "Share what you learn", "Teaching someone else is the fastest way to remember it." },
                new String[] { "Your quick win today", "Choose one point from this carousel and act on it before tonight." });
    }

    /** The first keyword in ICONS (in priority order) that appears in the text decides the icon. */
    static String iconFor(String text) {
        List<String> words = List.of(text.toLowerCase(Locale.ROOT).split("[^a-z]+"));
        for (Map.Entry<String, String> e : ICONS) {
            String key = e.getKey();
            for (String word : words) {
                // Short keys like "ai" or "vet" must match the whole word; longer ones may be a prefix.
                boolean hit = key.length() <= 3 ? word.equals(key) : word.startsWith(key);
                if (hit) {
                    return e.getValue();
                }
            }
        }
        return "sparkles";
    }

    static String hashtags(String idea, String platform) {
        Set<String> tags = new LinkedHashSet<>();
        String lower = idea.toLowerCase(Locale.ROOT);
        if (lower.matches(".*\\b(dog|dogs|puppy|puppies)\\b.*")) {
            tags.add("#DogCare");
        }
        if (lower.matches(".*\\b(cat|cats|kitten|kittens)\\b.*")) {
            tags.add("#CatCare");
        }
        if (lower.matches(".*\\b(pet|pets|animal|animals|vet|vets)\\b.*")) {
            tags.add("#PetCare");
            tags.add("#PetParents");
        }
        if (lower.matches(".*\\b(ai|chatgpt|claude|prompt|prompts|automation)\\b.*")) {
            tags.add("#AI");
            tags.add("#AITools");
        }
        if (lower.matches(".*\\b(career|job|jobs|skill|skills|student|students)\\b.*")) {
            tags.add("#Upskilling");
            tags.add("#CareerGrowth");
        }
        tags.add("LINKEDIN".equals(platform) ? "#Learning" : "#SaveThis");
        return String.join(" ", tags);
    }

    static String truncate(String s, int max) {
        if (s.length() <= max) {
            return s;
        }
        int cut = s.lastIndexOf(' ', max - 1);
        return s.substring(0, cut > max / 2 ? cut : max - 1).trim() + "\u2026";
    }

    private static String capitalise(String s) {
        return s.isEmpty() ? s : Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }
}
