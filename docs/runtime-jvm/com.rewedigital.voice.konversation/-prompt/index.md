[runtime-jvm](../../index.md) / [org.rewedigital.konversation](../index.md) / [Prompt](./index.md)

# Prompt

`open class Prompt`

### Constructors

| Name | Summary |
|---|---|
| [&lt;init&gt;](-init-.md) | `Prompt(part: `[`Part`](../-part/index.md)`)`<br>`Prompt(parts: `[`MutableList`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-mutable-list/index.html)`<`[`Part`](../-part/index.md)`> = mutableListOf(), suggestions: `[`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)`<`[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`> = emptyList(), reprompts: `[`Map`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-map/index.html)`<`[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, `[`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)`<`[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`>> = mapOf())` |

### Properties

| Name | Summary |
|---|---|
| [parts](parts.md) | `val parts: `[`MutableList`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-mutable-list/index.html)`<`[`Part`](../-part/index.md)`>` |
| [reprompts](reprompts.md) | `val reprompts: `[`Map`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-map/index.html)`<`[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, `[`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)`<`[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`>>` |
| [suggestions](suggestions.md) | `val suggestions: `[`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)`<`[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`>` |

### Inheritors

| Name | Summary |
|---|---|
| [AnswerImpl](../-answer-impl/index.md) | `class AnswerImpl : `[`Prompt`](./index.md) |
