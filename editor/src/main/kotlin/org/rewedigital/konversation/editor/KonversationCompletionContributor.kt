package org.rewedigital.konversation.editor

import com.intellij.codeInsight.completion.*
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.patterns.PlatformPatterns
import com.intellij.psi.tree.IElementType
import com.intellij.util.ProcessingContext
import org.rewedigital.konversation.editor.psi.KonversationTypes

class KonversationCompletionContributor : CompletionContributor() {
    init {
        println("Adding suggestions...")
        extend(CompletionType.BASIC, PlatformPatterns.psiElement(KonversationTypes.INTENT_NAME)
            .withLanguage(KonversationLanguage.INSTANCE), object : CompletionProvider<CompletionParameters>() {
            public override fun addCompletions(parameters: CompletionParameters, context: ProcessingContext, resultSet: CompletionResultSet) {
                println("Konversation file: intent name suggestion added")
                resultSet.addElement(LookupElementBuilder.create("Hello"))
            }
        })
        //PsiJavaPatterns.psiMethod().condition.conditions.add()
        IElementType.enumerate { it.language.id == "kotlin" && it.toString() == "REGULAR_STRING_PART" }.firstOrNull()?.let { kotlinString ->
            //println("adding kotlin suggestion on $kotlinString")
            extend(CompletionType.BASIC, PlatformPatterns.psiElement(kotlinString)
                .withLanguage(kotlinString.language), object : CompletionProvider<CompletionParameters>() {
                public override fun addCompletions(parameters: CompletionParameters, context: ProcessingContext, resultSet: CompletionResultSet) {
                    //println("Generating suggestion $parameters; $context")
                    if (parameters.position.parent?.parent?.parent?.prevSibling?.parent?.prevSibling?.text == "loadKonversation") {
                        KonversationUtil.findIntents(parameters.editor.project!!).forEach {
                            resultSet.addElement(LookupElementBuilder.create(it.text))
                        }
                        //resultSet.addElement(LookupElementBuilder.create("Test123"))
                    }
                }
            })
        }
    }
}