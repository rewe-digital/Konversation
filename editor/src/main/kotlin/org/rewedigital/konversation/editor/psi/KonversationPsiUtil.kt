package org.rewedigital.konversation.editor.psi

import com.intellij.psi.PsiElement

object KonversationPsiImplUtil {

    // ...

    fun getName(element: KonversationUtterence) = element.text

    fun setName(element: KonversationUtterence, newName: String): PsiElement {
        val keyNode = element.node.findChildByType(KonversationTypes.INTENT_NAME)
        if (keyNode != null) {

            val property = KonversationElementFactory.createProperty(element.getProject(), newName)
            val newKeyNode = property.firstChild.node
            element.node.replaceChild(keyNode, newKeyNode)
        }
        return element
    }

    fun getNameIdentifier(element: KonversationUtterence) =
        element.node.findChildByType(KonversationTypes.INTENT_NAME)?.psi

    // ...
}