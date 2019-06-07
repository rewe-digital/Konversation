package org.rewedigital.konversation.editor

import com.intellij.psi.tree.IElementType
import org.jetbrains.annotations.NonNls

class KonversationElementType(@NonNls debugName: String) : IElementType(debugName, KonversationLanguage.INSTANCE)