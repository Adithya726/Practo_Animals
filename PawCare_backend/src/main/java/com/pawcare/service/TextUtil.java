package com.pawcare.service;

import java.util.Arrays;
import java.util.Locale;
import java.util.stream.Collectors;

import com.pawcare.exception.BadRequestException;

/** Small helpers for cleaning user input. */
public final class TextUtil {

    private TextUtil() {
    }

    public static String blankToNull(String s) {
        return s == null || s.isBlank() ? null : s.trim();
    }

    /** "  new   delhi " becomes "New Delhi", so one city never appears twice in the filters. */
    public static String titleCase(String s) {
        if (s == null) {
            return null;
        }
        return Arrays.stream(s.trim().split("\\s+"))
                .filter(w -> !w.isEmpty())
                .map(w -> w.substring(0, 1).toUpperCase(Locale.ROOT) + w.substring(1).toLowerCase(Locale.ROOT))
                .collect(Collectors.joining(" "));
    }

    public static <E extends Enum<E>> E parseEnum(Class<E> type, String value, String message) {
        if (value == null) {
            throw new BadRequestException(message);
        }
        try {
            return Enum.valueOf(type, value.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            throw new BadRequestException(message);
        }
    }
}
