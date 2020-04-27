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
* 複数のSASSファイルを別ディレクトリで管理をしたい
    - 例えば、 `src/vendor/scss` と `src/main/sass` の2フォルダのファイルを、1ディレクトリ or 別ディレクトリにコンパイルしたい などなど...


## 利用をする

https://plugins.gradle.org/plugin/com.matsuyoido.frontend にアクセスして、pluginをもらってきてください。

利用するシーンに合わせて、以下の設定をしてみてください。

## SASS(SCSS) -> CSS にしたい

### build.gradle

```gradle
frontend {
    style {
        scss {
            inDir = file("$projectDir/src/main/sass")
            outDir = file("$projectDir/src/main/resources/static/css")
        }
    }
}
```

### 実行

`$ gradlew sassCompile`


## SASS(SCSS) -> CSS にしたい(フレームワークのファイルと、自分でカスタマイズしたものを同時に変換したい)

```gradle
frontend {
    style {
        scss {
            inDir = file("$rootDir/src/vendor")
            outDir = file("$projectDir/src/main/resources/static/vendor)
        }
        scss {
            inDir = file("$rootDir/src/sass")
            outDir = file("$projectDir/src/main/resources/static/css")
        }
    }
}
```

※scss のブロックは複数設定が可能

### 実行

`$ gradlew sassCompile`


## SASS(SCSS) -> min.css にする(prefixer もつけておきたい)

### build.gradle

```gradle
frontend {
    setting {
        prefixer {
            // (ダウンロード可能 & 最新でOKであれば、最新のファイルをダウンロードするため、指定不要)
            // caniuseData = file("$rootDir/caniuse/data.json")
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
    style {
        scss {
            inDir = file("$projectDir/src/main/sass")
            outDir = file("$projectDir/src/main/resources/static/css")
            enableMinify = true
            addPrefixer = true
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
    style {
        scss {
            inDir = file("$projectDir/src/main/sass")
            outDir = file("$rootDir/temp/check/${project.name}")
        }
        css {
            inDir = file("$rootDir/temp/check/${project.name}")
            outDir = file("$projectDir/src/main/resources/static/css")
        }
    }
}
```

### 実行

`$ gradlew sassCompile cssMinify`


## CSS -> min.css にする、 JS -> min.js にする

### build.gradle

```gradle
frontend {
    style {
        css {
            inDir = file("$projectDir/src/main/resources/static/css")
            outDir = file("$projectDir/src/main/resources/static/css")
        }
    }
    script {
        js {
            inDir = file("$projectDir/src/main/resources/static/js")
            outDir = file("$projectDir/src/main/resources/static/js")
        }
    }
}
```

### 実行

`$ gradlew cssMinify jsMinify`


## JS を部品化して実装をする。

```gradle
frontend {
    style {
        js {
            inDir = file("$projectDir/src/main/js")
            outDir = file("$projectDir/src/main/resources/static/js")
        }
    }
}
```

### src/main/js 内の .js.mapファイル。

※mapファイルの中でも最小限だけの構成でOK。

```
.js.map の例
{
    "sources": ["your/specified/include/js/file/path.js", "./../relative/js/path.js"]
}
```

### 実行

`$ gradlew jsMerge`

