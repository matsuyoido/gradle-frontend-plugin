package com.matsuyoido.plugin.frontend.extension;

import java.io.File;
import java.io.Serializable;

/**
 * ScssExtension
 */
public class ScssExtension implements Serializable {
    private static final long serialVersionUID = 692641724829802569L;

    private File inputDirectory;
    private File outputDirectory;
    private boolean addPrefixer;
    private boolean enableMinify;

    public void setInDir(File path) {
        this.inputDirectory = path;
    }
    public void setOutDir(File path) {
        this.outputDirectory = path;
    }
    public void setAddPrefixer(boolean isAdd) {
        this.addPrefixer = isAdd;
    }
    public void setEnableMinify(boolean enable) {
        this.enableMinify = enable;
    }


    public File getInputDirectory() {
        return this.inputDirectory;
    }
    public File getOutputDirectory() {
        return this.outputDirectory;
    }
    public boolean isAddPrefixer() {
        return this.addPrefixer;
    }
    public boolean isEnableMinify() {
        return this.enableMinify;
    }

}