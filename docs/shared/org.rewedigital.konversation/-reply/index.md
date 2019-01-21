[shared](../../index.md) / [org.rewedigital.konversation](../index.md) / [Reply](./index.md)

# Reply

`open class Reply`

Each Reply contains multiple [Part](../-part/index.md)s, suggestions and reprompts. The parts can be for the display or just for the audio output.

### Constructors

| Name | Summary |
|---|---|
| [&lt;init&gt;](-init-.md) | `Reply(parts: `[`MutableList`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-mutable-list/index.html)`<`[`Part`](../-part/index.md)`> = mutableListOf(), suggestions: `[`MutableList`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-mutable-list/index.html)`<`[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`> = mutableListOf(), reprompts: `[`MutableMap`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-mutable-map/index.html)`<`[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, `[`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)`<`[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`>> = mutableMapOf())`<br>Each Reply contains multiple [Part](../-part/index.md)s, suggestions and reprompts. The parts can be for the display or just for the audio output. |

### Properties

| Name | Summary |
|---|---|
| [parts](parts.md) | `open val parts: `[`MutableList`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-mutable-list/index.html)`<`[`Part`](../-part/index.md)`>`<br>The parts of the response which should be build. |
| [reprompts](reprompts.md) | `open val reprompts: `[`MutableMap`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-mutable-map/index.html)`<`[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, `[`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)`<`[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`>>`<br>The reprompts which are used in context of the runtime. |
| [suggestions](suggestions.md) | `open val suggestions: `[`MutableList`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-mutable-list/index.html)`<`[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`>`<br>The suggestions which are used in context of the runtime. |
