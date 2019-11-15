# Command line interface of Konversation [![Build Status][travis-image]][travis-url] ![Latest CLI version is 1.1.0][cli-ver-img] [![License: MIT][mit-image]][mit-url]

Konversation has its own command line interface, which you can use for generating the kson files for the runtime. You can use it for
validate the syntax of the input files or to integrate it into your own build system. Keep in mind you can also use the
[gradle plugin](../gradle-plugin/readme.md) for integrating the conversation of your ksv files in your gradle build.

## Setup

Konversation has some implementations you can use for setting up the command line tool:

### Homebrew (for Linux and MacOS)

When you have installed [Homebrew] just enter in your shell:

    brew install rekire/packages/konversation

### Chocolatey (for Windows)

When you have installed [Chocolatey] just enter in your shell:

    choco install konversation

### Manually

Download the latest cli jar from the [release page][releases] and store it somewhere you like. On **Windows** you need
to create a file called `konversation.cmd` somewhere in any directory you have in your path variable, the content should be:

    java -jar path/to/your/konveration.jar %1 %2 %3 %4 %5 %6 %7 %8 %9

If you use Linux or Mac create a file called `konversation` in `/usr/local/bin` with this content:

    #!/bin/sh
    java -jar path/to/your/konveration.jar $@

Remember that you need to make it executable with `chmod +x konversation`.

Now in your shell/command line/terminal you can use the konversation command e.g.: `konversation -v`. This should
output your installed konversation cli version number and exit then.

## Usage

You can process a single file or a directory to convert a batch of files. Currently are two formats supported: `.ksv` files and `.grammar`
files. Check also the specification of the [supported file formats](../file-formats.md).

    konversation <path/to/process> --export-kson outdir

Now are your kson files in the outdir and you can copy them into that directory that it can be read by using `require('intent.kson')` or
via a `AJAX` request without specifiying an directory.

## Arguments

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
[codecov-img]: http://codecov.io/github/rewe-digital-incubator/Konversation/coverage.svg?branch=master
[codecov-url]: http://codecov.io/github/rewe-digital-incubator/Konversation?branch=master
[mit-image]: https://img.shields.io/badge/License-MIT-yellow.svg
[mit-url]: https://opensource.org/licenses/MIT
[Homebrew]: https://brew.sh/
[Chocolatey]: https://chocolatey.org/
[releases]: https://github.com/rewe-digital-incubator/Konversation/releases
[cli-ver-img]: https://img.shields.io/badge/cli-1.1.0-blue "Latest CLI version is 1.1.0"