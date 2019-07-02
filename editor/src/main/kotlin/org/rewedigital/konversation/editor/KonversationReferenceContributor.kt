package org.rewedigital.konversation.editor

import com.intellij.openapi.util.TextRange
import com.intellij.patterns.PlatformPatterns
import com.intellij.psi.*
import com.intellij.psi.tree.IElementType
import com.intellij.util.ProcessingContext

class KonversationReferenceContributor : PsiReferenceContributor() {
    override fun registerReferenceProviders(registrar: PsiReferenceRegistrar) {
        registrar.registerReferenceProvider(PlatformPatterns.psiElement(PsiLiteralExpression::class.java),
            object : PsiReferenceProvider() {
                override fun getReferencesByElement(element: PsiElement, context: ProcessingContext): Array<PsiReference> {
                    val literalExpression = element as PsiLiteralExpression
                    return (literalExpression.value as? String)?.let { value ->
                        if (element.parent?.prevSibling?.text == "loadKonversation") {
                            arrayOf<PsiReference>(KonversationReference(element, TextRange(1, value.length + 1)))
                        } else null
                    } ?: PsiReference.EMPTY_ARRAY
                }
            })
        IElementType.enumerate { it.language.id == "kotlin" && it.toString() == "LITERAL_STRING_TEMPLATE_ENTRY" }.firstOrNull()?.let { kotlinString ->
            registrar.registerReferenceProvider(PlatformPatterns.psiElement(kotlinString),
                object : PsiReferenceProvider() {
                    override fun getReferencesByElement(element: PsiElement, context: ProcessingContext) =
                        element.text?.let { value ->
                            println("Processing kotlin reference: $value")
                            if (element.parent?.parent?.parent?.prevSibling?.text == "loadKonversation") {
                                arrayOf<PsiReference>(KonversationReference(element, TextRange(0, value.length)))
                            } else null
                        } ?: PsiReference.EMPTY_ARRAY
                })
        }
    }
}