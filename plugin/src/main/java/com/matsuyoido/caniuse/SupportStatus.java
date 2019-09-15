package com.matsuyoido.caniuse;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * SupportStatus
 */
public class SupportStatus {

    private String browser;
    private String prefixer;
    private Map<Version, SupportLevel> supportVersionMap;

    //#region getter
    public String getBrowser() {
        return this.browser;
    }
    public String getPrefixer() {
        return this.prefixer;
    }
    public Map<Version, SupportLevel> getSupportVersionMap() {
        return this.supportVersionMap == null ? Collections.emptyMap() : this.supportVersionMap;
    }
    //#endregion

    //#region setter
    public void setBrowser(String browser, String prefixer) {
        this.browser = browser;
        this.prefixer = prefixer;
    }
    public void addSupportVersion(Version version, SupportLevel level) {
        if (this.supportVersionMap == null) {
            this.supportVersionMap = new HashMap<>();
        }

        this.supportVersionMap.put(version, level);
    }
    //#endregion

}