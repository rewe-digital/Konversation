package org.rewedigital.konversation.editor.psi

import com.intellij.psi.tree.IElementType
import org.jetbrains.annotations.NonNls
import org.rewedigital.konversation.editor.KonversationLanguage

class KonversationElementType(@NonNls debugName: String) : IElementType(debugName, KonversationLanguage.INSTANCE) {
    override fun toString() = "Konversation:" + super.toString()
}