# Konversation [![License: MIT][mit-image]][mit-url] [![Stars][star-img]][star-url]

Konversation is a tool to generate rich and diversified responses to the user of a voice application. You can support multiple platforms
and different output devices at once, as watches, speaker, smart displays and TVs. Multiple languages are also supported.   
This library can be used in Kotlin, Java, node.js and in the browser. You can define your intent model in ksv files. Which you can export
directly to the Alexa Developer console, together with the ASK SDK you can deploy it directly to Amazon if you like.

Konversation has integrations for multiplatform Voice Assistant and Chatbot libraries like [Dialog] for JVM languages like Kotlin and Java
and [ChatbotBase] for script languages like TypeScript or JavaScript.

## Code usage
Here is a sample how a help intent can be implemented with Konversation and Dialog or ChatbotBase.

### Konversation file (*.kvs)
In this sample you will see how a diversified output can be generated with Konversation. If you want to know the full syntax please check the [file format documentation][file-formats]. 
```
@AlexaName("Amazon.HelpIntent")
HelpIntent:
- I can tell you about
- What do you want to know?
+
- Jokes, Offers or Payment.
- Offers, Jokes or Payment.
- Payment, Offers or Jokes.
+
- Please {choose|select} one topic.
?1 In which topic are you intersted in?
[Jokes] [Offers] [Payment]
```

This small sample above will create one of those random outputs:
- I can tell you about Jokes, Offers or Payment. Please choose one topic.
- I can tell you about Offers, Jokes or Payment. Please choose one topic.
- I can tell you about Payment, Offers or Jokes. Please choose one topic.
- I can tell you about Jokes, Offers or Payment. Please select one topic.
- I can tell you about Offers, Jokes or Payment. Please select one topic.
- I can tell you about Payment, Offers or Jokes. Please select one topic.
- What do you want to know? Jokes, Offers or Payment. Please choose one topic.
- What do you want to know? Offers, Jokes or Payment. Please choose one topic.
- What do you want to know? Payment, Offers or Jokes. Please choose one topic.
- What do you want to know? Jokes, Offers or Payment. Please select one topic.
- What do you want to know? Offers, Jokes or Payment. Please select one topic.
- What do you want to know? Payment, Offers or Jokes. Please select one topic.


### Dialog
Here is a sample how a help intent can be implemented in kotlin with [Dialog]:
```
@IntentHandler
class HelpIntentHandler : MultiPlatformIntentHandler {
    override val intentNames: List<String>
        get() = listOf(AMAZON_HELP, HELP)

    override fun handleDialogflow(input: DialogflowHandler): DialogflowResponseBuilder {
        val output = input.loadKonversation("HelpIntent").createOutput()
        return input.responseBuilder
            .withGoogleSimpleResponse(output)
            .withGoogleReprompts(output)
            .withGoogleSuggestions(output)
            .expectUserAnswer(true)
    }

    override fun handleAlexa(input: HandlerInput): ResponseBuilder {
        val output = input.loadKonversation("HelpIntent").createOutput()
        return input.responseBuilder
            .withSpeech(output)
            .withReprompt(output)
            .expectUserAnswer(true)
    }
}
```

### ChatbotBase
The same sample as above when you use [ChatbotBase]:
```
export class HelpIntent implements IntentHandler {
    createOutput(input: Input, translations: TranslationProvider): Output | Promise<Output> {
        return new Reply(input, translations).addReply("HelpIntent");
    }

    isSupported(input: Input): boolean {
        return input.intent == "HelpIntent" || input.intent == "Amazon.HelpIntent";
    }
}
```
Please note that ChatbotBase has currently no support to apply the suggestions or reprompts.

## Usage export
The export required the command line interface or the gradle plugin (the gradle plugin can currently export only Alexa).

### Example project
Here is a fictive shop interaction model without the prompts, repromts and suggestions:   
**Shop.kvs**:
```
ProductSearch:
! {|I want to} buy a {{product}}
! I need {{count:number}} {{product}}

BuyIntent:
! {|Buy|Select|Order} {product|number|article|thing|gadget} {{index:number}}
```

This will generate 2 Intents with in total 23 utterances. In single brackets are alternatives which are divided by the pipe symbol.
In the `BuyIntent` are two permutations which define 4*5=20 utterances; just written in one single line and still readable. Here are some samples:
- Product 1
- Buy article 2
- Select number 3

In the utterance `I need {{count:number}} {{product}}` are two slot values (Amazon naming) or entities (Google naming). Those variables are marked by double brackets and have a optional name in the front of the type. In the sample is the name "count" with the data type "number" used amd the custom slot type/entity "product".

The required `product.values` definition could look like this:
```
Google Home Mini
Google Home
Nest Hub
Echo Dot
Echo Show
...
```
For more details check the [file format documentation][file-formats].

### Export to Amazon
To export the intent schema for a skill named "Test" into the file "schema.json" you need to execute:

    $ konversation --export-alexa schema.json -invocation "Test" shop.kvs product.values
    
Please note that the order of the commands are not important you can also use wildcards as `*.kvs` or `slots/*.values`.

### Export to Dialogflow
To export the intents to Dialogflow for a skill named "Test" you need to execute:

    $ konversation --export-dialogflow . -invocation "Test" shop.kvs product.values
    
This will generate a `dialogflow-.zip` file in the current (`.`) directory containing all the intents, utterances and entities. We are aware that the naming of the output file is suboptimal and will change until the final release of the cli version 1.1.0.  



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

[Dialog]: https://github.com/rewe-digital-incubator/dialog
[ChatbotBase]: https://github.com/rekire/ChatbotBase
[file-formats]: https://github.com/rewe-digital-incubator/Konversation/blob/master/file-formats.md