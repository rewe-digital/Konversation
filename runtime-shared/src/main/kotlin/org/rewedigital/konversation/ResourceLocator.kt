package org.rewedigital.konversation

open class ResourceLocator {
    fun locate(konversation: String, environment: Environment): String {
        val prefix = "konversation"
        val locale = environment.locale.toLowerCase().replace('-', '_')
        val lang = locale.split('_').first()
        return returnIfExists("$prefix-${environment.platform}-$locale/$konversation.kson")
            .orIfExists("$prefix-${environment.platform}-$lang/$konversation.kson")
            .orIfExists("$prefix-$locale/$konversation.kson")
            .orIfExists("$prefix-$lang/$konversation.kson")
            .orIfExists("$prefix-${environment.platform}/$konversation.kson")
            .orIfExists("$prefix/$konversation.kson")
            .orThrow("Konversation with the name \"$konversation\" not found")
    }

    // open for testing
    open fun checkPath(path: String): Boolean = FileChecker().exists(path)

    private fun returnIfExists(path: String): String? =
        if (checkPath(path)) {
            path
        } else {
            null
        }

    private fun String?.orIfExists(path: String): String? =
        this ?: if (checkPath(path)) {
            path
        } else {
            null
        }

    private fun String?.orThrow(error: String): String =
        this ?: throw RuntimeException(error)
}