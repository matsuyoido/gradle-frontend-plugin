package com.matsuyoido.plugin.frontend.task;

import org.gradle.api.Action;
import org.gradle.api.tasks.incremental.IncrementalTaskInputs;
import org.gradle.api.tasks.incremental.InputFileDetails;

/**
 * MockIncrementalTaskInputs
 */
public class MockIncrementalTaskInputs implements IncrementalTaskInputs {
    @Override
    public boolean isIncremental() {
        return false;
    }

    @Override
    public void outOfDate(Action<? super InputFileDetails> action) {
    }

    @Override
    public void removed(Action<? super InputFileDetails> action) {
        // do nothing
    }
}