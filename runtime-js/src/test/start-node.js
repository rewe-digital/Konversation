const konversation = require("./runtime.js");
const test = new konversation.com.rewedigital.voice.konversation.Konversation("test");
for (var i = 0; i < 10; i++) {
    console.log(test.create());
}
