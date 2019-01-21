[shared](../index.md) / [org.rewedigital.konversation](./index.md)

## Package org.rewedigital.konversation

### Types

| Name | Summary |
|---|---|
| [Intent](-intent/index.md) | `data class Intent`<br>The intent of the user, holding the utterances, prompts and so on. |
| [Part](-part/index.md) | `interface Part`<br>One part of the prompt for the user. There can be two types: Text and VoiceOnly. The runtime will contact the parts and add separators (spaces). |
| [PartType](-part-type/index.md) | `enum class PartType`<br>The type of a part of the prompt. All parts will be concatenated by the runtime. For the display test the voice only parts will be stripped out. This is very helpful if you need to describe some visual parts as lists which would be redundant on display devices. Please note that the Text parts will be also added to the voice output. If you really need TextOnly please add a bug report. With an example why you need that. |
| [Prompt](-prompt/index.md) | `open class Prompt`<br>Each prompt consists of multiple [Part](-part/index.md)s which can be for the display or just for the audio output. |
| [Suggestion](-suggestion/index.md) | `interface Suggestion` |
| [Utterance](-utterance/index.md) | `interface Utterance` |
