package org.rewedigital.konversation.editor

import com.intellij.openapi.project.Project
import com.intellij.psi.PsiManager
import com.intellij.psi.search.FileTypeIndex
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.util.PsiTreeUtil
import org.rewedigital.konversation.editor.psi.KonversationIntentBlock

object KonversationUtil {
    /*
    fun findIntents(project: Project, key: String): List<KonversationIntentName> {
        var result: MutableList<KonversationIntentName>? = null
        val virtualFiles = FileTypeIndex.getFiles(KonversationFileType.INSTANCE, GlobalSearchScope.allScope(project))
        for (virtualFile in virtualFiles) {
            val konversationFile = PsiManager.getInstance(project).findFile(virtualFile) as KonversationFile?
            if (konversationFile != null) {
                val properties = PsiTreeUtil.getChildrenOfType(konversationFile, KonversationIntentName::class.java)
                if (properties != null) {
                    for (property in properties) {
                        if (key == property.getKey()) {
                            if (result == null) {
                                result = ArrayList<KonversationIntentName>()
                            }
                            result.add(property)
                        }
                    }
                }
            }
        }
        return result ?: emptyList<KonversationIntentName>()
    }
     */

    fun findIntents(project: Project, intentName: String? = null) =
        FileTypeIndex.getFiles(KonversationFileType.INSTANCE, GlobalSearchScope.allScope(project)).flatMap { virtualFile ->
            PsiManager.getInstance(project).findFile(virtualFile)?.let { file ->
                PsiTreeUtil.getChildrenOfType(file, KonversationIntentBlock::class.java)?.map {
                    it.firstChild.firstChild
                }
            }?.toList() ?: emptyList()
        }.filter { intentName == null || it.text == intentName }
}