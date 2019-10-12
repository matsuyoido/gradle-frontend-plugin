package com.matsuyoido.caniuse;

/**
 * Version
 */
public class Version implements Comparable<Version> {

    private String version;
    private boolean isLatest;
    private String[] parts;

    public Version(String version) {
        this.version = version;
        if ("TP".equals(version)) { // Safari Technology Preview
            this.isLatest = true;
        } else if ("all".equals(version)) { // Opera mini all
            this.isLatest = true;
        } else if (version.matches("[0-9]+(\\.[0-9]+)*")) {
            this.parts = version.split("\\.");
        } else {
            this.isLatest = true;
        }
    }

    public String getVersion() {
        return this.version;
    }

    @Override
    public int compareTo(Version comp) {
        if(comp == null) {
            return 1;
        } else if (this.isLatest) {
            return 1;
        } else {
            String[] thisParts = this.parts;
            String[] thatParts = comp.parts;
            int length = Math.max(thisParts.length, thatParts.length);
            for(int i = 0; i < length; i++) {
                int thisPart = i < thisParts.length ?
                        Integer.parseInt(thisParts[i]) : 0;
                int thatPart = i < thatParts.length ?
                        Integer.parseInt(thatParts[i]) : 0;
                if(thisPart < thatPart) {
                    return -1;
                }
                if (thatPart < thisPart) {
                    return 1;
                }
            }
            return 0;
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof Version) {
            Version val = (Version) obj;
            return this.version.equals(val.version);
        } else {
            return false;
        }
    }

    public boolean isNewerThan(Version version) {
        return 0 <= this.compareTo(version);
    }
}