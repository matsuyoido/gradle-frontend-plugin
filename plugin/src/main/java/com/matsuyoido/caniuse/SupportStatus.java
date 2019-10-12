package com.matsuyoido.caniuse;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * SupportStatus
 */
public class SupportStatus {

    private String browser;
    private Map<VersionPrefixer, SupportLevel> supportVersionMap;

    public SupportStatus(String browser) {
        this.browser = browser;
    }
    //#region getter
    public String getBrowser() {
        return this.browser;
    }
    public Map<VersionPrefixer, SupportLevel> getSupportVersionMap() {
        return this.supportVersionMap == null ? Collections.emptyMap() : this.supportVersionMap;
    }
    //#endregion

    //#region setter
    public void addSupportVersion(VersionPrefixer version, SupportLevel level) {
        if (this.supportVersionMap == null) {
            this.supportVersionMap = new HashMap<>();
        }
        this.supportVersionMap.put(version, level);
    }
    //#endregion

}