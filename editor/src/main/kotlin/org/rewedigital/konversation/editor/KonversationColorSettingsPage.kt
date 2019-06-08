package org.rewedigital.konversation.editor

import com.intellij.openapi.options.colors.AttributesDescriptor
import com.intellij.openapi.options.colors.ColorDescriptor
import com.intellij.openapi.options.colors.ColorSettingsPage

class KonversationColorSettingsPage : ColorSettingsPage {

    override fun getIcon() = KonversationLanguage.ICON

    override fun getHighlighter() = KonversationSyntaxHighlighter()

    override fun getDemoText(): String {
        return "# This is a example Konversation File\n" +
                "// you can also use slashes as comment\n" +
                "ExampleIntent:\n" +
                "! Example Utterance\n" +
                "- Example output\n" +
                "+\n" +
                "~ and some only spoken text\n" +
                "# And some Suggestions:\n" +
                "[do this] [do that]"
    }

    override fun getAdditionalHighlightingTagToDescriptorMap(): Nothing? = null

    override fun getAttributeDescriptors() = DESCRIPTORS

    override fun getColorDescriptors(): Array<out ColorDescriptor> = ColorDescriptor.EMPTY_ARRAY

    override fun getDisplayName() = "Konversation"

    companion object {
        private val DESCRIPTORS = arrayOf(
            AttributesDescriptor("Intent name", KonversationSyntaxHighlighter.INTENT_NAME),
            AttributesDescriptor("Utterence", KonversationSyntaxHighlighter.UTTERANCE),
            AttributesDescriptor("Output", KonversationSyntaxHighlighter.ALTERNATIVE),
            AttributesDescriptor("Suggestion", KonversationSyntaxHighlighter.SUGGESTION))
    }
}