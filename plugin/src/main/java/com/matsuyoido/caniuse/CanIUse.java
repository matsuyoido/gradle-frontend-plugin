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

    protected Map<String, Browser> browsers;
    protected List<SupportData> supports;

    public CanIUse(File dataFile) throws IOException {
        setup(Files.readString(dataFile.toPath()));
    }

    public Map<String, Browser> getBrowsers() {
        return this.browsers;
    }

    /**
     * 
     * @return key: cssKeyword
     */
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



    private void setup(String jsonData) throws IOException {
        ObjectMapper mapper = new ObjectMapper();

        JsonNode caniuseNode = mapper.readTree(jsonData);

        this.browsers = Collections.unmodifiableMap(convertBrowserMap(caniuseNode));
        this.supports = Collections.unmodifiableList(convertData(caniuseNode, browsers));
    }

    private Map<String, Browser> convertBrowserMap(JsonNode node) {
        Map<String, Browser> browserMap = new HashMap<>();
        node.get("agents").fields().forEachRemaining(entry -> {
            Browser browser = new Browser();
            JsonNode agentRoot = entry.getValue();
            browser.setKey(entry.getKey());
            browser.setBrowser(agentRoot.get("browser").asText());
            browser.setAbbr(agentRoot.get("abbr").asText());
            browser.setPrefix(agentRoot.get("prefix").asText());
            browser.setType(agentRoot.get("type").asText());
            agentRoot.get("usage_global").fields().forEachRemaining(usageEntry -> 
                browser.addUsageGlobal(usageEntry.getKey(),
                    usageEntry.getValue().asDouble())
            );
            agentRoot.get("versions").elements().forEachRemaining(versionValue -> {
                if (!versionValue.isNull()) {
                    browser.addVersions(versionValue.asText());
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
                SupportStatus support = new SupportStatus();
                support.setBrowser(statusEntry.getKey(), browsers.get(statusEntry.getKey()).getPrefix());
                statusEntry.getValue().fields().forEachRemaining(supportEntry -> {
                    support.addSupportVersion(
                        supportEntry.getKey(), supportEntry.getValue().asText());
                });
                supports.add(support);
            });

            SupportData data = new SupportData();
            data.setKey(key);
            data.setKeyword(keyword);
            data.setDescription(description);
            data.setTitle(title);
            data.setCategories(categories);
            data.setSupports(supports);
            dataList.add(data);
        });
        return dataList;
    }
}