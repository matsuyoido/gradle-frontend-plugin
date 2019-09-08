package com.matsuyoido.plugin.frontend.extension;

import java.io.File;
import java.io.Serializable;
import java.util.Objects;
import java.util.Map.Entry;
import java.util.function.Predicate;

import com.matsuyoido.caniuse.SupportLevel;
import com.matsuyoido.caniuse.SupportStatus;
import com.matsuyoido.model.Version;

/**
 * PrefixerExtension
 */
public class PrefixerExtension implements Serializable {
    private static final long serialVersionUID = 7180279308716077071L;

    private File caniuseDataFile;
    private String ie;
    private String edge;
    private String chrome;
    private String firefox;
    private String safari;
    private String ios;
    private String android;

    public void setCaniuseData(File data) {
        this.caniuseDataFile = data;
    }

    public void setIe(String version) {
        this.ie = version;
    }

    public void setEdge(String version) {
        this.edge = version;
    }

    public void setChrome(String version) {
        this.chrome = version;
    }

    public void setFirefox(String version) {
        this.firefox = version;
    }

    public void setSafari(String version) {
        this.safari = version;
    }

    public void setIos(String version) {
        this.ios = version;
    }

    public void setAndroid(String version) {
        this.android = version;
    }

    // #region getter
    public File getCaniuseData() {
        return this.caniuseDataFile != null ? this.caniuseDataFile :
            new File(getClass().getResource("/META-INF/resources/webjars/caniuse-db/1.0.30000748/data.json")
                               .toExternalForm());
        /*
        return Objects.requireNonNullElseGet(
            this.caniuseDataFile, () -> 
            new File(getClass().getResource("/META-INF/resources/webjars/caniuse-db/1.0.30000748/data.json")
                               .toExternalForm()));
        */
    }
    public Predicate<SupportStatus> getSupportFilter() {
        return status -> 
            status.getSupportVersionMap().entrySet()
                    .stream()
                    .filter(entry -> this.isAddPrefixerVersion(status.getBrowser(), entry))
                    .anyMatch(this::isAddPrefixer)
        ;
    }
    //#endregion

    private boolean isAddPrefixerVersion(String browser, Entry<String, SupportLevel> entry) {
        boolean isBrowserSupport = false;
        String version = null;
        switch(browser) {
            case "ie": 
            isBrowserSupport = this.ie != null && !this.ie.trim().isEmpty(); //!this.ie.isBlank();
            version = this.ie;
            break;
            case "edge":
            isBrowserSupport = this.edge != null && !this.edge.trim().isEmpty();//!this.edge.isBlank();
            version = this.edge;
            break;
            case "firefox":
            isBrowserSupport = this.firefox != null && !this.firefox.trim().isEmpty();//!this.firefox.isBlank();
            version = this.firefox;
            break;
            case "chrome":
            isBrowserSupport = this.chrome != null && !this.chrome.trim().isEmpty();//!this.chrome.isBlank();
            version = this.chrome;
            break;
            case "safari":
            isBrowserSupport = this.safari != null && !this.safari.trim().isEmpty();//!this.safari.isBlank();
            version = this.safari;
            case "ios_saf":
            isBrowserSupport = this.ios != null && !this.ios.trim().isEmpty();//!this.ios.isBlank();
            version = this.ios;
            break;
            case "android":
            case "and_ff":
            case "and_chr":
            isBrowserSupport = this.android != null && !this.android.trim().isEmpty();//!this.android.isBlank();
            version = this.android;
            break;
            case "opera":
            case "op_mini":
            case "bb":
            case "op_mob":
            case "ie_mob":
            case "and_uc":
            case "samsung":
            case "and_qq":
            case "baidu":
            case "kaios":
            break;
        }
        return isBrowserSupport && isGreaterThanVersion(version, entry.getKey());
    }

    private boolean isGreaterThanVersion(String supportVersion, String compareVersion) {
        if (supportVersion == null) {
            return false;
        } else if (supportVersion == "ALL") {
            return true;
        }

        if (compareVersion.equals("TP") || compareVersion.equals("all")) {
            return true;
        } else if (compareVersion.contains("-")) {
            Version version = new Version(supportVersion);
            String[] versions = compareVersion.split("-");
            Version minVersion = new Version(versions[0]);
            return version.isNewerThan(minVersion);
        } else {
            return new Version(supportVersion).isNewerThan(new Version(compareVersion));
        }
    }

    private boolean isAddPrefixer(Entry<String, SupportLevel> entry) {
        SupportLevel level = entry.getValue();
        boolean isAddPrefixLevel = SupportLevel.ENABLE_WITH_PREFIX == level
                    || SupportLevel.PARTIAL_WITH_PREFIX == level || SupportLevel.YES_WITH_PREFIX == level;
        return isAddPrefixLevel;
    }
}