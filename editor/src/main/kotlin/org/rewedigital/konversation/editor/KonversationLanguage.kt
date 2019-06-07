package org.rewedigital.konversation.editor

import com.intellij.lang.Language
import com.intellij.openapi.fileTypes.SingleLazyInstanceSyntaxHighlighterFactory
import com.intellij.openapi.fileTypes.SyntaxHighlighter
import com.intellij.openapi.fileTypes.SyntaxHighlighterFactory
import com.intellij.openapi.util.IconLoader

class KonversationLanguage : Language("Konversation", "text/konversation") {
    init {
        SyntaxHighlighterFactory.LANGUAGE_FACTORY.addExplicitExtension(this, object : SingleLazyInstanceSyntaxHighlighterFactory() {
            override fun createHighlighter(): SyntaxHighlighter {
                return KonversationSyntaxHighlighter()
            }
        })
    }

    companion object {
        val INSTANCE = KonversationLanguage()
        val ICON = IconLoader.getIcon("/kvs.png")
    }
}