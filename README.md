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

### sassCompile

```gradle
frontend {
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
            caniuseData = file("$projectDir/caniuse/data.json")
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


## Bug or Request

please create `New Issue` in Japanese or English.
(Japanese is better...)

## TODO memo

### many js to one min js file.

* by GoogleClosureCompiler


