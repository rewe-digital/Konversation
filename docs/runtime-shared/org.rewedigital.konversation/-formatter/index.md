[runtime-shared](../../index.md) / [org.rewedigital.konversation](../index.md) / [Formatter](./index.md)

# Formatter

`expect class Formatter`

Abstraction layer to format a string. This function just provides a platform specific `sprintf()` implementation.

### Constructors

| Name | Summary |
|---|---|
| [&lt;init&gt;](-init-.md) | `Formatter()`<br>Abstraction layer to format a string. This function just provides a platform specific `sprintf()` implementation. |

### Functions

| Name | Summary |
|---|---|
| [format](format.md) | `fun format(locale: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, format: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, vararg args: `[`Any`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)`?): `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)<br>Uses the provided [format](https://github.com/rewe-digital/konversation/blob/master/docs/shared/org.rewedigital.konversation/-formatter/format/format.md) as a format string and returns a string obtained by substituting the specified arguments, using the locale of the environment. |
