package com.matsuyoido.plugin.frontend;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.util.List;

import com.matsuyoido.caniuse.SupportData;
import com.matsuyoido.plugin.PathUtil;
import com.matsuyoido.plugin.frontend.extension.PrefixerExtension;

import org.junit.Test;

/**
 * PrefixerCanIUseTest
 */
public class PrefixerCanIUseTest {

    @Test
    public void getCssSupport() throws IOException {
        PrefixerExtension extension = new PrefixerExtension();
        extension.setCaniuseData(new File(PathUtil.classpathResourcePath("caniuse/data.json")));
        extension.setChrome("all");

        PrefixerCanIUse caniuse = new PrefixerCanIUse(extension);

        List<SupportData> result = caniuse.getCssSupports();

        assertThat(result).isNotEmpty();
    }


}