const Konversation = require("konversation").Konversation;
const test = new Konversation("test");
for (let i = 0; i < 10; i++) {
    console.log(test.createOutput());
}
