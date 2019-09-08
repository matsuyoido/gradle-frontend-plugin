package com.matsuyoido.plugin.frontend;

import java.io.IOException;

import org.gradle.testkit.runner.GradleRunner;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;

/**
 * PluginApplyTest
 */
public class PluginApplyTest {
    @Rule
    public TemporaryFolder projectDir = new TemporaryFolder();
    private GradleRunner runner;

    @Before
    public void seup() throws IOException {    
        runner = GradleRunner.create()
        // Test against Gradle 2.14.1 in order to maintain backwards compatibility.
        .withGradleVersion("2.14.1")
        .withProjectDir(projectDir.getRoot().getCanonicalFile())
        .withPluginClasspath()
        ;

    }
}