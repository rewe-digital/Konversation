

### All Types

| Name | Summary |
|---|---|
| [org.rewedigital.konversation.Part](../org.rewedigital.konversation/-part/index.md) | One part of the [Reply](../org.rewedigital.konversation/-reply/index.md) for the user. There can be two types: Text and VoiceOnly. The runtime will contact the parts and add separators (spaces). |
| [org.rewedigital.konversation.PartType](../org.rewedigital.konversation/-part-type/index.md) | The type of a part of the prompt. All parts will be concatenated by the runtime. For the display test the voice only parts will be stripped out. This is very helpful if you need to describe some visual parts as lists which would be redundant on display devices. Please note that the Text parts will be also added to the voice output. If you really need TextOnly please add a bug report. With an example why you need that. |
| [org.rewedigital.konversation.Reply](../org.rewedigital.konversation/-reply/index.md) | Each prompt consists of multiple [Part](../org.rewedigital.konversation/-part/index.md)s which can be for the display or just for the audio output. |
