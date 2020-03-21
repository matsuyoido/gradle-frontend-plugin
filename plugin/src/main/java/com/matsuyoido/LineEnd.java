package com.matsuyoido;

import java.util.Arrays;

/**
 * LineEnd
 */
public enum LineEnd {
    PLATFORM(System.lineSeparator()),
    WINDOWS("\r\n"),
    LINUX("\n"),
    MAC("\r");

    private String line;
    LineEnd(String lineString) {
        this.line = lineString;
    }

    public String get() {
        return this.line;
    }

    public static LineEnd of(String lineText) {
        return Arrays.stream(LineEnd.values())
                     .filter(val -> val.line.equals(lineText))
                     .findFirst()
                     .orElse(null);
    }
}