# Gradle plugin for Konversation

The gradle plugin is an easy way to integrate the processing of the ksv files in your build process. So you just need to place your
kvs files in one place and the plugin will put the kson files into the resource directory. Kson just means **K**onver**s**ation
**O**bject **N**otation, which is basically just a JSON file with the structure for the runtime.

## Usage

This is an example for `build.gradle` file:

```
plugins {
    id 'org.jetbrains.kotlin.jvm' version '1.3.11'
    id 'com.rewedigital.voice.konversation' version '0.1'
}

apply plugin: 'kotlin'

repositories {
    jcenter()
}

dependencies {
    compile "org.jetbrains.kotlin:kotlin-stdlib-jdk8"
    compile "org.jetbrains.kotlin:kotlin-stdlib"
    compile "com.rewedigital.voice:konversation-jvm:0.1"
}

konversation {
    invocationName = "test" // the invocation name for the alexa intent schema
}
```

This will place your kson files into your resource directory, which can be used by the runtime.

## Tasks

You can use the `compileKonversation` task to TODO