# gradle-frontend-plugin

https://plugins.gradle.org/plugin/com.matsuyoido.frontend

## Feature

1. sass compile by [jsass](https://gitlab.com/jsass/jsass)
1. css minify by [ph-css](https://github.com/phax/ph-css)
1. css add prefix by [CanIUse](https://github.com/Fyrd/caniuse) when minify css
1. js minify by [YUI Compressor](https://mvnrepository.com/artifact/com.yahoo.platform.yui/yuicompressor) or [Google Closure Compiler](https://github.com/google/closure-compiler)


## Tasks

group: Compile tasks

1. `$ gradlew sassCompile`
1. `$ gradlew cssMinify`
1. `$ gradlew jsMinify`
1. `$ gradlew jsMerge`

### sassCompile

```gradle
frontend {
    // If you want to set encoding. 'windows' or 'linux' or 'mac'
    lineEnding = 'linux'
    css {
        sassDir = file("$projectDir/src/main/sass")
        cssDir = file("$projectDir/src/main/resources/static/css")
        // If you compile and minify, set true.
        minifyEnable = false
        // If yout want to delete precompile css file, set true.
        originDeleted = false
        outDir = file("$projectDir/src/main/resources/static/css")
    }
}
```

### cssMinify

```gradle
frontend {
    css {
        cssDir = file("$projectDir/src/main/resources/static/css")
        // If yout want to delete precompile css file. set true
        originDeleted = false
        outDir = file("$projectDir/src/main/resources/static/css")
    }
}
```

#### cssMinify & autoPrefixer

```gradle
frontend {
    css {
        cssDir = file("$projectDir/src/main/resources/static/css")
        outDir = file("$projectDir/src/main/resources/static/css")
        // If you want to add prefixer, set true.
        prefixerEnable = false
        prefixer {
            // specify data.json from https://github.com/Fyrd/caniuse/blob/master/data.json
            // If don't set and enable=true, data.json is used from caniuse-db-1.0.30000748
            caniuseData = file("$rootDir/caniuse/data.json")
            // If set version string(ex. "76" or "all"), specified greater version check css supports & add prefixer.
            ie = ""
            edge = ""
            chrome = ""
            firefox = ""
            safari = ""
            ios = ""
            android = ""
        }
    }
}
```

### jsMinify

```gradle
frontend {
    js {
        jsDir = file("$projectDir/src/main/resources/static/js")
        outDir = file("$projectDir/src/main/resources/static/js")
        // If you want to specify minify compiler. 'google' (Default) or 'yahoo'
        type = 'google'
    }
}
```

### jsMerge

```gradle
frontend {
    js {
        jsDir = file("$projectDir/src/main/js")
        outDir = file("$projectDir/src/main/resources/static/js")
    }
}
```

### All Extension

```gradle
frontend {
    lineEnding = 'linux'
    css {
        sassDir = file("$projectDir/your/scss/directory")
        cssDir = file("$projectDir/your/css/directory")
        outDir = file("$projectDir/your/css/output/directory")
        minifyEnable = true
        prefixerEnable = true
        originDeleted = false
        prefixer {
            caniuseData = file("$rootDir/your/fit/version/data.json")
            ie = "all"
            edge = "42"
            chrome = ""
            firefox = ""
            safari = ""
            ios = ""
            android = ""
        }
    }
    js {
        jsDir = file("$projectDir/your/javascript/directory")
        outDir = file("$projectDir/your/javascript/output/directory")
        type = 'google'
    }
}
```

## Details

### SassCompile

* target: `.scss` files
* output: `.css` file
* fileName: scss file name

### cssMinify

* target: `.css` files
* output: `.min.css` file
* fileName: css file name

### jsMinify

* target: `.js` files
* output: `.min.js` file
* fileName: js file name

### jsMerge

* target: `.js.map` files
    - this content is json file.
    - 「key: `sources`」's value is type of string array and value is js file path.
* output: `.min.js` file
* fileName: map file name


## Bug or Request

please create `New Issue` in Japanese or English.
(Japanese is better...)




