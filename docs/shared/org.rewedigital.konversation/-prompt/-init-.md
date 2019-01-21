[shared](../../index.md) / [org.rewedigital.konversation](../index.md) / [Prompt](index.md) / [&lt;init&gt;](./-init-.md)

# &lt;init&gt;

`Prompt(part: `[`Part`](../-part/index.md)`)`

This is only constructor of the Prompt, the other are for the non yet existing Reply.

`Prompt(parts: `[`MutableList`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-mutable-list/index.html)`<`[`Part`](../-part/index.md)`> = mutableListOf(), suggestions: `[`MutableList`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-mutable-list/index.html)`<`[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`> = mutableListOf(), reprompts: `[`MutableMap`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-mutable-map/index.html)`<`[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, `[`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)`<`[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`>> = mutableMapOf())`

Each prompt consists of multiple [Part](../-part/index.md)s which can be for the display or just for the audio output.

