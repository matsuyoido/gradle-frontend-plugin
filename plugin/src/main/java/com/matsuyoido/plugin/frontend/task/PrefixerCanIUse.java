package com.matsuyoido.plugin.frontend.task;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import com.matsuyoido.caniuse.Browser;
import com.matsuyoido.caniuse.CanIUse;
import com.matsuyoido.caniuse.SupportLevel;
import com.matsuyoido.caniuse.Version;
import com.matsuyoido.plugin.frontend.extension.PrefixerExtension;

import org.gradle.api.Project;

/**
 * PrefixerCanIUse
 */
public class PrefixerCanIUse extends CanIUse {
    private final PrefixerExtension extension;

    PrefixerCanIUse(Project project, PrefixerExtension extension) throws IOException {
        this.extension = extension;
        File settingCaniuseFile = extension.getCaniuseData();

        try {
            if (settingCaniuseFile == null || !settingCaniuseFile.exists() || !settingCaniuseFile.isFile()
                    || !settingCaniuseFile.canRead()) {
                Path rootProjectDir = project.getRootProject().getProjectDir().toPath();
                File dataJsonFile = rootProjectDir.resolve("gradle/plugin/data.json").toFile();
                if (!dataJsonFile.exists()) {
                    settingCaniuseFile = downloadDataJson(dataJsonFile);
                } else {
                    settingCaniuseFile = dataJsonFile;
                }
            }
            super.setup(settingCaniuseFile);
        } catch (Exception e) {
            project.getLogger().warn("[task] file '{}' load failed.", settingCaniuseFile);
            super.setup(loadDataJsonForJar());
        }
    }

    private File downloadDataJson(File jsonFile) throws IOException {
        String repoUrl = "https://github.com/Fyrd/caniuse/blob/master/data.json";
        URL url = new URL(repoUrl.replace("github.com", "raw.githubusercontent.com").replace("/blob", ""));
        
        return new Download().execute(url, jsonFile);
    }

    private File loadDataJsonForJar() throws IOException {
        Path jsonFile = Files.createTempFile("plugin-", ".json");
        try (InputStream in = getClass().getResourceAsStream("/META-INF/resources/webjars/caniuse-db/1.0.30000748/data.json")) {
            Files.copy(in, jsonFile, StandardCopyOption.REPLACE_EXISTING);
            File result = jsonFile.toFile();
            result.deleteOnExit();
            return result;
        }
        /*
        new File(getClass().getResource("/META-INF/resources/webjars/caniuse-db/1.0.30000748/data.json")
                           .toExternalForm())
        */
    }

	@Override
    protected boolean isAddSupport(Browser browser) {
        switch(browser.getAgent()) {
            case "ie": 
                return isSetBrowser(this.extension.getIe());
            case "edge":
                return isSetBrowser(this.extension.getEdge());
            case "firefox":
                return isSetBrowser(this.extension.getFirefox());
            case "chrome":
                return isSetBrowser(this.extension.getChrome());
            case "safari":
                return isSetBrowser(this.extension.getSafari());
            case "ios_saf":
                return isSetBrowser(this.extension.getIos());
            case "android":
            case "and_ff":
            case "and_chr":
                return isSetBrowser(this.extension.getAndroid());
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
            default:
                return false;
        }
    }

    @Override
    protected boolean isAddSupportData(Browser browser, Version version, SupportLevel supportLevel) {
        return isAddPrefixerVersion(supportLevel) && isAddPrefixerVersion(browser.getAgent(), version);
    }

    private boolean isAddPrefixerVersion(SupportLevel level) {
        return SupportLevel.ENABLE_WITH_PREFIX == level
            || SupportLevel.PARTIAL_WITH_PREFIX == level
            || SupportLevel.YES_WITH_PREFIX == level;
    }

    private boolean isAddPrefixerVersion(String browser, Version version) {
        String versionString = null;
        switch(browser) {
            case "ie": 
                versionString = this.extension.getIe();
                break;
            case "edge":
                versionString = this.extension.getEdge();
                break;
            case "firefox":
                versionString = this.extension.getFirefox();
                break;
            case "chrome":
                versionString = this.extension.getChrome();
                break;
            case "safari":
                versionString = this.extension.getSafari();
                break;
            case "ios_saf":
                versionString = this.extension.getIos();
                break;
            case "android":
            case "and_ff":
            case "and_chr":
                versionString = this.extension.getAndroid();
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
        return isGreaterThanVersion(versionString, version);
    }

    private boolean isGreaterThanVersion(String supportVersion, Version compareVersion) {
        if (supportVersion == null || supportVersion.isEmpty()) {
            return false;
        } else if ("ALL".equals(supportVersion.toUpperCase())) {
            return true;
        } else {
            return new Version(supportVersion).isNewerThan(compareVersion);
        }
    }

    private boolean isSetBrowser(String browser) {
        return browser != null && !browser.trim().isEmpty(); //!browser.isBlank();
    }
}