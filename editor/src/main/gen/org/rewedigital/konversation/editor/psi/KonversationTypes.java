// This is a generated file. Not intended for manual editing.
package org.rewedigital.konversation.editor.psi;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.IElementType;

import org.rewedigital.konversation.editor.impl.KonversationAnnotationsImpl;
import org.rewedigital.konversation.editor.impl.KonversationBlockPartImpl;
import org.rewedigital.konversation.editor.impl.KonversationCommandDelimitterImpl;
import org.rewedigital.konversation.editor.impl.KonversationCommentImpl;
import org.rewedigital.konversation.editor.impl.KonversationConcatLineBreakImpl;
import org.rewedigital.konversation.editor.impl.KonversationConcatLineImpl;
import org.rewedigital.konversation.editor.impl.KonversationIntentBlockImpl;
import org.rewedigital.konversation.editor.impl.KonversationIntentDeclarationImpl;
import org.rewedigital.konversation.editor.impl.KonversationIntentNameCharsImpl;
import org.rewedigital.konversation.editor.impl.KonversationIntentNameImpl;
import org.rewedigital.konversation.editor.impl.KonversationLineImpl;
import org.rewedigital.konversation.editor.impl.KonversationPromptBlockImpl;
import org.rewedigital.konversation.editor.impl.KonversationRepromptBlockImpl;
import org.rewedigital.konversation.editor.impl.KonversationRepromptImpl;
import org.rewedigital.konversation.editor.impl.KonversationRepromptLineImpl;
import org.rewedigital.konversation.editor.impl.KonversationSuggestionBlockImpl;
import org.rewedigital.konversation.editor.impl.KonversationSuggestionImpl;
import org.rewedigital.konversation.editor.impl.KonversationSuggestionLineImpl;
import org.rewedigital.konversation.editor.impl.KonversationTextBlockImpl;
import org.rewedigital.konversation.editor.impl.KonversationTextPromptImpl;
import org.rewedigital.konversation.editor.impl.KonversationUtteranceLineImpl;
import org.rewedigital.konversation.editor.impl.KonversationUtterancesBlockImpl;
import org.rewedigital.konversation.editor.impl.KonversationUtterenceImpl;
import org.rewedigital.konversation.editor.impl.KonversationVoiceBlockImpl;
import org.rewedigital.konversation.editor.impl.KonversationVoicePromptImpl;

public interface KonversationTypes {

  IElementType ANNOTATIONS = new KonversationElementType("ANNOTATIONS");
  IElementType BLOCK_PART = new KonversationElementType("BLOCK_PART");
  IElementType COMMAND_DELIMITTER = new KonversationElementType("COMMAND_DELIMITTER");
  IElementType COMMENT = new KonversationElementType("COMMENT");
  IElementType CONCAT_LINE = new KonversationElementType("CONCAT_LINE");
  IElementType CONCAT_LINE_BREAK = new KonversationElementType("CONCAT_LINE_BREAK");
  IElementType INTENT_BLOCK = new KonversationElementType("INTENT_BLOCK");
  IElementType INTENT_DECLARATION = new KonversationElementType("INTENT_DECLARATION");
  IElementType INTENT_NAME = new KonversationElementType("INTENT_NAME");
  IElementType INTENT_NAME_CHARS = new KonversationElementType("INTENT_NAME_CHARS");
  IElementType LINE = new KonversationElementType("LINE");
  IElementType PROMPT_BLOCK = new KonversationElementType("PROMPT_BLOCK");
  IElementType REPROMPT = new KonversationElementType("REPROMPT");
  IElementType REPROMPT_BLOCK = new KonversationElementType("REPROMPT_BLOCK");
  IElementType REPROMPT_LINE = new KonversationElementType("REPROMPT_LINE");
  IElementType SUGGESTION = new KonversationElementType("SUGGESTION");
  IElementType SUGGESTION_BLOCK = new KonversationElementType("SUGGESTION_BLOCK");
  IElementType SUGGESTION_LINE = new KonversationElementType("SUGGESTION_LINE");
  IElementType TEXT_BLOCK = new KonversationElementType("TEXT_BLOCK");
  IElementType TEXT_PROMPT = new KonversationElementType("TEXT_PROMPT");
  IElementType UTTERANCES_BLOCK = new KonversationElementType("UTTERANCES_BLOCK");
  IElementType UTTERANCE_LINE = new KonversationElementType("UTTERANCE_LINE");
  IElementType UTTERENCE = new KonversationElementType("UTTERENCE");
  IElementType VOICE_BLOCK = new KonversationElementType("VOICE_BLOCK");
  IElementType VOICE_PROMPT = new KonversationElementType("VOICE_PROMPT");

  IElementType ANNOTATION = new KonversationElementType("@");
  IElementType BLOCK = new KonversationElementType("-");
  IElementType BLOCK_CONCAT = new KonversationElementType("+");
  IElementType COLON = new KonversationElementType(":");
  IElementType LEFT_BRACE = new KonversationElementType("{");
  IElementType REPROMPT_MARKER = new KonversationElementType("REPROMPT_MARKER");
  IElementType RIGHT_BRACE = new KonversationElementType("}");
  IElementType SHARP_COMMENT = new KonversationElementType("#");
  IElementType SLASH_COMMENT = new KonversationElementType("//");
  IElementType STRING = new KonversationElementType("string");
  IElementType SUGGESTION_END = new KonversationElementType("]");
  IElementType SUGGESTION_START = new KonversationElementType("[");
  IElementType UTTERANCE = new KonversationElementType("!");
  IElementType VARIABLE_DOLLAR = new KonversationElementType("$");
  IElementType VARIABLE_PERCENT = new KonversationElementType("%");
  IElementType VOICE_ONLY_BLOCK = new KonversationElementType("~");
  IElementType WHITE_SPACE = new KonversationElementType("WHITE_SPACE");

  class Factory {
    public static PsiElement createElement(ASTNode node) {
      IElementType type = node.getElementType();
      if (type == ANNOTATIONS) {
        return new KonversationAnnotationsImpl(node);
      } else if (type == BLOCK_PART) {
        return new KonversationBlockPartImpl(node);
      } else if (type == COMMAND_DELIMITTER) {
        return new KonversationCommandDelimitterImpl(node);
      } else if (type == COMMENT) {
        return new KonversationCommentImpl(node);
      } else if (type == CONCAT_LINE) {
        return new KonversationConcatLineImpl(node);
      } else if (type == CONCAT_LINE_BREAK) {
        return new KonversationConcatLineBreakImpl(node);
      } else if (type == INTENT_BLOCK) {
        return new KonversationIntentBlockImpl(node);
      } else if (type == INTENT_DECLARATION) {
        return new KonversationIntentDeclarationImpl(node);
      } else if (type == INTENT_NAME) {
        return new KonversationIntentNameImpl(node);
      } else if (type == INTENT_NAME_CHARS) {
        return new KonversationIntentNameCharsImpl(node);
      } else if (type == LINE) {
        return new KonversationLineImpl(node);
      } else if (type == PROMPT_BLOCK) {
        return new KonversationPromptBlockImpl(node);
      } else if (type == REPROMPT) {
        return new KonversationRepromptImpl(node);
      } else if (type == REPROMPT_BLOCK) {
        return new KonversationRepromptBlockImpl(node);
      } else if (type == REPROMPT_LINE) {
        return new KonversationRepromptLineImpl(node);
      } else if (type == SUGGESTION) {
        return new KonversationSuggestionImpl(node);
      } else if (type == SUGGESTION_BLOCK) {
        return new KonversationSuggestionBlockImpl(node);
      } else if (type == SUGGESTION_LINE) {
        return new KonversationSuggestionLineImpl(node);
      } else if (type == TEXT_BLOCK) {
        return new KonversationTextBlockImpl(node);
      } else if (type == TEXT_PROMPT) {
        return new KonversationTextPromptImpl(node);
      } else if (type == UTTERANCES_BLOCK) {
        return new KonversationUtterancesBlockImpl(node);
      } else if (type == UTTERANCE_LINE) {
        return new KonversationUtteranceLineImpl(node);
      } else if (type == UTTERENCE) {
        return new KonversationUtterenceImpl(node);
      } else if (type == VOICE_BLOCK) {
        return new KonversationVoiceBlockImpl(node);
      } else if (type == VOICE_PROMPT) {
        return new KonversationVoicePromptImpl(node);
      }
      throw new AssertionError("Unknown element type: " + type);
    }
  }
}
