[runtime-shared](../../index.md) / [org.rewedigital.konversation](../index.md) / [Konversation](./index.md)

# Konversation

`class Konversation`

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
| [createOutput](create-output.md) | `fun createOutput(data: `[`Map`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-map/index.html)`<`[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, `[`Any`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)`> = emptyMap()): `[`Output`](https://github.com/rewe-digital-incubator/konversation/blob/master/docs/shared/org.rewedigital.konversation/-output/index.md)<br>Creates a static randomized output for your voice application. The [data](https://github.com/rewe-digital-incubator/konversation/blob/master/docs/shared/org.rewedigital.konversation/-konversation/create-output/data.md) will be applied to the output so that you can customize the output with your values. |

### Companion Object Properties

| Name | Summary |
|---|---|
| [random](random.md) | `val random: `[`Random`](https://github.com/rewe-digital-incubator/konversation/blob/master/docs/shared/org.rewedigital.konversation/-random/index.md)<br>The randomness implementation which can be modified for testing. |
| [regex](regex.md) | `val regex: <ERROR CLASS>`<br>A regular expression to apply the actual values. |
