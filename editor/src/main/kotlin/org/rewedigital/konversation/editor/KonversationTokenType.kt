package org.rewedigital.konversation.editor

import com.intellij.psi.tree.IElementType
import org.jetbrains.annotations.NonNls

class KonversationTokenType(@NonNls debugName: String) : IElementType(debugName, KonversationLanguage.INSTANCE) {
    override fun toString() = "KonversationTokenType." + super.toString()
}