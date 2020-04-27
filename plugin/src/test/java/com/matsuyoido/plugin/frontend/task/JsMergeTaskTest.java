package com.matsuyoido.plugin.frontend.task;

import static org.assertj.core.api.Assertions.assertThat;
// import java.io.File;

// import com.matsuyoido.plugin.PathUtil;

import org.gradle.api.tasks.incremental.IncrementalTaskInputs;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/**
 * JsMergeTaskTest
 */
@Ignore("TaskのテストはPlugin の結合テストで担保することとする")
public class JsMergeTaskTest {

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    // private static final String SOURCE_FILE_DIR = PathUtil.classpathResourcePath("mergeCompile");

    private IncrementalTaskInputs inputs = new MockIncrementalTaskInputs();
    private JsMergeTask task = ProjectBuilder.builder()
                                               .build()
                                               .getTasks().create("test", JsMergeTask.class);

    @Before
    public void setup() {
        // task.setJsMapDirectory(new File(SOURCE_FILE_DIR))
        //     .setOutputFileDirectory(tempFolder.getRoot());
    }

    @Test
    public void mergeJavascript() {
        task.mergeJavascript(inputs);

        assertThat(tempFolder.getRoot().listFiles()).allSatisfy(file -> {
            assertThat(file).isFile()
                            .hasName("test.min.js")
                            .hasContent("'use strict';function Test(){this.name=0};const test=\"\";");
        });
    }
}