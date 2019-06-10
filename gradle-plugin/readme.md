# Gradle plugin for Konversation [![Build Status][travis-image]][travis-url] [![License: MIT][mit-image]][mit-url]

The gradle plugin is an easy way to integrate the processing of the ksv files in your build process. So you just need to place your
kvs files in one place and the plugin will put the kson files into the resource directory. Kson just means **K**onver**s**ation
**O**bject **N**otation, which is basically just a JSON file with the structure for the runtime.

## Usage

This is an example for `build.gradle` file:

```
plugins {
    id 'org.jetbrains.kotlin.jvm' version '1.3.31'
    id 'org.rewedigital.konversation' version '1.0-rc1'
}

apply plugin: 'kotlin'

repositories {
    jcenter()
}

dependencies {
    compile "org.jetbrains.kotlin:kotlin-stdlib-jdk8"
    compile "org.jetbrains.kotlin:kotlin-stdlib"
    compile "org.rewedigital.voice:konversation-jvm:0.1"
}

konversation {
    invocationName = "test" // the invocation name for the alexa intent schema
}
```

You need to put your kvs or grammar files then in your sourceset in a subdirectory e.g. `src/main/konversation`
if you use multiple sourcesets that will be respected. The kson files will be placed into `build/konversation/res/main`
and will be included to your resources path, so that this files will be in your final jar.

## Tasks

The `compileKonversation` task is used to create your resource files. By default the system task `prepairResources`
will make sure that this task is executed. So there is in general no need to execute it directly.

The `exportAlexa` task will create the intent schema file in `build/konversation/alexa-intent-schema.json`,
you can override the location by using `alexaIntentSchemaFile` as in the example below:

    konversation {
        invocationName = "test"
        alexaIntentSchemaFile = "alexa-production.json"
    }
    
Please note that is relative to your project root.

## License

The MIT license (MIT)

Copyright (c) 2018 REWE Digital GmbH

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
documentation files (the "Software"), to deal in the Software without restriction, including without limitation the
rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit
persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the
Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

[travis-image]: https://travis-ci.com/rewe-digital-incubator/Konversation.svg?branch=master
[travis-url]: https://travis-ci.com/rewe-digital-incubator/Konversation
[mit-image]: https://img.shields.io/badge/License-MIT-yellow.svg
[mit-url]: https://opensource.org/licenses/MIT