package org.rewedigital.konversation.editor

import com.intellij.lang.ASTNode
import com.intellij.lang.folding.FoldingBuilderEx
import com.intellij.lang.folding.FoldingDescriptor
import com.intellij.openapi.editor.Document
import com.intellij.psi.PsiElement
import java.util.*

class KonversationFoldingBuilder : FoldingBuilderEx() {
    override fun buildFoldRegions(root: PsiElement, document: Document, quick: Boolean): Array<FoldingDescriptor> {
        //val windowManager = WindowManager.getInstance()
        //ProjectManager.getInstance().openProjects.firstOrNull { project ->
        //    windowManager.suggestParentWindow(project).also { println(it); println(it?.isActive)}?.isActive == true
        //}?.let { project ->
        //    println(FileEditorManager.getInstance(project).selectedTextEditor?.inlayModel)
        //}

        val descriptors = ArrayList<FoldingDescriptor>()
        /*
        val group = FoldingGroup.newGroup("simple")
        val literalExpressions = PsiTreeUtil.findChildrenOfType(root, PsiLiteralExpression::class.java)
        for (literalExpression in literalExpressions) {
            val value = if (literalExpression.value is String) literalExpression.value as String? else null

            if (value != null && value.startsWith("simple:")) {
                val project = literalExpression.project
                val key = value.substring(7)
                val properties = SimpleUtil.findIntents(project, key)
                if (properties.size == 1) {
                    descriptors.add(object : FoldingDescriptor(literalExpression.node,
                        TextRange(literalExpression.textRange.startOffset + 1,
                            literalExpression.textRange.endOffset - 1),
                        group) {
                        override fun getPlaceholderText(): String? {
                            // IMPORTANT: keys can come with no values, so a test for null is needed
                            // IMPORTANT: Convert embedded \n to backslash n, so that the string will look like it has LF embedded
                            // in it and embedded " to escaped "
                            val valueOf = properties.get(0).getValue()
                            return if (valueOf == null) "" else valueOf!!.replace("\n".toRegex(), "\\n").replace("\"".toRegex(), "\\\\\"")
                        }
                    })
                }
            }
        }
         */
        return descriptors.toTypedArray()
    }

    override fun getPlaceholderText(node: ASTNode) = "..."

    override fun isCollapsedByDefault(node: ASTNode) = false
}

