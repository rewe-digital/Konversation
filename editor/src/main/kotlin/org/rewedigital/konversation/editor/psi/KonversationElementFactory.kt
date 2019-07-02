package org.rewedigital.konversation.editor.psi

import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFileFactory
import org.rewedigital.konversation.editor.KonversationFileType

object KonversationElementFactory {
    fun createProperty(project: Project, name: String): KonversationIntentName {
        val file = createFile(project, name)
        return file.firstChild as KonversationIntentName
    }

    private fun createFile(project: Project, text: String): KonversationFile {
        val name = "dummy.kvs"
        return PsiFileFactory.getInstance(project).createFileFromText(name, KonversationFileType.INSTANCE, text) as KonversationFile
    }
}