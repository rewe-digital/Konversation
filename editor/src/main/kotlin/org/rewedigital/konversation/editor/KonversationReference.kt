package org.rewedigital.konversation.editor

import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.openapi.util.TextRange
import com.intellij.psi.*
import java.util.*

class KonversationReference(element: PsiElement, textRange: TextRange) : PsiReferenceBase<PsiElement>(element, textRange), PsiPolyVariantReference {
    private val intentName = element.text.substring(textRange.startOffset, textRange.endOffset)

    override fun multiResolve(incompleteCode: Boolean): Array<ResolveResult> {
        val project = myElement.project
        val intents = KonversationUtil.findIntents(project, intentName)
        //println("Searching for $intentName. Found: $intents")
        val results = ArrayList<ResolveResult>()
        for (intent in intents) {
            results.add(PsiElementResolveResult(intent))
        }
        return results.toTypedArray()
    }

    override fun resolve(): PsiElement? {
        val resolveResults = multiResolve(false)
        return if (resolveResults.size == 1) resolveResults[0].element else null
    }

    override fun getVariants(): Array<Any> =
        KonversationUtil.findIntents(myElement.project).mapNotNull { intent ->
            println("Debug: ${intent.text}")
            if (intent.text != null && !intent.text.isNullOrEmpty()) {
                LookupElementBuilder.create(intent).withIcon(KonversationLanguage.ICON).withTypeText(intent.containingFile.name)
            }
        }.toTypedArray()
}