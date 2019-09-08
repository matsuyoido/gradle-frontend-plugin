package com.matsuyoido.caniuse;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;

import com.matsuyoido.plugin.PathUtil;

import org.junit.Test;

/**
 * CanIUseTest
 */
public class CanIUseTest {
    
    @Test
    public void constructor() throws IOException {
        CanIUse result = new CanIUse(new File(PathUtil.classpathResourcePath("caniuse/data.json")));

        assertThat(result.browsers).isNotEmpty();
        assertThat(result.supports).isNotEmpty();
    }

}