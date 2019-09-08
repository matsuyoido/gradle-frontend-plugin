package com.matsuyoido.caniuse;

import java.util.List;

/**
 * SupportData
 */
public class SupportData {

    private String key;
    private String title;
    private String keyword;
    private String description;

    private List<String> categories;
    private List<SupportStatus> supports;

    //#region getter
    public String getKey() {
        return this.key;
    }
    public String getTitle() {
        return this.title;
    }
    public String getKeyword() {
        return this.keyword;
    }
    public String getDescription() {
        return this.description;
    }
    public List<String> getCategories() {
        return this.categories;
    }
    public List<SupportStatus> getSupports() {
        return this.supports;
    }
    //#endregion

    //#region setter
    public void setKey(String key) {
        this.key = key;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public void setCategories(List<String> categories) {
        this.categories = categories;
    }
    public void setSupports(List<SupportStatus> supports) {
        this.supports = supports;
    }
    //#endregion
}