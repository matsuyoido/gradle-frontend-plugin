package com.matsuyoido.plugin.frontend.task;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;

import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;

public class Download {
    private final Logger log;

    public Download() {
        this.log = Logging.getLogger(this.getClass());
    }

    public File execute(URL url, File outputFile) throws IOException {
        outputFile.getParentFile().mkdirs();
        outputFile.createNewFile();
        outputFile.setReadable(true);
        outputFile.setWritable(true);

        log.lifecycle("[task] download file from {}...", url);
        HttpURLConnection http = (HttpURLConnection) url.openConnection();
        Map<String, List<String>> header = http.getHeaderFields();
        while (isRedirected(header)) {
            URL redirectUrl = new URL(header.get("Location").get(0));
            http = (HttpURLConnection) redirectUrl.openConnection();
            header = http.getHeaderFields();
        }

        // int allSize = http.getContentLength();
        // int loadSize = 0;
        byte[] buffer = new byte[4096];
        int n = -1;
        try (BufferedInputStream input = new BufferedInputStream(http.getInputStream());
                FileOutputStream output = new FileOutputStream(outputFile)) {
            while ((n = input.read(buffer)) != -1) {
                output.write(buffer, 0, n);
                // loadSize += n;
            }
            log.lifecycle("[task] download file completed.");
            log.info("[task] download file -> {}", outputFile);
            return outputFile;
        } catch (IOException e) {
            log.lifecycle("[task] download error.", e);
            throw e;
        }
    }

    private boolean isRedirected(Map<String, List<String>> header) {
        for (String hv : header.get(null)) {
            if (hv.contains(" 301 ") || hv.contains(" 302 ")) {
                return true;
            }
        }
        return false;
    }

}