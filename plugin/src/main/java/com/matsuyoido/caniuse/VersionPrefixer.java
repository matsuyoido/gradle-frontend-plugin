package com.matsuyoido.caniuse;

/**
 * Version with Prefixer
 */
public class VersionPrefixer {

    protected String version;
    private String prefixer;

    public VersionPrefixer(String version, String prefixer) {
        this.version = version;
        this.prefixer = prefixer;
    }

    public String getPrefixer() {
        return this.prefixer;
    }
}