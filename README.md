# gradle-frontend-plugin

## Feature

1. sass compile by [jsass](https://gitlab.com/jsass/jsass)
1. css minify by [YUI Compressor](https://mvnrepository.com/artifact/com.yahoo.platform.yui/yuicompressor)
1. js minify by YUI Compressor or [Google Closure Compiler](https://github.com/google/closure-compiler)


## Tasks

1. `$ gradlew sassCompile`
1. `$ gradlew cssMinify`
1. `$ gradlew jsMinify`

### sassCompile

```gradle
frontend {
    css {
        sassDir = file("$projectDir/src/main/sass")
        cssDir = file("$projectDir/src/main/resources/static/css")
        minifyEnable = true // If you compile and minify, set true.
        originDeleted = true // If yout want to delete precompile css file, set true.
        outDir = file("$projectDir/src/main/resources/static/css")
    }
}
```

### cssMinify

```gradle
frontend {
    css {
        cssDir = file("$projectDir/src/main/resources/static/css")
        originDeleted = true // If yout want to delete precompile css file. set true
        outDir = file("$projectDir/src/main/resources/static/css")
    }
}
```

### jsMinify

```gradle
frontend {
    js {
        jsDir = file("$projectDir/src/main/resources/static/js")
        outDir = file("$projectDir/src/main/resources/static/js")
        type = 'yahoo' // If you want to specify minify compiler. 'google' (Default) or 'yahoo'
    }
}
```



