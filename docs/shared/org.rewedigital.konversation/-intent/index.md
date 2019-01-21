[shared](../../index.md) / [org.rewedigital.konversation](../index.md) / [Intent](./index.md)

# Intent

`data class Intent`

The intent of the user, holding the utterances, prompts and so on.

### Constructors

| Name | Summary |
|---|---|
| [&lt;init&gt;](-init-.md) | `Intent(name: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, utterances: `[`MutableList`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-mutable-list/index.html)`<`[`Utterance`](../-utterance/index.md)`> = mutableListOf(), prompt: `[`Prompt`](../-prompt/index.md)` = Prompt(), reprompt: `[`MutableMap`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-mutable-map/index.html)`<`[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)`, `[`Prompt`](../-prompt/index.md)`> = mutableMapOf(), inContext: `[`MutableList`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-mutable-list/index.html)`<`[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`> = mutableListOf(), outContext: `[`MutableList`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-mutable-list/index.html)`<`[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`> = mutableListOf(), followUp: `[`MutableList`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-mutable-list/index.html)`<`[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`> = mutableListOf(), suggestions: `[`MutableList`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-mutable-list/index.html)`<`[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`> = mutableListOf(), extras: `[`MutableMap`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-mutable-map/index.html)`<`[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, `[`Any`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)`> = mutableMapOf())`<br>The intent of the user, holding the utterances, prompts and so on. |

### Properties

| Name | Summary |
|---|---|
| [extras](extras.md) | `val extras: `[`MutableMap`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-mutable-map/index.html)`<`[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, `[`Any`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)`>`<br>The extras e.g. for UI Elements, for future usage. |
| [followUp](follow-up.md) | `val followUp: `[`MutableList`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-mutable-list/index.html)`<`[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`>`<br>The next logical intent, for future usage. |
| [inContext](in-context.md) | `var inContext: `[`MutableList`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-mutable-list/index.html)`<`[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`>`<br>The input context, for future usage. |
| [name](name.md) | `val name: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)<br>The name of this intent. Typically the generated kson file will have this file name. |
| [outContext](out-context.md) | `var outContext: `[`MutableList`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-mutable-list/index.html)`<`[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`>`<br>The output context, for future usage. |
| [prompt](prompt.md) | `val prompt: `[`Prompt`](../-prompt/index.md)<br>The prompt of this intent. That are the outputs what the voice application will say or show the user. |
| [reprompt](reprompt.md) | `val reprompt: `[`MutableMap`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-mutable-map/index.html)`<`[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)`, `[`Prompt`](../-prompt/index.md)`>`<br>The reprompt which is said when the user gives no input. |
| [suggestions](suggestions.md) | `val suggestions: `[`MutableList`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-mutable-list/index.html)`<`[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`>`<br>The suggestions what the user can say next when using this intent. |
| [utterances](utterances.md) | `val utterances: `[`MutableList`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-mutable-list/index.html)`<`[`Utterance`](../-utterance/index.md)`>`<br>The utterances of this intent. That are the sentences the user can say to invoke this intent. |
