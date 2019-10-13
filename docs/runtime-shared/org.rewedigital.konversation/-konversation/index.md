[runtime-shared](../../index.md) / [org.rewedigital.konversation](../index.md) / [Konversation](./index.md)

# Konversation

`open class Konversation`

The interface for accessing Konversation.

### Constructors

| Name | Summary |
|---|---|
| [&lt;init&gt;](-init-.md) | `Konversation(name: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, environment: `[`Environment`](https://github.com/rewe-digital-incubator/konversation/blob/master/docs/shared/org.rewedigital.konversation/-environment/index.md)`)`<br>Creates a new Konversation object with the given options. |

### Properties

| Name | Summary |
|---|---|
| [name](name.md) | `val name: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)<br>The conversation name you want to load. |

### Functions

| Name | Summary |
|---|---|
| [createOutput](create-output.md) | `fun createOutput(data: `[`Map`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-map/index.html)`<`[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, `[`Any`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)`> = emptyMap()): <ERROR CLASS>`<br>Creates a static randomized output for your voice application. The [data](https://github.com/rewe-digital-incubator/konversation/blob/master/docs/shared/org.rewedigital.konversation/-konversation/create-output/data.md) will be applied to the output so that you can customize the output with your values. |
