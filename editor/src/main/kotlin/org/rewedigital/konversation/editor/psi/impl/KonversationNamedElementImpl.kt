package org.rewedigital.konversation.editor.psi.impl

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import com.intellij.util.IncorrectOperationException
import org.rewedigital.konversation.editor.psi.KonversationNamedElement

abstract class KonversationNamedElementImpl(node: ASTNode) : ASTWrapperPsiElement(node), KonversationNamedElement {

    override fun getNameIdentifier(): PsiElement? = null

    @Throws(IncorrectOperationException::class)
    override fun setName(name: String): PsiElement? = null
}