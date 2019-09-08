package com.matsuyoido.plugin;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

/**
 * PathUtil
 */
public final class PathUtil {
    private PathUtil() {
    }

    public static final String classpathResourcePath(String path) {
        String uri = ClassLoader.getSystemResource(path).getPath();
        uri = uri.startsWith("/") ? uri.substring(1) : uri;
        // return URLDecoder.decode(uri, StandardCharsets.UTF_8);
        try {
            return URLDecoder.decode(uri, StandardCharsets.UTF_8.name());
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
}