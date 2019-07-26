package org.rewedigital.konversation.editor

import com.intellij.openapi.editor.DefaultLanguageHighlighterColors
import com.intellij.openapi.editor.HighlighterColors
import com.intellij.openapi.editor.colors.TextAttributesKey.createTextAttributesKey
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase
import com.intellij.psi.TokenType
import com.intellij.psi.tree.IElementType
import org.rewedigital.konversation.editor.psi.KonversationTypes

class KonversationSyntaxHighlighter : SyntaxHighlighterBase() {

    override fun getHighlightingLexer() = KonversationLexerAdapter()

    override fun getTokenHighlights(tokenType: IElementType) = listOfNotNull(when (tokenType) {
        KonversationTypes.INTENT_NAME -> INTENT_NAME
        KonversationTypes.UTTERENCE -> UTTERANCE
        KonversationTypes.BLOCK -> ALTERNATIVE
        KonversationTypes.COMMENT -> COMMENT
        //KonversationTypes.CHARSEQUENCE -> VAR_TEST
        TokenType.BAD_CHARACTER -> BAD_CHARACTER
        KonversationTypes.COLON,
        KonversationTypes.LEFT_BRACE,
        KonversationTypes.RIGHT_BRACE,
        KonversationTypes.BLOCK_CONCAT,
        KonversationTypes.SUGGESTION_END,
        KonversationTypes.SUGGESTION_START,
        KonversationTypes.LINE,
        KonversationTypes.UTTERANCE,
        KonversationTypes.VARIABLE_DOLLAR,
        KonversationTypes.VARIABLE_PERCENT,
        KonversationTypes.VOICE_ONLY_BLOCK -> OP_TEST
        KonversationTypes.SUGGESTION -> SUGGESTION
        else -> null
    }).also { println("Highlight: $tokenType -> $it") }.toTypedArray()

    companion object {
        val INTENT_NAME = createTextAttributesKey("KONVERSATION_INTENT_NAME", DefaultLanguageHighlighterColors.CLASS_NAME)
        val UTTERANCE = createTextAttributesKey("KONVERSATION_UTTERANCE", DefaultLanguageHighlighterColors.STRING)
        val ALTERNATIVE = createTextAttributesKey("KONVERSATION_UTTERANCE", DefaultLanguageHighlighterColors.STRING)
        val COMMENT = createTextAttributesKey("KONVERSATION_COMMENT", DefaultLanguageHighlighterColors.LINE_COMMENT)
        val BAD_CHARACTER = createTextAttributesKey("KONVERSATION_BAD_CHARACTER", HighlighterColors.BAD_CHARACTER)
        val VAR_TEST = createTextAttributesKey("KONVERSATION_VARIABLE", DefaultLanguageHighlighterColors.CLASS_NAME)
        val OP_TEST = createTextAttributesKey("KONVERSATION_OPERATOR", DefaultLanguageHighlighterColors.OPERATION_SIGN)
        val SUGGESTION = createTextAttributesKey("KONVERSATION_SUGGESTION", DefaultLanguageHighlighterColors.CLASS_REFERENCE)
    }
}