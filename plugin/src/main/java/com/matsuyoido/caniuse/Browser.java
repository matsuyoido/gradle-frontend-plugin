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
    private String prefix;
    private String type;
    private Map<String, Double> usageGlobal;
    private List<String> versions;
    
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
    public String getPrefix() {
        return this.prefix;
    }
    public String getType() {
        return this.type;
    }
    public Map<String, Double> getUsageGlobal() {
        return this.usageGlobal == null ? Collections.emptyMap() : this.usageGlobal;
    }
    public List<String> getVersions() {
        return this.versions == null ? Collections.emptyList() : this.versions;
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
    public void setPrefix(String prefix) {
        this.prefix = prefix;
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
    public void addVersions(String version) {
        if (this.versions == null) {
            this.versions = new ArrayList<>();
        }
        this.versions.add(version);
    }
    //#endregion

}