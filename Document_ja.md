# gradle-frontend-plugin

## こんな人に利用して欲しい

* SASS(SCSS) を利用したい。
* GradleのNode plugin を利用してきたけれど、メンテナンスをしたくない。
    - プロジェクトの構造によって、出力先の指定や対象の指定が面倒。
* SASS(SCSS) を利用するだけなのに、Nodeのプラグインを利用するための準備をする時間が惜しい。
* コンパイル前のディレクトリ構造と同じ形で出力をしたい。
* AutoPrefixer も使いたいけれど、デプロイするときだけでOK。
    - min化するのも、デプロイするときだけで良い。
* min化するのに、プラグインは1つだけで良い。


## 利用をする

https://plugins.gradle.org/plugin/com.matsuyoido.frontend にアクセスして、pluginをもらってきてください。

利用するシーンに合わせて、以下の設定をしてみてください。

## SASS(SCSS) -> CSS にしたい

### build.gradle

```gradle
frontend {
    css {
        sassDir = file("$projectDir/src/main/sass")
        cssDir = file("$projectDir/src/main/resources/static/css")
    }
}
```

### 実行

`$ gradlew sassCompile`


## SASS(SCSS) -> min.css にする(prefixer もつけておきたい)

### build.gradle

```gradle
frontend {
    css {
        sassDir = file("$projectDir/src/main/sass")
        cssDir = file("$projectDir/src/main/resources/static/css")
        outDir = file("$projectDir/src/main/resources/static/css")
        originDeleted = true
        minifyEnable = true
        prefixerEnable = true
        prefixer {
            // 1. https://github.com/Fyrd/caniuse/blob/master/data.json からファイルを取得する
            // 2. 取得したファイルを指定する
            caniuseData = file("$rootDir/caniuse/data.json")
            // サポートするためのバージョンを指定する(全部対象にする場合は、 all を使う)
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

### 実行

`$ gradlew sassCompile`


## SASS(SCSS) -> min.css にする(元のCSSファイルも確認したい, prefixerはいらない)

### build.gradle

```gradle
frontend {
    css {
        sassDir = file("$projectDir/src/main/sass")
        cssDir = file("$projectDir/src/main/resources/static/css")
        outDir = file("$projectDir/src/main/resources/static/css")
        minifyEnable = true
    }
}
```

### 実行

`$ gradlew sassCompile`


## CSS -> min.css にする、 JS -> min.js にする

### build.gradle

```gradle
frontend {
    css {
        cssDir = file("$projectDir/src/main/resources/static/css")
        outDir = file("$projectDir/src/main/resources/static/css")
    }
    js {
        jsDir = file("$projectDir/src/main/resources/static/js")
        outDir = file("$projectDir/src/main/resources/static/js")
    }
}
```

### 実行

`$ gradlew cssMinify jsMinify`


