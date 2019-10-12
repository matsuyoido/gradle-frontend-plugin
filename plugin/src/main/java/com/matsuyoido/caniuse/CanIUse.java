package com.matsuyoido.caniuse;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;


/**
 * CanIUse
 */
public class CanIUse {

    protected List<Browser> browsers;
    protected List<SupportData> supports;

    public CanIUse(File dataFile) throws IOException {
        this.setup(dataFile);
    }
    protected CanIUse() {}

    public List<Browser> getBrowsers() {
        return Collections.unmodifiableList(this.browsers);
    }

    public List<SupportData> getCssSupports() {
        return this.supports.stream()
                            .filter(d -> 
                                d.getCategories().stream().anyMatch(category ->
                                    "CSS".equals(category) ||
                                    "CSS3".equals(category) ||
                                    "CSS2".equals(category)
                                )
                            )
                            .collect(Collectors.toList());
    }

    protected void setup(File dataFile) throws IOException {
        setup(Files.readAllLines(dataFile.toPath()).stream().collect(Collectors.joining(System.lineSeparator())));
        // setup(Files.readString(dataFile.toPath()));
    }

    private void setup(String jsonData) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode caniuseNode = mapper.readTree(jsonData);

        Map<String, Browser> browsers = convertBrowserMap(caniuseNode);
        this.supports = convertData(caniuseNode, browsers);
        this.browsers = new ArrayList<>(browsers.values());
    }

    private Map<String, Browser> convertBrowserMap(JsonNode node) {
        Map<String, Browser> browserMap = new HashMap<>();
        node.get("agents").fields().forEachRemaining(entry -> {
            Browser browser = new Browser();
            JsonNode agentRoot = entry.getValue();
            String prefixer = agentRoot.get("prefix").asText();
            browser.setAgentName(entry.getKey());
            browser.setBrowser(agentRoot.get("browser").asText());
            browser.setAbbr(agentRoot.get("abbr").asText());
            browser.setType(agentRoot.get("type").asText());
            agentRoot.get("usage_global").fields().forEachRemaining(usageEntry -> 
                browser.addUsageGlobal(usageEntry.getKey(),
                    usageEntry.getValue().asDouble())
            );
            List<String> addedPrefixer = new ArrayList<>();
            JsonNode specialPrefix = agentRoot.get("prefix_exceptions");
            if (specialPrefix != null) {
                specialPrefix.fields().forEachRemaining(prefixEntry -> {
                    String version = prefixEntry.getKey();
                    browser.addVersions(version, prefixEntry.getValue().asText());
                    addedPrefixer.add(version);
                });
            }
            agentRoot.get("versions").elements().forEachRemaining(versionValue -> {
                if (!versionValue.isNull() && !addedPrefixer.contains(versionValue.asText())) {
                    browser.addVersions(versionValue.asText(), prefixer);
                }
            });
            browserMap.put(entry.getKey(), browser);
        });
        return browserMap;
    }

    private List<SupportData> convertData(JsonNode node, Map<String, Browser> browsers) {
        List<SupportData> dataList = new ArrayList<>();
        node.get("data").fields().forEachRemaining(dataEntry -> {
            String key = dataEntry.getKey();
            JsonNode rootNode = dataEntry.getValue();

            String title = rootNode.get("title").asText();
            String description = rootNode.get("description").asText();
            String keyword = rootNode.get("keywords").asText();
            List<String> categories = new ArrayList<>();
            rootNode.get("categories").elements().forEachRemaining(category -> 
                categories.add(category.asText())
            );
            List<SupportStatus> supports = new ArrayList<>();
            rootNode.get("stats").fields().forEachRemaining(statusEntry -> {
                Browser browser = browsers.get(statusEntry.getKey());
                if (isAddSupport(browser)) {
                    SupportStatus support = new SupportStatus(browser.getAgent());
                    statusEntry.getValue().fields().forEachRemaining(supportEntry -> {
                        Version version = convertVersion(supportEntry.getKey());
                        SupportLevel supportLevel = SupportLevel.of(supportEntry.getValue().asText());
                        if (isAddSupportData(browser, version, supportLevel)) {
                            support.addSupportVersion(browser.getPrefixer(supportEntry.getKey()), supportLevel);
                        }
                    });
                    if (!support.getSupportVersionMap().isEmpty()) {
                        supports.add(support);   
                    }
                }
            });

            if (!supports.isEmpty()) {
                SupportData data = new SupportData();
                data.setKey(key);
                data.setKeyword(keyword);
                data.setDescription(description);
                data.setTitle(title);
                data.setCategories(categories);
                data.setSupports(supports);
                dataList.add(data);
            }
        });
        return dataList;
    }

    private Version convertVersion(String version) {
        if (version.contains("-")) {
            String[] versions = version.split("-");
            Version minVersion = new Version(versions[0]);
            return minVersion;
        } else {
            return new Version(version);
        }
    }

    /**
     * custom support filter. default is all support.
     *
     * @param browser      {@link Browser}
     * @return
     */
    protected boolean isAddSupport(Browser browser) {
        return true;
    }

    /**
     * custom support filter. default is all support.
     *
     * @param browser      {@link Browser}
     * @param version  {@link Version}
     * @param supportLevel {@link SupportLevel}
     * @return
     */
    protected boolean isAddSupportData(Browser browser, Version version, SupportLevel supportLevel) {
        return true;
    }


}