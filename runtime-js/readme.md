# Koversation (JS-Runtime) [![NPM Version][npm-image]][npm-url] [![NPM Downloads][downloads-image]][downloads-url] [![License: MIT][mit-image]][mit-url]

This is the runtime library of [Konversation][github-url]. You can create with this library the output
for your voice application.

## Usage

At first install the dependency "konversation" with the command:

    npm install konversation

Then you can use this example code:

    const Konversation = require("konversation").Konversation;
    const test = Konversation("test");
    const output = test.createOutput(); // created one randomized response
    const ssml = output.ssml;
    const displayText = output.displayText;
    
The example above requires that you have a `test.kson` file in the same directory as the script itself.
If you want to know check the documentation of the [konversation cli][cli-readme-url]. 
    
    
[npm-image]: https://img.shields.io/npm/v/konversation.svg
[npm-url]: https://npmjs.org/package/konversation
[downloads-image]: https://img.shields.io/npm/dm/konversation.svg
[downloads-url]: https://npmjs.org/package/konversation
[mit-image]: https://img.shields.io/badge/License-MIT-yellow.svg
[mit-url]: https://opensource.org/licenses/MIT
[github-url]: https://github.com/rewe-digital-incubator/konversation
[cli-readme-url]: https://github.com/rewe-digital-incubator/konversation/cli/readme.md
