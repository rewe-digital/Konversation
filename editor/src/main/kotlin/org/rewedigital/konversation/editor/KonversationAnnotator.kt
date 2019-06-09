package org.rewedigital.konversation.editor

import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.impl.source.tree.LeafPsiElement
import org.rewedigital.konversation.editor.psi.KonversationUtterence

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
        (element as? KonversationUtterence)?.let { utterance ->
            println(utterance.text)
            val slots = validate(utterance.text, element.textRange, holder)
            var count = 1
            slots.forEach { slot ->
                count *= slot.count { it == '|' } + 1
            }
            if (count > 1000) {
                val range = TextRange(element.textRange.startOffset, element.textRange.endOffset)
                holder.createWarningAnnotation(range, "This utterance creates $count permutations.")
            }
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

    private fun validate(line: String, textRange: TextRange, holder: AnnotationHolder): MutableList<String> {
        // Parse the line to make sure that there is no syntax error. Regex would not work for cases like {{Foo}|{Bar}}
        var start = 0
        var counter = 0
        val slots = mutableListOf<String>()
        var lastWasMasked = false
        var dontProcess = false
        line.forEachIndexed { i, c ->
            when (c) {
                '\\' -> lastWasMasked = true
                '{' -> {
                    if (!lastWasMasked) {
                        when (counter) {
                            0 -> start = i + 1
                            1 -> {
                                // we found a slot type, that is fine
                            }
                            else -> {
                                val range = TextRange(textRange.startOffset + start, textRange.startOffset + i)
                                holder.createErrorAnnotation(range, "Too many opening brackets!")
                            }
                        }
                        counter++
                    }
                    lastWasMasked = false
                }
                '}' -> {
                    if (!lastWasMasked) {
                        when (counter) {
                            1 -> {
                                // we found the end of the slot
                                if (!dontProcess) {
                                    slots.add(line.substring(start, i))
                                }
                            }
                            else -> {
                                val range = TextRange(textRange.startOffset + i, textRange.startOffset + i + 1)
                                holder.createErrorAnnotation(range, "Unexpected closing bracket!")
                            }
                        }
                        counter--
                    }
                    lastWasMasked = false
                    dontProcess = false
                }
                '$' -> dontProcess = true
                //'%'-> {

                //}
                else -> lastWasMasked = false
            }
        }
        if (counter > 0) {
            val range = TextRange(textRange.startOffset + start - 1, textRange.endOffset)
            holder.createErrorAnnotation(range, "Closing bracket is missing!")
        }

        return slots
    }
}