const konversation = require("./runtime.js");
const test = new konversation.org.rewedigital.konversation.Konversation("test");
for (let i = 0; i < 10; i++) {
    console.log(test.createOutput());
}
