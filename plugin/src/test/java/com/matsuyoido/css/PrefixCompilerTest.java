package com.matsuyoido.css;

import static org.assertj.core.api.Assertions.assertThat;
import java.io.File;
import java.io.IOException;

import com.matsuyoido.caniuse.CanIUse;
import com.matsuyoido.plugin.PathUtil;

import org.junit.Before;
import org.junit.Test;

/**
 * PrefixerTest
 */
public class PrefixCompilerTest {

    private CanIUse canIUse;
    private PrefixCompiler prefixer;

    @Before
    public void setup() throws IOException {
        this.canIUse = new CanIUse(new File(PathUtil.classpathResourcePath("caniuse/data.json")));
        this.prefixer = new PrefixCompiler(canIUse.getCssSupports());
    }

    // @see https://github.com/postcss/autoprefixer/tree/master/test/cases

    @SuppressWarnings("unused")
    private String flexCss = String.join(System.lineSeparator(), 
        ".a {",
            "display: flex;",
            "flex-flow: row;",
            "order: 0;",
            "flex: 0 1 2;",
            "transition: flex 200ms;",
            "flex-direction: row;",
            "justify-content: flex-start;",
            "align-items: flex-start;",
            "flex-wrap: nowrap;",
            "align-content: flex-start;",
            "align-self: flex-start;",
        "}"
        );
    private String mediaCss = String.join(System.lineSeparator(), 
        "@media screen and (min-width:480px) { ",
            ".b {",
                "display: inline-flex;",
                "flex: auto;",
            "}",
        "}"
        );
    private String supportCss = String.join(System.lineSeparator(), 
        "@supports (display: flex) {",
            ".foo {",
              "display: flex;",
            "}",
        "}"
        );

    private String viewportCss = String.join(System.lineSeparator(),
        "@viewport {",
            "width: device-width;",
        "}"
        );

    private String lineClampCss = String.join(System.lineSeparator(),
        "a {",
            "display: -webkit-box;",
            "-webkit-box-orient: vertical;",
            "-webkit-line-clamp: 3;",
            "overflow: hidden;",
        "}"
        );

    @Test
    public void addPrefix_mediaScreen() {
        String result = prefixer.addPrefix(this.mediaCss);

        assertThat(result)
                .containsOnlyOnce("@media screen and (min-width:480px) {")
                .containsOnlyOnce("display:inline-flex")
                .containsOnlyOnce("display:-webkit-inline-flex")
                .containsOnlyOnce("display:-ms-inline-flex")
                .containsOnlyOnce("display:-moz-inline-flex")
                .contains("flex:auto")
                .containsOnlyOnce("-ms-flex:auto")
                .containsOnlyOnce("-moz-flex:auto")
                .containsOnlyOnce("-webkit-flex:auto")
        ;
        System.out.println(result);
    }

    @Test
    public void addPrefix_support() {
        String result = prefixer.addPrefix(this.supportCss);

        assertThat(result)
                .containsOnlyOnce("@supports")
                .containsOnlyOnce("(display:flex) or (display:-ms-flex) or (display:-moz-flex) or (display:-webkit-flex) or (display:flex)")
        ;
    }

    @Test
    public void addPrefix_viewport() {
        String result = prefixer.addPrefix(this.viewportCss);

        assertThat(result)
                .containsOnlyOnce("@viewport {")
                .containsOnlyOnce("width:device-width;")
        ;
    }

    @Test
    public void addPrefix_lineClamp() {
        String result = prefixer.addPrefix(this.lineClampCss);

        assertThat(result)
                .containsOnlyOnce("display:-webkit-box;")
                .containsOnlyOnce("-webkit-box-orient:vertical;")
                .containsOnlyOnce("-webkit-line-clamp:3;")
                .containsOnlyOnce("overflow:hidden;")
        ;
    }

    @Test
    public void addPrefix_addedPrefix() {
        String css = String.join(System.lineSeparator(),
        "a {",
            "display: -webkit-flex;",
            "display: flex;",
            "flex: auto;",
            "-webkit-flex: auto;",
        "}"
        );

        String result = prefixer.addPrefix(css);

        assertThat(result)
                .containsOnlyOnce("display:flex")
                .containsOnlyOnce("display:-webkit-flex")
                .containsOnlyOnce("display:-ms-flex")
                .containsOnlyOnce("display:-moz-flex")
                .contains("flex:auto")
                .containsOnlyOnce("-ms-flex:auto")
                .containsOnlyOnce("-moz-flex:auto")
                .containsOnlyOnce("-webkit-flex:auto")
        ;
    }

    @Test
    public void addPrefix_textCombineUpright() {
        String css = String.join(System.lineSeparator(),
        "span {",
            "text-combine-upright: all;",
        "}"
        );
        assertThat(prefixer.addPrefix(css))
                .containsOnlyOnce("text-combine-upright:all;")
                .containsOnlyOnce("-ms-text-combine-horizontal:all;")
                .containsOnlyOnce("-webkit-text-combine:horizontal;")
        ;

        css = String.join(System.lineSeparator(),
        "span {",
            "text-combine-upright: all;",
            "-ms-text-combine-horizontal: all;",
            "-webkit-text-combine: horizontal;",
        "}"
        );
        assertThat(prefixer.addPrefix(css))
                .containsOnlyOnce("text-combine-upright:all;")
                .containsOnlyOnce("-ms-text-combine-horizontal:all;")
                .containsOnlyOnce("-webkit-text-combine:horizontal;")
        ;

        css = String.join(System.lineSeparator(),
        "span {",
            "-ms-text-combine-horizontal: all;",
        "}"
        );
        assertThat(prefixer.addPrefix(css))
                .containsOnlyOnce("text-combine-upright:all;")
                .containsOnlyOnce("-ms-text-combine-horizontal:all;")
                .containsOnlyOnce("-webkit-text-combine:horizontal;")
        ;

        css = String.join(System.lineSeparator(),
        "span {",
            "-webkit-text-combine: horizontal;",
        "}"
        );
        assertThat(prefixer.addPrefix(css))
                .containsOnlyOnce("text-combine-upright:all;")
                .containsOnlyOnce("-ms-text-combine-horizontal:all;")
                .containsOnlyOnce("-webkit-text-combine:horizontal;")
        ;
    }

}