[shared](../../index.md) / [org.rewedigital.konversation](../index.md) / [Prompt](./index.md)

# Prompt

`open class Prompt`

Each prompt consists of multiple [Part](../-part/index.md)s which can be for the display or just for the audio output.

### Constructors

| Name | Summary |
|---|---|
| [&lt;init&gt;](-init-.md) | `Prompt(part: `[`Part`](../-part/index.md)`)``Prompt(parts: `[`MutableList`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-mutable-list/index.html)`<`[`Part`](../-part/index.md)`> = mutableListOf(), suggestions: `[`MutableList`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-mutable-list/index.html)`<`[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`> = mutableListOf(), reprompts: `[`MutableMap`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-mutable-map/index.html)`<`[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, `[`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)`<`[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`>> = mutableMapOf())`<br>Each prompt consists of multiple [Part](../-part/index.md)s which can be for the display or just for the audio output. |

### Properties

| Name | Summary |
|---|---|
| [parts](parts.md) | `open val parts: `[`MutableList`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-mutable-list/index.html)`<`[`Part`](../-part/index.md)`>` |
| [reprompts](reprompts.md) | `open val reprompts: `[`MutableMap`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-mutable-map/index.html)`<`[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, `[`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)`<`[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`>>` |
| [suggestions](suggestions.md) | `open val suggestions: `[`MutableList`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-mutable-list/index.html)`<`[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`>` |
