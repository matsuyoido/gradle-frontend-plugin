package com.matsuyoido.caniuse;

import java.util.Arrays;

/**
 * SupportLevel
 */
public enum SupportLevel {
    /** Support */
    YES("y"),
    /** Support with prefix */
    YES_WITH_PREFIX("y x"),
    /** Partial support */
    PARTIAL("a"),
    /** Partial support with prefix */
    PARTIAL_WITH_PREFIX("a x"),
    /** Not supported */
    NO("n"), 
    /** Not supported by default, but can be enabled. Requires with prefix */
    ENABLE_WITH_PREFIX("n x d"),
    /** Not supported by default, but can be enabled. */
    ENABLE("n d"),
    /** Not supported by default, but can be enabled..?? */
    UNKNOWN_ENABLE("u d"),
    /** Not supported by default, but can be enabled. */
    PARTIAL_ENABLE("a d"),
    /** Not supported by default, but can be enabled. */
    PENDING_ENABLE("p d"),
    /** Not supported */
    PENDING("p"),
    /** Support unknown */
    UNKNOWN("u");
    
    private String val;

    SupportLevel(String val) {
        this.val = val;
    }

    public static SupportLevel of(String val) {
        String v = val.replaceAll("#[0-9]+", "").trim();
        if ("n d x".equals(v)) {
            return ENABLE_WITH_PREFIX;
        }
        return Arrays.stream(SupportLevel.values())
                     .filter(level -> level.val.equals(v))
                     .findFirst()
                     .orElse(null);
    }
}