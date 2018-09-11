var hallo = require("./js/hallo");
var test = new hallo.app.rks.test.Konversation("test");
for (var i = 0; i < 10; i++) {
    console.log(test.create());
}
