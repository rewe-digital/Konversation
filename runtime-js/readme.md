# Koversation (JS-Runtime) [![NPM Version][npm-image]][npm-url] [![NPM Downloads][downloads-image]][npm-url] [![License: MIT][mit-image]][mit-url] [![Stars][star-img]][star-url]

This is the runtime library of [Konversation][github-url]. Konversation is a tool to generate rich and
diversified responses to the user of a voice application.

## Usage

At first install the dependency "@rewe-digital/konversation" with the command:

    npm install @rewe-digital/konversation

Then you can use this example code:

    const Konversation = require("@rewe-digital/konversation").Konversation;
    const test = Konversation("test");
    const output = test.createOutput(); // created one randomized response
    const ssml = output.ssml;
    const displayText = output.displayText;
    
The example above requires that you have a `test.kson` file in the same directory as the script itself.
If you want to know how to create the kson files, please check the documentation of the
[konversation cli][cli-readme-url].

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
[npm-image]: https://img.shields.io/npm/v/@rewe-digital/konversation.svg
[npm-url]: https://npmjs.org/package/@rewe-digital/konversation
[downloads-image]: https://img.shields.io/npm/dm/@rewe-digital/konversation.svg
[mit-image]: https://img.shields.io/badge/License-MIT-yellow.svg
[mit-url]: https://opensource.org/licenses/MIT
[github-url]: https://github.com/rewe-digital-incubator/Konversation
[cli-readme-url]: https://github.com/rewe-digital-incubator/Konversation/blob/master/cli/readme.md
[star-img]: https://img.shields.io/github/stars/rewe-digital-incubator/Konversation.svg?style=social&label=Star&maxAge=3600
[star-url]: https://github.com/rewe-digital-incubator/Konversation/stargazers