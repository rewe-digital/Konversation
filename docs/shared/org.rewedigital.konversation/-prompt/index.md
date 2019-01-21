[shared](../../index.md) / [org.rewedigital.konversation](../index.md) / [Prompt](./index.md)

# Prompt

`open class Prompt`

Each prompt consists of multiple [Part](../-part/index.md)s which can be for the display or just for the audio output.

### Constructors

| Name | Summary |
|---|---|
| [&lt;init&gt;](-init-.md) | `Prompt(part: `[`Part`](../-part/index.md)`)`<br>This is only constructor of the Prompt, the other are for the non yet existing Reply.`Prompt(parts: `[`MutableList`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-mutable-list/index.html)`<`[`Part`](../-part/index.md)`> = mutableListOf(), suggestions: `[`MutableList`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-mutable-list/index.html)`<`[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`> = mutableListOf(), reprompts: `[`MutableMap`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-mutable-map/index.html)`<`[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, `[`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)`<`[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`>> = mutableMapOf())`<br>Each prompt consists of multiple [Part](../-part/index.md)s which can be for the display or just for the audio output. |

### Properties

| Name | Summary |
|---|---|
| [parts](parts.md) | `open val parts: `[`MutableList`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-mutable-list/index.html)`<`[`Part`](../-part/index.md)`>`<br>The parts of the response which should be build. |
| [reprompts](reprompts.md) | `open val reprompts: `[`MutableMap`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-mutable-map/index.html)`<`[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, `[`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)`<`[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`>>`<br>The reprompts which are used in context of the runtime. TODO this is for the Reply part only and should be just an Int instead of a String |
| [suggestions](suggestions.md) | `open val suggestions: `[`MutableList`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-mutable-list/index.html)`<`[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`>`<br>The suggestions which are used in context of the runtime. TODO this is for the Reply part only |
