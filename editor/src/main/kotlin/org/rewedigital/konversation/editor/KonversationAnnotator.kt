package org.rewedigital.konversation.editor

import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.impl.source.tree.LeafPsiElement

class KonversationAnnotator : Annotator {
    override fun annotate(element: PsiElement, holder: AnnotationHolder) {
        (element as? com.intellij.psi.impl.source.tree.java.PsiMethodCallExpressionImpl)?.let { method ->
            val methodName = method.firstChild.text
            if (method.argumentList.children.size > 2) {
                method.argumentList.children[1]?.let { arg ->
                    validateArgument(arg, holder)
                }
                println("java code: $methodName")
            }
        }
        if (element.javaClass.name == "org.jetbrains.kotlin.psi.KtCallExpression") {
            val methodName = (element.firstChild.firstChild as LeafPsiElement).text
            element.lastChild.firstChild.nextSibling.firstChild?.let { arg ->
                validateArgument(arg, holder)
            }
            println("KtCallExpression. Found: $methodName")
        }
    }

    private fun validateArgument(argument: PsiElement, holder: AnnotationHolder) {
        if (argument.text?.startsWith('"') == true && argument.text?.endsWith('"') == true) {
            val value = argument.text.trim('"')
            if (value == "bad") {
                val range = TextRange(argument.textRange.startOffset + 1,
                    argument.textRange.endOffset - 1)
                holder.createErrorAnnotation(range, "Bad is bad!")
            } else if (value == "good") {
                val range = TextRange(argument.textRange.startOffset + 1,
                    argument.textRange.endOffset - 1)
                holder.createInfoAnnotation(range, "Hallo!")
            }
        }
    }
}