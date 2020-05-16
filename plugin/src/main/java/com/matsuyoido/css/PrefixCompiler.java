package com.matsuyoido.css;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.helger.css.ECSSVersion;
import com.helger.css.ICSSWriterSettings;
import com.helger.css.decl.CSSDeclaration;
import com.helger.css.decl.CSSExpression;
import com.helger.css.decl.CSSExpressionMemberTermSimple;
import com.helger.css.decl.CSSMediaRule;
import com.helger.css.decl.CSSStyleRule;
import com.helger.css.decl.CSSSupportsConditionDeclaration;
import com.helger.css.decl.CSSSupportsConditionNested;
import com.helger.css.decl.CSSSupportsRule;
import com.helger.css.decl.CascadingStyleSheet;
import com.helger.css.decl.ECSSSupportsConditionOperator;
import com.helger.css.decl.ICSSTopLevelRule;
import com.helger.css.reader.CSSReader;
import com.helger.css.writer.CSSWriterSettings;
import com.matsuyoido.caniuse.SupportData;
import com.matsuyoido.caniuse.SupportLevel;
import com.matsuyoido.caniuse.SupportStatus;
import com.matsuyoido.caniuse.VersionPrefixer;

/**
 * Prefixer
 */
public class PrefixCompiler {

    private ICSSWriterSettings writeSetting;
    // key: property
    private Map<String, CssSupport> supportMap = new HashMap<>();

    public PrefixCompiler(ICSSWriterSettings settings, List<SupportData> supportData) {
        supportData.forEach(data -> {
            setupCssPrefixer(data.getKey(), data.getSupports());
        });
        this.writeSetting = settings;
    }

    public PrefixCompiler(List<SupportData> supportData) {
        this(CSSWriterSettings.DEFAULT_SETTINGS, supportData);
    }

    /**
     * 
     * @param file CSS File
     * @return Added prefixer css
     */
    public String addPrefix(File file) {
        return addPrefix(CSSReader.readFromFile(file, StandardCharsets.UTF_8, ECSSVersion.CSS30));
    }

    /**
     * 
     * @param cssText CSS
     * @return Added prefixer css
     */
    public String addPrefix(String cssText) {
        return addPrefix(CSSReader.readFromString(cssText, StandardCharsets.UTF_8, ECSSVersion.CSS30));
    }


	private String addPrefix(CascadingStyleSheet css) {
        List<ICSSTopLevelRule> mainCssRule = new ArrayList<>();
        css.getAllRules().forEach(r -> {
            if (r instanceof CSSStyleRule) {
                mainCssRule.add(createPrefixedRule((CSSStyleRule) r));
            } else if (r instanceof CSSMediaRule) {
                CSSMediaRule rule = (CSSMediaRule) r;
                Stream<ICSSTopLevelRule> ruleStream = rule.getAllRules().stream()
                        .map(innerRule -> (innerRule instanceof CSSStyleRule)
                                ? createPrefixedRule((CSSStyleRule) innerRule)
                                : innerRule);
                rule.removeAllRules();
                ruleStream.forEach(rule::addRule);
                mainCssRule.add(rule);
            } else if (r instanceof CSSSupportsRule) {
                CSSSupportsRule rule = (CSSSupportsRule) r;
                CSSSupportsConditionNested support = new CSSSupportsConditionNested();
                rule.getAllSupportConditionMembers().forEach(supportMember -> {
                    if (supportMember instanceof CSSSupportsConditionDeclaration) {
                        CSSSupportsConditionDeclaration supportDeclaration = (CSSSupportsConditionDeclaration) supportMember;
                        createPrefixedDeclaration(supportDeclaration.getDeclaration()).stream()
                                .map(CSSSupportsConditionDeclaration::new).forEach(newDeclaration -> {
                                    support.addMember(newDeclaration);
                                    support.addMember(ECSSSupportsConditionOperator.OR);
                                });
                    }
                    support.addMember(supportMember);
                });
                Stream<ICSSTopLevelRule> ruleStream = rule.getAllRules().stream().map(innerRule -> {
                    if (innerRule instanceof CSSStyleRule) {
                        return createPrefixedRule((CSSStyleRule) innerRule);
                    }
                    return innerRule;
                });

                rule.removeAllSupportsConditionMembers();
                rule.addSupportConditionMember(support);
                rule.removeAllRules();
                ruleStream.forEach(rule::addRule);
                mainCssRule.add(rule);
            } else {
                mainCssRule.add(r);
            }
        });

        StringBuilder value = new StringBuilder();
        appendNewLineIfNotMinify(value, String.format("@charset \"%s\";", StandardCharsets.UTF_8.name()));
        css.getAllImportRules().forEach(importCss -> appendNewLineIfNotMinify(value, importCss.getAsCSSString(writeSetting)));
        css.getAllNamespaceRules().forEach(nameSpaceCss -> appendNewLineIfNotMinify(value, nameSpaceCss.getAsCSSString(writeSetting)));
        mainCssRule.forEach(mainCss -> appendNewLineIfNotMinify(value, mainCss.getAsCSSString(writeSetting)));
        return value.toString();
    }

    private void appendNewLineIfNotMinify(StringBuilder builder, String value) {
        builder.append(value);
        if (!this.writeSetting.isOptimizedOutput()) {
            builder.append(this.writeSetting.getNewLineMode().getText());
        }
    }

    private CSSStyleRule createPrefixedRule(CSSStyleRule rule) {
        CSSStyleRule newCss = new CSSStyleRule();
        rule.getAllSelectors().forEach(newCss::addSelector);
        // already added prefix remove
        Map<String, CSSDeclaration> declarations = new HashMap<>();
        rule.getAllDeclarations().forEach(declaration -> {
            CSSDeclaration declarationValue = checkCustomProperty(declaration);
            String property = removedPrefixer.apply(declarationValue.getProperty());
            declarations.put(property, declarationValue);
        });
        declarations.values().forEach(declaration -> {
            createPrefixedDeclaration(declaration).forEach(newCss::addDeclaration);
        });
        return newCss;
    }

    private Collection<CSSDeclaration> createPrefixedDeclaration(
            CSSDeclaration declaration) {
        String cssProperty = removedPrefixer.apply(declaration.getProperty());
        String cssValue = removedPrefixer.apply(declaration.getExpressionAsCSSString());
        if (this.supportMap.containsKey(cssProperty)) {
            CssSupport support = this.supportMap.get(cssProperty);
            Map<String, CSSDeclaration> prefixerDeclaration = new HashMap<>();
            if (!support.anySupport.isEmpty()) {
                prefixerDeclaration.put("", new CSSDeclaration(cssProperty, declaration.getExpression()));
                support.anySupport.stream()
                        .flatMap(status -> status.getSupportVersionMap().entrySet().stream()
                            .map(this::extractPrefixer)
                            .filter(Objects::nonNull))
                        .distinct()
                        .forEach(prefixer -> 
                            prefixerDeclaration.put(prefixer,
                                new CSSDeclaration(String.format("-%s-%s", prefixer, cssProperty),
                                        declaration.getExpression()))
                            
                        );
            } else {
                prefixerDeclaration.put("", new CSSDeclaration(cssProperty,
                        new CSSExpression().addMember(new CSSExpressionMemberTermSimple(cssValue))));
                support.specificSupport.entrySet().stream().filter(es -> cssValue.contains(es.getKey())).findFirst()
                        .ifPresent(es -> {
                            es.getValue().stream()
                                .flatMap(status -> status.getSupportVersionMap().entrySet().stream()
                                    .map(this::extractPrefixer)
                                    .filter(Objects::nonNull))
                                .distinct()
                                .forEach(prefixer -> {
                                    CSSExpression expression = new CSSExpression();
                                        expression.addMember(new CSSExpressionMemberTermSimple(
                                                String.format("-%s-%s", prefixer, cssValue)));
                                        prefixerDeclaration.put(prefixer,
                                                new CSSDeclaration(cssProperty, expression));
                                });
                        });
            }
            return prefixerDeclaration.values();
        }

        Collection<CSSDeclaration> customDeclarations = customPrefixDeclaration(cssProperty, cssValue);
        return customDeclarations.isEmpty() ? Collections.singleton(declaration) : customDeclarations;
    }

    private Function<String, String> removedPrefixer = val -> {
        String webkit = "-webkit-";
        if (val.startsWith(webkit)) {
            return val.substring(webkit.length());
        }
        String moz = "-moz-";
        if (val.startsWith(moz)) {
            return val.substring(moz.length());
        }
        String ms = "-ms-";
        if (val.startsWith(ms)) {
            return val.substring(ms.length());
        }
        String o = "-o-";
        if (val.startsWith(o)) {
            return val.substring(o.length());
        }
        return val;
    };
    private String extractPrefixer(Entry<VersionPrefixer, SupportLevel> entry) {
        return (entry.getValue().name().endsWith("_WITH_PREFIX")) ?
            entry.getKey().getPrefixer() : null;
    }

    //#region caniuse 以外の自流設定
    private CSSDeclaration checkCustomProperty(CSSDeclaration declaration) {
        String cssProperty = declaration.getProperty();
        if (cssProperty.endsWith("text-combine-horizontal")) { 
            return new CSSDeclaration("text-combine-upright", declaration.getExpression());
        } else if (cssProperty.endsWith("text-combine")) {
            return new CSSDeclaration("text-combine-upright",
                        new CSSExpression().addMember(new CSSExpressionMemberTermSimple("all")));
        }
        return declaration;
    }
    private Collection<CSSDeclaration> customPrefixDeclaration(String cssProperty, String cssValue) {
        List<CSSDeclaration> declarations = new ArrayList<>();
        if (cssProperty.equals("text-combine-upright") && (cssValue.equals("all") || cssValue.startsWith("digits"))) {
            declarations.add(new CSSDeclaration("text-combine-upright",
                        new CSSExpression().addMember(new CSSExpressionMemberTermSimple(cssValue))));
            declarations.add(new CSSDeclaration("-ms-text-combine-horizontal",
                        new CSSExpression().addMember(new CSSExpressionMemberTermSimple(cssValue))));
            declarations.add(new CSSDeclaration("-webkit-text-combine",
                        new CSSExpression().addMember(new CSSExpressionMemberTermSimple("horizontal"))));
            return declarations;
        }

        return declarations;
    }
    //#endregion

    //#region initialize setup

    /**
     * @see https://github.com/postcss/autoprefixer/blob/master/data/prefixes.js
     * @param keyword
     * @param supports
     */
    private void setupCssPrefixer(String keyword, List<SupportStatus> supports) {
        switch (keyword) {
        case "border-radius":
            Stream.of("border-radius", "border-top-left-radius", "border-top-right-radius",
                    "border-bottom-right-radius", "border-bottom-left-radius")
                    .forEach(prop -> addSupport(prop, supports));
            break;
        case "css-boxshadow":
            addSupport("box-shadow", supports);
            break;
        case "css-animation":
            Stream.of("animation", "animation-name", "animation-duration", "animation-delay", "animation-direction",
                    "animation-fill-mode", "animation-iteration-count", "animation-play-state",
                    "animation-timing-function", "@keyframes").forEach(prop -> addSupport(prop, supports));
            break;
        case "css-transitions":
            Stream.of("transition", "transition-property", "transition-duration", "transition-delay",
                    "transition-timing-function").forEach(prop -> addSupport(prop, supports));
            break;
        case "transforms2d":
            Stream.of("transform", "transform-origin").forEach(prop -> addSupport(prop, supports));
            break;
        case "transforms3d":
            Stream.of("perspective", "perspective-origin", "transform-style", "backface-visibility")
                    .forEach(prop -> addSupport(prop, supports));
            break;
        case "css-gradients":
            Stream.of("background", "background-image", "border-image", "mask", "list-style", "list-style-image",
                    "content", "mask-image").forEach(prop -> {
                        Stream.of("linear-gradient", "repeating-linear-gradient", "radial-gradient",
                                "repeating-radial-gradient").forEach(cssValue -> addSupport(prop, cssValue, supports));
                    });
            break;
        case "css3-boxsizing":
            addSupport("box-sizing", supports);
            break;
        case "css-filters":
            addSupport("filter", supports);
            break;
        case "css-filter-function":
            Stream.of("background", "background-image", "border-image", "mask", "list-style", "list-style-image",
                    "content", "mask-image").forEach(prop -> {
                        Stream.of("blur", "brightness", "contrast", "drop-shadow", "grayscale", "hue-rotate", "invert",
                                "opacity", "saturate", "sepia")
                                .forEach(cssValue -> addSupport(prop, cssValue, supports));
                    });
            break;
        case "css-backdrop-filter":
            addSupport("backdrop-filter", supports);
            break;
        case "css-element-function":
            Stream.of("background", "background-image", "border-image", "mask", "list-style", "list-style-image",
                    "content", "mask-image").forEach(prop -> addSupport(prop, "element", supports));
            break;
        case "multicolumn":
            Stream.of("columns", "column-width", "column-gap", "column-rule", "column-rule-color", "column-rule-width",
                    "column-count", "column-rule-style", "column-span", "column-fill", "break-before", "break-after",
                    "break-inside").forEach(prop -> addSupport(prop, supports));
            break;
        case "user-select-none":
            addSupport("user-select", supports);
            break;
        case "flexbox":
            addSupport("display", "flex", supports);
            Stream.of("flex", "flex-grow", "flex-shrink", "flex-basis", "flex-direction", "flex-wrap", "flex-flow",
                    "justify-content", "order", "align-items", "align-self", "align-content")
                    .forEach(prop -> addSupport(prop, supports));
            break;
        case "background-img-opts":
            Stream.of("background-origin", "background-size").forEach(prop -> addSupport(prop, supports));
            break;
        case "background-clip-text":
            addSupport("background-clip", supports);
            break;
        case "font-feature":
            Stream.of("font-feature-settings", "font-variant-ligatures", "font-language-override")
                    .forEach(prop -> addSupport(prop, supports));
            break;
        case "font-kerning":
        case "border-image":
        case "text-size-adjust":
        case "text-overflow":
            addSupport(keyword, supports);
            break;
        case "css-hyphens":
            addSupport("hyphens", supports);
            break;
        case "css3-tabsize":
            addSupport("tab-size", supports);
            break;
        case "intrinsic-width":
            Stream.of("width", "min-width", "max-width", "height", "min-height", "max-height", "inline-size",
                    "min-inline-size", "max-inline-size", "block-size", "min-block-size", "max-block-size", "grid",
                    "grid-template", "grid-template-rows", "grid-template-columns", "grid-auto-columns",
                    "grid-auto-rows").forEach(prop -> {
                        Stream.of("max-content", "min-content", "fit-content", "fill", "fill-available", "stretch")
                                .forEach(cssValue -> addSupport(prop, cssValue, supports));
                    });
            break;
        case "css3-cursors-newer":
            Stream.of("zoom-in", "zoom-out", "grab", "grabbing").forEach(prop -> addSupport(prop, supports));
            break;
        case "css-sticky":
            addSupport("position", supports);
            break;
        case "pointer":
            addSupport("touch-action", supports);
            break;
        case "text-decoration":
            Stream.of("text-decoration-style", "text-decoration-color", "text-decoration-line", "text-decoration",
                    "text-decoration-skip", "text-decoration-skip-ink").forEach(prop -> addSupport(prop, supports));
            break;
        case "css-masks":
            Stream.of("mask-clip", "mask-composite", "mask-image", "mask-origin", "mask-repeat", "mask-border-repeat",
                    "mask-border-source", "mask", "mask-position", "mask-size", "mask-border", "mask-border-outset",
                    "mask-border-width", "mask-border-slice").forEach(prop -> addSupport(prop, supports));
            break;
        case "css-clip-path":
            addSupport("clip-path", supports);
            break;
        case "css-boxdecorationbreak":
            addSupport("box-decoration-break", supports);
            break;
        case "object-fit":
            Stream.of("object-fit", "object-position").forEach(prop -> addSupport(prop, supports));
            break;
        case "css-shapes":
            Stream.of("shape-margin", "shape-outside", "shape-image-threshold")
                    .forEach(prop -> addSupport(prop, supports));
            break;
        case "css-text-align-last":
            addSupport("text-align-last", supports);
            break;
        case "css-crisp-edges":
            addSupport("image-rendering", supports);
            break;
        case "css-logical-props":
            Stream.of("border-inline-start", "border-inline-end", "margin-inline-start", "margin-inline-end",
                    "padding-inline-start", "padding-inline-end", "border-block-start", "border-block-end",
                    "margin-block-start", "margin-block-end", "padding-block-start", "padding-block-end")
                    .forEach(prop -> addSupport(prop, supports));
            break;
        case "css-appearance":
            addSupport("appearance", supports);
            break;
        case "css-snappoints":
            Stream.of("scroll-snap-type", "scroll-snap-coordinate", "scroll-snap-destination", "scroll-snap-points-x",
                    "scroll-snap-points-y").forEach(prop -> addSupport(prop, supports));
            break;
        case "css-regions":
            Stream.of("flow-into", "flow-from", "region-fragment").forEach(prop -> addSupport(prop, supports));
            break;
        case "css-image-set":
            Stream.of("background", "background-image", "border-image", "cursor", "mask", "mask-image", "list-style",
                    "list-style-image", "content").forEach(prop -> addSupport(prop, "image-set", supports));
            break;
        case "css-writing-mode":
            addSupport("writing-mode", supports);
            break;
        case "css-cross-fade":
            Stream.of("background", "background-image", "border-image", "mask", "list-style", "list-style-image",
                    "content", "mask-image").forEach(prop -> addSupport(prop, "cross-fade", supports));
            break;
        case "text-emphasis":
            Stream.of("text-emphasis", "text-emphasis-position", "text-emphasis-style", "text-emphasis-color")
                    .forEach(prop -> addSupport(prop, supports));
            break;
        case "css-grid":
            addSupport("display", "grid", supports);
            Stream.of("grid-template-columns", "grid-template-rows", "grid-row-start", "grid-column-start",
                    "grid-row-end", "grid-column-end", "grid-row", "grid-column", "grid-area", "grid-template",
                    "grid-template-areas", "place-self", "grid-column-align", "grid-row-align")
                    .forEach(prop -> addSupport(prop, supports));
            break;
        case "css-text-spacing":
            addSupport("text-spacing", supports);
            break;
        case "css-unicode-bidi":
            addSupport("unicode-bidi", supports);
            break;
        case "css-overscroll-behavior":
            addSupport("overscroll-behavior", supports);
            break;
        case "css-color-adjust":
            addSupport("color-adjust", supports);
            break;
        case "css-text-orientation":
            addSupport("text-orientation", supports);
            break;
        case "css-line-clamp":
            addSupport("display", "box",
                    supports.stream()
                            .filter(stat -> stat.getBrowser().equals("chrome") || stat.getBrowser().equals("firefox"))
                            .collect(Collectors.toList()));
            addSupport("line-clamp", supports);
            break;
        }
    }

    private void addSupport(String property, List<SupportStatus> support) {
        this.addSupport(property, null, support);
    }

    private void addSupport(String property, String value, List<SupportStatus> support) {
        this.supportMap.computeIfAbsent(property, (v) -> new CssSupport());
        if (value == null) {
            this.supportMap.get(property).anySupport = support;
        } else {
            this.supportMap.get(property).specificSupport.put(value, support);
        }
    }

    //#endregion
}

class CssSupport {
    List<SupportStatus> anySupport = Collections.emptyList();
    Map<String, List<SupportStatus>> specificSupport = new HashMap<>();
}