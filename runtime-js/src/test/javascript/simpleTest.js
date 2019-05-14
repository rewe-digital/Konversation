const runtime = require("../../../build/out/index");
const Konversation = runtime.Konversation;
const Environment = runtime.Environment;

runtime.Random.Companion.forcedValue = 0;

const k1 = new Konversation("test", {platform:"Foo", locale:"de"});
const k2 = new Konversation("test", new Environment("Foo", ""));
const o1 = k1.createOutput();
const o2 = k1.createOutput({a:"a", b:"2"});
const o3 = k2.createOutput();
const o4 = k2.createOutput({a:"a", b:"2"});
if(!o1 || !o2 || !o3 || !o4) throw Error("Output was empty!");
if(JSON.stringify(o2) !== '{"displayText":"Deine Liste ist leer.","ssml":"<speak>Deine Liste ist leer. Du kannst aber Zutaten aus den Rezepten oder Angebote zur Einkaufsliste hinzufügen. Versuch\'s doch mal damit.</speak>","reprompts":{},"suggestions":[],"extras":{}}') throw Error("Unexpected output");
console.log(JSON.stringify(o2, null, 2));
