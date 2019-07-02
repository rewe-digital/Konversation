// This is a generated file. Not intended for manual editing.
package org.rewedigital.konversation.editor.psi;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.IElementType;

import org.rewedigital.konversation.editor.impl.KonversationBlockDelimitterImpl;
import org.rewedigital.konversation.editor.impl.KonversationBlockPartImpl;
import org.rewedigital.konversation.editor.impl.KonversationCommandDelimitterImpl;
import org.rewedigital.konversation.editor.impl.KonversationCommentImpl;
import org.rewedigital.konversation.editor.impl.KonversationIntentBlockImpl;
import org.rewedigital.konversation.editor.impl.KonversationIntentImpl;
import org.rewedigital.konversation.editor.impl.KonversationIntentNameImpl;
import org.rewedigital.konversation.editor.impl.KonversationOutputImpl;
import org.rewedigital.konversation.editor.impl.KonversationPromptBlockImpl;
import org.rewedigital.konversation.editor.impl.KonversationSuggestionBlockImpl;
import org.rewedigital.konversation.editor.impl.KonversationSuggestionImpl;
import org.rewedigital.konversation.editor.impl.KonversationSuggestionLineImpl;
import org.rewedigital.konversation.editor.impl.KonversationTextBlockImpl;
import org.rewedigital.konversation.editor.impl.KonversationUtteranceLineImpl;
import org.rewedigital.konversation.editor.impl.KonversationUtterancesBlockImpl;
import org.rewedigital.konversation.editor.impl.KonversationUtterenceImpl;
import org.rewedigital.konversation.editor.impl.KonversationVoiceBlockImpl;

public interface KonversationTypes {

    IElementType BLOCK_DELIMITTER = new KonversationElementType("BLOCK_DELIMITTER");
    IElementType BLOCK_PART = new KonversationElementType("BLOCK_PART");
    IElementType COMMAND_DELIMITTER = new KonversationElementType("COMMAND_DELIMITTER");
    IElementType COMMENT = new KonversationElementType("COMMENT");
    IElementType INTENT = new KonversationElementType("INTENT");
    IElementType INTENT_BLOCK = new KonversationElementType("INTENT_BLOCK");
    IElementType INTENT_NAME = new KonversationElementType("INTENT_NAME");
    IElementType OUTPUT = new KonversationElementType("OUTPUT");
    IElementType PROMPT_BLOCK = new KonversationElementType("PROMPT_BLOCK");
    IElementType SUGGESTION = new KonversationElementType("SUGGESTION");
    IElementType SUGGESTION_BLOCK = new KonversationElementType("SUGGESTION_BLOCK");
    IElementType SUGGESTION_LINE = new KonversationElementType("SUGGESTION_LINE");
    IElementType TEXT_BLOCK = new KonversationElementType("TEXT_BLOCK");
    IElementType UTTERANCES_BLOCK = new KonversationElementType("UTTERANCES_BLOCK");
    IElementType UTTERANCE_LINE = new KonversationElementType("UTTERANCE_LINE");
    IElementType UTTERENCE = new KonversationElementType("UTTERENCE");
    IElementType VOICE_BLOCK = new KonversationElementType("VOICE_BLOCK");

    IElementType BLOCK = new KonversationElementType("-");
    IElementType BLOCK_CONCAT = new KonversationElementType("+");
    IElementType CHARSEQUENCE = new KonversationElementType("charSequence");
    IElementType COLON = new KonversationElementType(":");
    IElementType LEFT_BRACE = new KonversationElementType("{");
    IElementType RIGHT_BRACE = new KonversationElementType("}");
    IElementType SUGGESTION_END = new KonversationElementType("]");
    IElementType SUGGESTION_START = new KonversationElementType("[");
    IElementType TEXT = new KonversationElementType("text");
    IElementType UTTERANCE = new KonversationElementType("!");
    IElementType VARIABLE_DOLLAR = new KonversationElementType("$");
    IElementType VARIABLE_PERCENT = new KonversationElementType("%");
    IElementType VOICE_ONLY_BLOCK = new KonversationElementType("~");

    class Factory {
        public static PsiElement createElement(ASTNode node) {
            IElementType type = node.getElementType();
            if (type == BLOCK_DELIMITTER) {
                return new KonversationBlockDelimitterImpl(node);
            } else if (type == BLOCK_PART) {
                return new KonversationBlockPartImpl(node);
            } else if (type == COMMAND_DELIMITTER) {
                return new KonversationCommandDelimitterImpl(node);
            } else if (type == COMMENT) {
                return new KonversationCommentImpl(node);
            } else if (type == INTENT) {
                return new KonversationIntentImpl(node);
            } else if (type == INTENT_BLOCK) {
                return new KonversationIntentBlockImpl(node);
            } else if (type == INTENT_NAME) {
                return new KonversationIntentNameImpl(node);
            } else if (type == OUTPUT) {
                return new KonversationOutputImpl(node);
            } else if (type == PROMPT_BLOCK) {
                return new KonversationPromptBlockImpl(node);
            } else if (type == SUGGESTION) {
                return new KonversationSuggestionImpl(node);
            } else if (type == SUGGESTION_BLOCK) {
                return new KonversationSuggestionBlockImpl(node);
            } else if (type == SUGGESTION_LINE) {
                return new KonversationSuggestionLineImpl(node);
            } else if (type == TEXT_BLOCK) {
                return new KonversationTextBlockImpl(node);
            } else if (type == UTTERANCES_BLOCK) {
                return new KonversationUtterancesBlockImpl(node);
            } else if (type == UTTERANCE_LINE) {
                return new KonversationUtteranceLineImpl(node);
            } else if (type == UTTERENCE) {
                return new KonversationUtterenceImpl(node);
            } else if (type == VOICE_BLOCK) {
                return new KonversationVoiceBlockImpl(node);
            }
            throw new AssertionError("Unknown element type: " + type);
        }
    }
}
