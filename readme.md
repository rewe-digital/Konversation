# Konversation

Konversation is a tool to generate rich and diversified responses to the user of a voice application. You can support multiple platforms
and different output devices at once, as watches, speaker, smart displays and TVs. Multiple languages are also supported.   
This library can be used in Kotlin, Java, node.js and in the browser. You can define your intent model in ksv files. Which you can export
directly to the Alexa Developer console, together with the ASK SDK you can deploy it directly to Amazon if you like.

## ksv file syntax

```
Help:                                    // Intent name
!Help {|me|us|how does this app work}    // Utterance how the user can call this intent
!How {works|do I use} this App           // more utterances
-You can use this app to                 // A block for a response within a block
-With this app you can                   // a line will be randomly choosen.
+                                        // Concats two blocks without a linebreak
-{read|hear}                             // elements in brackets are alterntives
+
-Recipies and offers.
-Offers and recipies.
+
~Cool isn't it?                          // This sentence should not be displayed just said.
+
~How should {we|I} continue now?         // Yet another voice only block with alternatives
~What should {we|I} do now?

?1 How can I help you{| now}?            // Reprompts when the user gives no input
?2 What should {I|we} do now?            // Second repromt used when no input was given again
[Offers] [Recipie]                       // Suggestions what the user could try (GUI only)

Hello:                                   // Second intent
-Great to see you                        // First response block of the Hello intent
// ...                                   // A comment :-)
```

The example above will creat the following utterances (also called invocation):

- Help
- Help me
- Help us
- Help how does this app work
- How works this App
- How do I use this App

You can create really tones of utterances for you skill or action. We already generated a 15GB file with it. However there are multiple
restrictions on the Alexa server side which makes it impossible to upload such crazy large files.

The displayed result could be e.g.: *"You can use this app to read Recipies and offers."* followed by just the spoken text *"Cool isn't it?
How should we continue now?"*. This is very useful if you have some visual parts which you would just repeat. The example above has also
31 more permutations.

## Usage

### Gradle plugin

The gradle plugin is an easy way to integrate the processing of the ksv files in your build process. So you just need to place your
kvs files in one place and the plugin will put the kson files into the resource directory. Kson just means **K**onver**s**ation
**O**bject **N**otation, which is basically just a JSON file with the structure for the runtime.

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

### CLI

Konversation has its own command line interface, which you can use for generating the kson files for the runtime. You can use it for
validate the syntax of the input files or to integrate it into your own build system.

#### Setup

At first you need to download the latest version of the konversation.jar. Then create in Windows a file called `konversation.cmd` e.g. in 
`c:\Windows\system32` directory (yep dirty hack sorry) and add the content:

    java -jar path/to/your/konveration.jar %1 %2 %3 %4 %5 %6 %7 %8 %9

If you use Linux or Mac create a file called `konversation.sh` in `/usr/local/bin` with this content:

    #!/bin/sh
    java -jar path/to/your/konveration.jar $1 $2 $3 $4 $5 $6 $7 $8 $9

Now in your shell/command line/terminal you can use the konversation command e.g.: `konversation -v`. This should output your installed
konversation cli version number and exit then.

Please note we know that this is not an ideal way for an installation, better ways are planed for future releases.

#### Arguments

```
Arguments for konversation:
[-help]                     Print this help
[-count]                    Count the permutations and print this to the console
[-stats]                    Print out some statistics while generation
[-cache]                    Cache everything even if an utterance has just a single permutation
[--export-alexa <OUTFILE>]  Write the resulting json to OUTFILE instead of result.json
[-invocation <NAME>]        Define the invocation name for the Alexa export
[-limit <COUNT>]            While pretty printing the json to the output file limit the utterances count per intent
[--export-kson <OUTDIR>]    Compiles the kvs file to kson resource files which are required for the runtime
[-dump]                     Dump out all intents to its own txt file
[-prettyprint]              Generate a well formatted json for easier debugging
<FILE>                      The grammar or kvs file to parse
```
    
### JVM

When you have your kson files in your resource directory you can use the runtime like this:

    val konveration = Konversation(intentName, Environment("google", "de-DE"))
    val output = konversation.createOutput() // created one randomized response
    val ssml = output.ssml
    val displayText = output.displayText
   
See also the [Output](runtime-shared/src/main/kotlin/com/rewedigital/voice/konversation/Output.kt) class for more details.

If you use [dialog](https://github.com/rewe-digital-incubator/dialog)'s
[konversation plugin](https://github.com/rewe-digital-incubator/dialog/konversation-plugin) (a sibling project) you can create a response
very easy just with a couple of lines:

    override fun handleDialogflowIntent(handler: DialogflowHandler): DialogflowResponseBuilder {
        val konversation = handler.loadKonversation("help").createOutput()

        return handler.responseBuilder
            .expectUserResponse(true)
            .withGoogleSimpleResponse(konversation)
            .withGoogleSuggestions(konversation)
            .withGoogleReprompts(konversation)
    }

A more complex example can be found in the [readme](https://github.com/rewe-digital-incubator/dialog/konversation-plugin/readme.md) file
of the [konversation plugin](https://github.com/rewe-digital-incubator/dialog/konversation-plugin).

### Node.js

At first install the dependency "konversation" with the command:

`> npm install konversation`

Then you can use this example code:

    const Konversation = require("konversation").Konversation;
    const test = Konversation("test");
    const output = test.createOutput(); // created one randomized response
    const ssml = output.ssml;
    const displayText = output.displayText;

## Deep dive for development

Here is a overview about the project structure.

### CLI

Konversation has it's own command line interface which you can use for generating the kson files for the runtime. Kson just means
**K**onver**s**ation **O**bject **N**otation, which is basically just a JSON file with the structure for the runtime.

### Shared
This module holds the shared structures which are required for the runtime and CLI.

### Runtime shared
This module holds shared structures for the different target platforms. This is required for multi platform projects (MPP).

### Runtime JVM
This is the runtime which you need to use in your Kotlin or Java applications. See also the usage section above.

### Runtime JS
This is the runtime which you need to use in your node or web applications. See also the usage section above.

### Gradle plugin
The sources of the gradle plugin the preferec way to create the kson files, within the gradle build pipeline.


## Ideas for future features
- Version hint in cli
- CI/CD
- Extra fields e.g. for visual parts of your GUI.
- Support to generate correct articles and ordinals for your responses (very likely just for German and English).
- Feature toggles to provide a special response if a toggle is set
- A GUI tool to visualize the inputs and outputs of you voice application (this is hard, pull requests welcome!).
- A preview for the GUI parts
- A [Homebrew](https://brew.sh/) [Formula](https://docs.brew.sh/Formula-Cookbook) or at least a
[tap](https://docs.brew.sh/How-to-Create-and-Maintain-a-Tap) to install the konversation cli via brew
- A [PowerShell Module](https://docs.microsoft.com/en-us/powershell/developer/module/writing-a-windows-powershell-module)