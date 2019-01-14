# Command line interface of Konversation [![Build Status][travis-image]][travis-url] [![Code Coverage][codecov-img]][codecov-url] [![License: MIT][mit-image]][mit-url]

Konversation has its own command line interface, which you can use for generating the kson files for the runtime. You can use it for
validate the syntax of the input files or to integrate it into your own build system. Keep in mind you can also use the
[gradle plugin](../gradle-plugin/readme.md) for integrating the conversation of your ksv files in your gradle build.

## Setup

At first you need to download the latest version of the konversation.jar. Then create in Windows a file called `konversation.cmd` e.g. in 
`c:\Windows\system32` directory (yep dirty hack sorry) and add the content:

    java -jar path/to/your/konveration.jar %1 %2 %3 %4 %5 %6 %7 %8 %9

If you use Linux or Mac create a file called `konversation.sh` in `/usr/local/bin` with this content:

    #!/bin/sh
    java -jar path/to/your/konveration.jar $1 $2 $3 $4 $5 $6 $7 $8 $9

Now in your shell/command line/terminal you can use the konversation command e.g.: `konversation -v`. This should output your installed
konversation cli version number and exit then.

Please note we know that this is not an ideal way for an installation, better ways are planed for future releases.

## Usage

You can process a single file or a directory to convert a batch of files. Currently are two formats supported: `.ksv` files and `.grammar`
files. Check also the specification of the [supported file formats](./file-formats.md).

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

[travis-image]: https://travis-ci.org/rewe-digital-incubator/konversation.svg?branch=master
[travis-url]: https://travis-ci.org/rewe-digital-incubator/konversation
[codecov-img]: http://codecov.io/github/rewe-digital-incubator/konversation/coverage.svg?branch=master
[codecov-url]: http://codecov.io/github/rewe-digital-incubator/konversation?branch=master
[mit-image]: https://img.shields.io/badge/License-MIT-yellow.svg
[mit-url]: https://opensource.org/licenses/MIT