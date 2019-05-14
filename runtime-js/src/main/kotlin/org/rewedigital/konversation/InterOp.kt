package org.rewedigital.konversation

/**
 * This class is the interop layer to use just primitiv values. Lists will be converted to Arrays and Maps to simple objects.
 */
@JsName("Konversation")
class KonversationJs(name: String, environment: dynamic) : Konversation(name, convertToEnvironment(environment)) {
    /**
     * Creates a static randomized output for your voice application.
     * The [data] will be applied to the output so that you can customize the output with your values.
     */
    @JsName("createOutput")
    fun createJavaScriptOutput(data: dynamic = null): OutputJs {
        val map = js("Object").keys(js("data||{}")).map { key ->
            (key as String) to (data[key] as Any)
        } as Array<Pair<String, Any>>
        return OutputJs(createOutput(map.toMap()))
    }

    @JsName("Output")
    class OutputJs(
        /** The display test. */
        val displayText: String,
        /** The Speech Synthesis Markup Language, the spoken output. */
        val ssml: String,
        /** The reprompts which should the user hear when the user gives no input. */
        val reprompts: dynamic,
        /** The list of suggestions which should been displayed. */
        val suggestions: Array<String>,
        /** The strings for UI elements */
        val extras: dynamic) {
        constructor(output: Output) : this(
            output.displayText,
            output.ssml,
            output.reprompts.toDynamic(),
            output.suggestions.toTypedArray(),
            output.extras.toDynamic())
    }
}

@Suppress("UNUSED_PARAMETER") // env is used by the js() method
private fun convertToEnvironment(env: dynamic) : Environment {
    val nonNullEnv = js("env || {platform:'', locale:''}")
    return Environment(nonNullEnv["platform"] as String, nonNullEnv["locale"] as String)
}

private fun <K,V> Map<K,V>.toDynamic(): dynamic {
    val output = js("{}")
    entries.forEach { (k, v) ->
        output[k] = v
    }
    return output
}