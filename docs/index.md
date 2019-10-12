# Konversation [![License: MIT][mit-image]][mit-url] [![Stars][star-img]][star-url]

Konversation is a tool to generate rich and diversified responses to the user of a voice application. You can support multiple platforms
and different output devices at once, as watches, speaker, smart displays and TVs. Multiple languages are also supported.   
This library can be used in Kotlin, Java, node.js and in the browser. You can define your intent model in ksv files. Which you can export
directly to the Alexa Developer console, together with the ASK SDK you can deploy it directly to Amazon if you like.

## Components
This Projects consists of a lot of modules, some parts are still in early development and not yet pushed.

### CLI [![latest brew version is 1.1.0-rc9][brew-badge-url]][cli-readme] [![latest chocolaty version is 1.1.0-rc9][chocolaty-badge-url]][cli-readme]
The command line interface (CLI) has the task to generate the from the supported input formats (kvs, grammar and values) to the output formats (kson, txt, json, and zip) depending on the arguments and use cases.  
Konversation can be installed with brew (for Linux and MacOS) and with chocolaty for Windows. For detailed instructions check the [readme][cli-readme] file.  
In version 1.1 will be added the export for Dialogflow with zip files.  
In version 1.2 provisioning will follow, that will allow you to integrate konversation into your CI/CD flow to automatically update your Alexa or Dialogflow project.

### Runtime [![latest JVM Runtime version is 1.0.1][jvm-badge-url]][runtime-jvm] [![latest JS runtime version is 1.0.3][js-badge-url]][runtime-js]
The runtime has 3 modules: a shared module for base classes which are used in the cli and each one module for JVM and JavaScript usage. So you can use Konveration in JVM languages like Kotlin and Java and JavaScript languages like ECMAScript or TypeScript.
To keep the JS interface as simple as possible there is also a small facade to use the runtime in a more natural way without using the kotlin classes.  

### Gradle plugin [![latest version is 1.0.0][gradle-badge-url]][gradle-plugin-url]
The gradle plugin create some hooks into the gradle build to create the required kson files for the runtime and add them to your resources directory. So you don't need to use the cli if you are using gradle as build system.

## Roadmap
### Version 1.0 ![released][released-badge-url]
- Create diverse outputs
- Suggestions
- Repompts
- Voice only blocks

### Version 1.1 ![release candidate][rc-badge-url]
- Dialogflow exporter
- Annotations (for fallbacks, list parameters, platform specific renamings, ...)

### Version 1.2 ![testing][testing-badge-url]
- Provisioning
- Refactoring of the cli (to make it easier to use for the gradle plugin)
- Extensions of the gradle plugin to configure the credentials and multiple targets

### IntelliJ Plugin ![work in progress][wip-badge-url]
- Syntax Highlighting
- Refactoring
- Warnings for too many utterances
- Errors for usage of not defined konversations
- Jump to definition

## Want to help?
Great! Please check the open issues and feel free to add a pull request ;-)

[mit-image]: https://img.shields.io/badge/License-MIT-yellow.svg
[mit-url]: https://opensource.org/licenses/MIT
[star-img]: https://img.shields.io/github/stars/rewe-digital-incubator/Konversation.svg?style=social&label=Star&maxAge=3600
[star-url]: https://github.com/rewe-digital-incubator/Konversation/stargazers
[jvm-badge-url]: https://img.shields.io/bintray/v/rewe-digital/Konversation/Konversation?label=jvm
[runtime-jvm]: https://github.com/rewe-digital-incubator/Konversation/blob/master/runtime-jvm/readme.md
[js-badge-url]: https://img.shields.io/npm/v/@rewe-digital/konversation?label=js
[runtime-js]: https://github.com/rewe-digital-incubator/Konversation/blob/master/runtime-js/readme.md
[brew-badge-url]: https://img.shields.io/badge/brew-1.1.0--rc9-blue
[chocolaty-badge-url]: https://img.shields.io/badge/chocolaty-1.1.0--rc9-blue
[cli-readme]: https://github.com/rewe-digital-incubator/Konversation/blob/master/cli/readme.md
[gradle-badge-url]: https://img.shields.io/badge/gradle--plugin-1.0.0-blue
[gradle-plugin-url]: https://plugins.gradle.org/plugin/org.rewedigital.konversation
[released-badge-url]: https://img.shields.io/badge/status-released-green
[rc-badge-url]: https://img.shields.io/badge/status-release%20candiate-yellow
[testing-badge-url]: https://img.shields.io/badge/status-testing-orange
[wip-badge-url]: https://img.shields.io/badge/status-in%20early%20development-red
