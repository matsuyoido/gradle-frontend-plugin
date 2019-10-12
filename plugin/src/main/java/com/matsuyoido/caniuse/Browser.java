package com.matsuyoido.caniuse;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Browser
 */
public class Browser {

    private String agentName;
    private String browser;
    private String abbr;
    private String type;
    private Map<String, Double> usageGlobal;
    private List<VersionPrefixer> versionPrefixer;
    
    //#region getter
    public String getAgent() {
        return this.agentName;
    }
    public String getBrowser() {
        return this.browser;
    }
    public String getAbbr() {
        return this.abbr;
    }
    public String getType() {
        return this.type;
    }
    public Map<String, Double> getUsageGlobal() {
        return this.usageGlobal == null ? Collections.emptyMap() : this.usageGlobal;
    }

    protected VersionPrefixer getPrefixer(String version) {
        return this.versionPrefixer.stream()
                .filter(prefixer -> prefixer.version.equals(version))
                .findFirst().get();
    }
    //#endregion

    //#region setter
    public void setAgentName(String agent) {
        this.agentName = agent;
    }
    public void setBrowser(String browser) {
        this.browser = browser;
    }
    public void setAbbr(String abbr) {
        this.abbr = abbr;
    }
    public void setType(String type) {
        this.type = type;
    }
    public void addUsageGlobal(String key, double value) {
        if (this.usageGlobal == null) {
            this.usageGlobal = new HashMap<>();
        }
        this.usageGlobal.put(key, value);
    }
    public void addVersions(String version, String prefixer) {
        if (this.versionPrefixer == null) {
            this.versionPrefixer = new ArrayList<>();
        }
        this.versionPrefixer.add(new VersionPrefixer(version, prefixer));
    }
    //#endregion

}