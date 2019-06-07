package org.rewedigital.konversation.editor.psi

import com.intellij.extapi.psi.PsiFileBase
import com.intellij.psi.FileViewProvider
import org.rewedigital.konversation.editor.KonversationFileType
import org.rewedigital.konversation.editor.KonversationLanguage

class KonversationFile(viewProvider: FileViewProvider) : PsiFileBase(viewProvider, KonversationLanguage.INSTANCE) {
    override fun getFileType() = KonversationFileType.INSTANCE

    override fun toString() = "Konversation File"

    override fun getIcon(flags: Int) = KonversationLanguage.ICON
}