[shared](../../index.md) / [org.rewedigital.konversation](../index.md) / [Part](./index.md)

# Part

`interface Part`

One part of the [Prompt](../-prompt/index.md) for the user. There can be two types: Text and VoiceOnly. The runtime will contact the parts and add separators (spaces).

### Properties

| Name | Summary |
|---|---|
| [type](type.md) | `abstract val type: `[`PartType`](../-part-type/index.md)<br>The type of this part can be Text and VoiceOnly. |
| [variants](variants.md) | `abstract val variants: `[`MutableList`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-mutable-list/index.html)`<`[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`>`<br>The variants for this part. Please make sure that there are no grammatical issues when you use another alternative. |
