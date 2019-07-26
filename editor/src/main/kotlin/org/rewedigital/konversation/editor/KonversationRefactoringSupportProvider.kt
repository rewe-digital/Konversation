package org.rewedigital.konversation.editor

import com.intellij.lang.refactoring.RefactoringSupportProvider
import com.intellij.psi.PsiElement
import org.rewedigital.konversation.editor.psi.KonversationIntentName

class KonversationRefactoringSupportProvider : RefactoringSupportProvider() {
    override fun isMemberInplaceRenameAvailable(element: PsiElement, context: PsiElement?) = element is KonversationIntentName
}