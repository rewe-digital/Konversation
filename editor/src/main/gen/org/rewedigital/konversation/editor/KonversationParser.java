// This is a generated file. Not intended for manual editing.
package org.rewedigital.konversation.editor;

import com.intellij.lang.ASTNode;
import com.intellij.lang.LightPsiParser;
import com.intellij.lang.PsiBuilder;
import com.intellij.lang.PsiBuilder.Marker;
import com.intellij.lang.PsiParser;
import com.intellij.psi.tree.IElementType;

import static org.rewedigital.konversation.editor.KonversationParserUtil.TRUE_CONDITION;
import static org.rewedigital.konversation.editor.KonversationParserUtil._COLLAPSE_;
import static org.rewedigital.konversation.editor.KonversationParserUtil._NONE_;
import static org.rewedigital.konversation.editor.KonversationParserUtil.adapt_builder_;
import static org.rewedigital.konversation.editor.KonversationParserUtil.consumeToken;
import static org.rewedigital.konversation.editor.KonversationParserUtil.current_position_;
import static org.rewedigital.konversation.editor.KonversationParserUtil.empty_element_parsed_guard_;
import static org.rewedigital.konversation.editor.KonversationParserUtil.enter_section_;
import static org.rewedigital.konversation.editor.KonversationParserUtil.exit_section_;
import static org.rewedigital.konversation.editor.KonversationParserUtil.nextTokenIs;
import static org.rewedigital.konversation.editor.KonversationParserUtil.parseTokens;
import static org.rewedigital.konversation.editor.KonversationParserUtil.recursion_guard_;
import static org.rewedigital.konversation.editor.psi.KonversationTypes.ANNOTATION;
import static org.rewedigital.konversation.editor.psi.KonversationTypes.ANNOTATIONS;
import static org.rewedigital.konversation.editor.psi.KonversationTypes.BLOCK;
import static org.rewedigital.konversation.editor.psi.KonversationTypes.BLOCK_CONCAT;
import static org.rewedigital.konversation.editor.psi.KonversationTypes.BLOCK_PART;
import static org.rewedigital.konversation.editor.psi.KonversationTypes.COLON;
import static org.rewedigital.konversation.editor.psi.KonversationTypes.COMMAND_DELIMITTER;
import static org.rewedigital.konversation.editor.psi.KonversationTypes.COMMENT;
import static org.rewedigital.konversation.editor.psi.KonversationTypes.CONCAT_LINE;
import static org.rewedigital.konversation.editor.psi.KonversationTypes.CONCAT_LINE_BREAK;
import static org.rewedigital.konversation.editor.psi.KonversationTypes.INTENT_BLOCK;
import static org.rewedigital.konversation.editor.psi.KonversationTypes.INTENT_DECLARATION;
import static org.rewedigital.konversation.editor.psi.KonversationTypes.INTENT_NAME;
import static org.rewedigital.konversation.editor.psi.KonversationTypes.INTENT_NAME_CHARS;
import static org.rewedigital.konversation.editor.psi.KonversationTypes.LEFT_BRACE;
import static org.rewedigital.konversation.editor.psi.KonversationTypes.LINE;
import static org.rewedigital.konversation.editor.psi.KonversationTypes.PROMPT_BLOCK;
import static org.rewedigital.konversation.editor.psi.KonversationTypes.REPROMPT;
import static org.rewedigital.konversation.editor.psi.KonversationTypes.REPROMPT_BLOCK;
import static org.rewedigital.konversation.editor.psi.KonversationTypes.REPROMPT_LINE;
import static org.rewedigital.konversation.editor.psi.KonversationTypes.REPROMPT_MARKER;
import static org.rewedigital.konversation.editor.psi.KonversationTypes.RIGHT_BRACE;
import static org.rewedigital.konversation.editor.psi.KonversationTypes.SHARP_COMMENT;
import static org.rewedigital.konversation.editor.psi.KonversationTypes.SLASH_COMMENT;
import static org.rewedigital.konversation.editor.psi.KonversationTypes.STRING;
import static org.rewedigital.konversation.editor.psi.KonversationTypes.SUGGESTION;
import static org.rewedigital.konversation.editor.psi.KonversationTypes.SUGGESTION_BLOCK;
import static org.rewedigital.konversation.editor.psi.KonversationTypes.SUGGESTION_END;
import static org.rewedigital.konversation.editor.psi.KonversationTypes.SUGGESTION_LINE;
import static org.rewedigital.konversation.editor.psi.KonversationTypes.SUGGESTION_START;
import static org.rewedigital.konversation.editor.psi.KonversationTypes.TEXT_BLOCK;
import static org.rewedigital.konversation.editor.psi.KonversationTypes.TEXT_PROMPT;
import static org.rewedigital.konversation.editor.psi.KonversationTypes.UTTERANCE;
import static org.rewedigital.konversation.editor.psi.KonversationTypes.UTTERANCES_BLOCK;
import static org.rewedigital.konversation.editor.psi.KonversationTypes.UTTERANCE_LINE;
import static org.rewedigital.konversation.editor.psi.KonversationTypes.UTTERENCE;
import static org.rewedigital.konversation.editor.psi.KonversationTypes.VARIABLE_DOLLAR;
import static org.rewedigital.konversation.editor.psi.KonversationTypes.VARIABLE_PERCENT;
import static org.rewedigital.konversation.editor.psi.KonversationTypes.VOICE_BLOCK;
import static org.rewedigital.konversation.editor.psi.KonversationTypes.VOICE_ONLY_BLOCK;
import static org.rewedigital.konversation.editor.psi.KonversationTypes.VOICE_PROMPT;
import static org.rewedigital.konversation.editor.psi.KonversationTypes.WHITE_SPACE;

@SuppressWarnings({"SimplifiableIfStatement", "UnusedAssignment"})
public class KonversationParser implements PsiParser, LightPsiParser {

  public ASTNode parse(IElementType t, PsiBuilder b) {
    parseLight(t, b);
    return b.getTreeBuilt();
  }

    public void parseLight(IElementType t, PsiBuilder b) {
        boolean r;
        b = adapt_builder_(t, b, this, null);
        Marker m = enter_section_(b, 0, _COLLAPSE_, null);
        r = parse_root_(t, b);
        exit_section_(b, 0, m, t, r, true, TRUE_CONDITION);
    }

    protected boolean parse_root_(IElementType t, PsiBuilder b) {
        return parse_root_(t, b, 0);
    }

    static boolean parse_root_(IElementType t, PsiBuilder b, int l) {
        return file(b, l + 1);
    }

    /* ********************************************************** */
    // line
    public static boolean Reprompt(PsiBuilder b, int l) {
        if (!recursion_guard_(b, l, "Reprompt")) {
            return false;
        }
        if (!nextTokenIs(b, STRING)) {
            return false;
        }
        boolean r;
        Marker m = enter_section_(b);
        r = line(b, l + 1);
        exit_section_(b, m, REPROMPT, r);
        return r;
    }

    /* ********************************************************** */
    // line
    public static boolean TextPrompt(PsiBuilder b, int l) {
        if (!recursion_guard_(b, l, "TextPrompt")) {
            return false;
        }
        if (!nextTokenIs(b, STRING)) {
            return false;
        }
        boolean r;
        Marker m = enter_section_(b);
        r = line(b, l + 1);
        exit_section_(b, m, TEXT_PROMPT, r);
        return r;
    }

    /* ********************************************************** */
    // line
    public static boolean VoicePrompt(PsiBuilder b, int l) {
        if (!recursion_guard_(b, l, "VoicePrompt")) {
            return false;
        }
        if (!nextTokenIs(b, STRING)) {
            return false;
        }
        boolean r;
        Marker m = enter_section_(b);
        r = line(b, l + 1);
        exit_section_(b, m, VOICE_PROMPT, r);
        return r;
    }

    /* ********************************************************** */
    // ANNOTATION line commandDelimitter
    public static boolean annotations(PsiBuilder b, int l) {
        if (!recursion_guard_(b, l, "annotations")) {
            return false;
        }
        if (!nextTokenIs(b, ANNOTATION)) {
            return false;
        }
        boolean r;
        Marker m = enter_section_(b);
        r = consumeToken(b, ANNOTATION);
        r = r && line(b, l + 1);
        r = r && commandDelimitter(b, l + 1);
        exit_section_(b, m, ANNOTATIONS, r);
        return r;
    }

    /* ********************************************************** */
    // textBlock | voiceBlock | concatLine | concatLineBreak | commandDelimitter
    public static boolean blockPart(PsiBuilder b, int l) {
        if (!recursion_guard_(b, l, "blockPart")) {
            return false;
        }
        boolean r;
        Marker m = enter_section_(b, l, _NONE_, BLOCK_PART, "<block part>");
        r = textBlock(b, l + 1);
        if (!r) {
            r = voiceBlock(b, l + 1);
        }
        if (!r) {
            r = concatLine(b, l + 1);
        }
        if (!r) {
            r = concatLineBreak(b, l + 1);
        }
        if (!r) {
            r = commandDelimitter(b, l + 1);
        }
        exit_section_(b, l, m, r, false, null);
        return r;
    }

  /* ********************************************************** */
  // comment? WHITE_SPACE
  public static boolean commandDelimitter(PsiBuilder b, int l) {
      if (!recursion_guard_(b, l, "commandDelimitter")) {
          return false;
      }
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, COMMAND_DELIMITTER, "<command delimitter>");
      r = commandDelimitter_0(b, l + 1);
      r = r && consumeToken(b, WHITE_SPACE);
      exit_section_(b, l, m, r, false, null);
    return r;
  }

    // comment?
  private static boolean commandDelimitter_0(PsiBuilder b, int l) {
      if (!recursion_guard_(b, l, "commandDelimitter_0")) {
          return false;
      }
      comment(b, l + 1);
      return true;
  }

  /* ********************************************************** */
  // (SHARP_COMMENT | SLASH_COMMENT) (ANNOTATION | COLON | UTTERANCE | BLOCK | VOICE_ONLY_BLOCK | BLOCK_CONCAT | LEFT_BRACE | RIGHT_BRACE | SUGGESTION_START | SUGGESTION_END | VARIABLE_DOLLAR | VARIABLE_PERCENT | SHARP_COMMENT | SLASH_COMMENT | REPROMPT_MARKER | '?' | line)+
  public static boolean comment(PsiBuilder b, int l) {
      if (!recursion_guard_(b, l, "comment")) {
          return false;
      }
      if (!nextTokenIs(b, "<comment>", SHARP_COMMENT, SLASH_COMMENT)) {
          return false;
      }
      boolean r;
      Marker m = enter_section_(b, l, _NONE_, COMMENT, "<comment>");
      r = comment_0(b, l + 1);
      r = r && comment_1(b, l + 1);
      exit_section_(b, l, m, r, false, null);
      return r;
  }

    // SHARP_COMMENT | SLASH_COMMENT
    private static boolean comment_0(PsiBuilder b, int l) {
        if (!recursion_guard_(b, l, "comment_0")) {
            return false;
        }
        boolean r;
        r = consumeToken(b, SHARP_COMMENT);
        if (!r) {
            r = consumeToken(b, SLASH_COMMENT);
        }
        return r;
    }

    // (ANNOTATION | COLON | UTTERANCE | BLOCK | VOICE_ONLY_BLOCK | BLOCK_CONCAT | LEFT_BRACE | RIGHT_BRACE | SUGGESTION_START | SUGGESTION_END | VARIABLE_DOLLAR | VARIABLE_PERCENT | SHARP_COMMENT | SLASH_COMMENT | REPROMPT_MARKER | '?' | line)+
    private static boolean comment_1(PsiBuilder b, int l) {
        if (!recursion_guard_(b, l, "comment_1")) {
            return false;
        }
        boolean r;
        Marker m = enter_section_(b);
        r = comment_1_0(b, l + 1);
        while (r) {
            int c = current_position_(b);
            if (!comment_1_0(b, l + 1)) {
                break;
            }
            if (!empty_element_parsed_guard_(b, "comment_1", c)) {
                break;
            }
        }
        exit_section_(b, m, null, r);
        return r;
    }

    // ANNOTATION | COLON | UTTERANCE | BLOCK | VOICE_ONLY_BLOCK | BLOCK_CONCAT | LEFT_BRACE | RIGHT_BRACE | SUGGESTION_START | SUGGESTION_END | VARIABLE_DOLLAR | VARIABLE_PERCENT | SHARP_COMMENT | SLASH_COMMENT | REPROMPT_MARKER | '?' | line
    private static boolean comment_1_0(PsiBuilder b, int l) {
        if (!recursion_guard_(b, l, "comment_1_0")) {
            return false;
        }
        boolean r;
        Marker m = enter_section_(b);
        r = consumeToken(b, ANNOTATION);
        if (!r) {
            r = consumeToken(b, COLON);
        }
        if (!r) {
            r = consumeToken(b, UTTERANCE);
        }
        if (!r) {
            r = consumeToken(b, BLOCK);
        }
        if (!r) {
            r = consumeToken(b, VOICE_ONLY_BLOCK);
        }
        if (!r) {
            r = consumeToken(b, BLOCK_CONCAT);
        }
        if (!r) {
            r = consumeToken(b, LEFT_BRACE);
        }
        if (!r) {
            r = consumeToken(b, RIGHT_BRACE);
        }
        if (!r) {
            r = consumeToken(b, SUGGESTION_START);
        }
        if (!r) {
            r = consumeToken(b, SUGGESTION_END);
        }
        if (!r) {
            r = consumeToken(b, VARIABLE_DOLLAR);
        }
        if (!r) {
            r = consumeToken(b, VARIABLE_PERCENT);
        }
        if (!r) {
            r = consumeToken(b, SHARP_COMMENT);
        }
        if (!r) {
            r = consumeToken(b, SLASH_COMMENT);
        }
        if (!r) {
            r = consumeToken(b, REPROMPT_MARKER);
        }
        if (!r) {
            r = consumeToken(b, "?");
        }
        if (!r) {
            r = line(b, l + 1);
        }
        exit_section_(b, m, null, r);
        return r;
    }

    /* ********************************************************** */
    // '+' commandDelimitter
    public static boolean concatLine(PsiBuilder b, int l) {
        if (!recursion_guard_(b, l, "concatLine")) {
            return false;
        }
        if (!nextTokenIs(b, BLOCK_CONCAT)) {
            return false;
        }
        boolean r;
        Marker m = enter_section_(b);
        r = consumeToken(b, BLOCK_CONCAT);
        r = r && commandDelimitter(b, l + 1);
        exit_section_(b, m, CONCAT_LINE, r);
        return r;
    }

    /* ********************************************************** */
    // '-' commandDelimitter
    public static boolean concatLineBreak(PsiBuilder b, int l) {
        if (!recursion_guard_(b, l, "concatLineBreak")) {
            return false;
        }
        if (!nextTokenIs(b, BLOCK)) {
            return false;
        }
        boolean r;
        Marker m = enter_section_(b);
        r = consumeToken(b, BLOCK);
        r = r && commandDelimitter(b, l + 1);
        exit_section_(b, m, CONCAT_LINE_BREAK, r);
        return r;
    }

    /* ********************************************************** */
    // commandDelimitter* (intentBlock commandDelimitter*)*
    static boolean file(PsiBuilder b, int l) {
        if (!recursion_guard_(b, l, "file")) {
            return false;
        }
        boolean r;
        Marker m = enter_section_(b);
        r = file_0(b, l + 1);
        r = r && file_1(b, l + 1);
        exit_section_(b, m, null, r);
        return r;
    }

    // commandDelimitter*
    private static boolean file_0(PsiBuilder b, int l) {
        if (!recursion_guard_(b, l, "file_0")) {
            return false;
        }
        while (true) {
            int c = current_position_(b);
            if (!commandDelimitter(b, l + 1)) {
                break;
            }
            if (!empty_element_parsed_guard_(b, "file_0", c)) {
                break;
            }
        }
        return true;
    }

    // (intentBlock commandDelimitter*)*
    private static boolean file_1(PsiBuilder b, int l) {
        if (!recursion_guard_(b, l, "file_1")) {
            return false;
        }
        while (true) {
            int c = current_position_(b);
            if (!file_1_0(b, l + 1)) {
                break;
            }
            if (!empty_element_parsed_guard_(b, "file_1", c)) {
                break;
            }
        }
        return true;
    }

    // intentBlock commandDelimitter*
    private static boolean file_1_0(PsiBuilder b, int l) {
        if (!recursion_guard_(b, l, "file_1_0")) {
            return false;
        }
        boolean r;
        Marker m = enter_section_(b);
        r = intentBlock(b, l + 1);
        r = r && file_1_0_1(b, l + 1);
        exit_section_(b, m, null, r);
        return r;
    }

    // commandDelimitter*
    private static boolean file_1_0_1(PsiBuilder b, int l) {
        if (!recursion_guard_(b, l, "file_1_0_1")) {
            return false;
        }
        while (true) {
            int c = current_position_(b);
            if (!commandDelimitter(b, l + 1)) {
                break;
            }
            if (!empty_element_parsed_guard_(b, "file_1_0_1", c)) {
                break;
            }
        }
        return true;
    }

    /* ********************************************************** */
    // intentDeclaration utterancesBlock promptBlock repromptBlock suggestionLine
    public static boolean intentBlock(PsiBuilder b, int l) {
        if (!recursion_guard_(b, l, "intentBlock")) {
            return false;
        }
        if (!nextTokenIs(b, "<intent block>", ANNOTATION, STRING)) {
            return false;
        }
        boolean r;
        Marker m = enter_section_(b, l, _NONE_, INTENT_BLOCK, "<intent block>");
        r = intentDeclaration(b, l + 1);
        r = r && utterancesBlock(b, l + 1);
        r = r && promptBlock(b, l + 1);
        r = r && repromptBlock(b, l + 1);
        r = r && suggestionLine(b, l + 1);
        exit_section_(b, l, m, r, false, null);
        return r;
    }

  /* ********************************************************** */
  // annotations* intentName COLON commandDelimitter
  public static boolean intentDeclaration(PsiBuilder b, int l) {
      if (!recursion_guard_(b, l, "intentDeclaration")) {
          return false;
      }
      if (!nextTokenIs(b, "<intent declaration>", ANNOTATION, STRING)) {
          return false;
      }
      boolean r;
      Marker m = enter_section_(b, l, _NONE_, INTENT_DECLARATION, "<intent declaration>");
      r = intentDeclaration_0(b, l + 1);
      r = r && intentName(b, l + 1);
      r = r && consumeToken(b, COLON);
      r = r && commandDelimitter(b, l + 1);
      exit_section_(b, l, m, r, false, null);
      return r;
  }

    // annotations*
    private static boolean intentDeclaration_0(PsiBuilder b, int l) {
        if (!recursion_guard_(b, l, "intentDeclaration_0")) {
            return false;
        }
        while (true) {
            int c = current_position_(b);
            if (!annotations(b, l + 1)) {
                break;
            }
            if (!empty_element_parsed_guard_(b, "intentDeclaration_0", c)) {
                break;
            }
        }
        return true;
    }

    /* ********************************************************** */
    // intentNameChars
    public static boolean intentName(PsiBuilder b, int l) {
        if (!recursion_guard_(b, l, "intentName")) {
            return false;
        }
        if (!nextTokenIs(b, STRING)) {
            return false;
        }
        boolean r;
        Marker m = enter_section_(b);
        r = intentNameChars(b, l + 1);
        exit_section_(b, m, INTENT_NAME, r);
        return r;
    }

  /* ********************************************************** */
  // (string BLOCK | string)+
  public static boolean intentNameChars(PsiBuilder b, int l) {
      if (!recursion_guard_(b, l, "intentNameChars")) {
          return false;
      }
      if (!nextTokenIs(b, STRING)) {
          return false;
      }
      boolean r;
      Marker m = enter_section_(b);
      r = intentNameChars_0(b, l + 1);
      while (r) {
          int c = current_position_(b);
          if (!intentNameChars_0(b, l + 1)) {
              break;
          }
          if (!empty_element_parsed_guard_(b, "intentNameChars", c)) {
              break;
          }
      }
      exit_section_(b, m, INTENT_NAME_CHARS, r);
      return r;
  }

  // string BLOCK | string
  private static boolean intentNameChars_0(PsiBuilder b, int l) {
      if (!recursion_guard_(b, l, "intentNameChars_0")) {
          return false;
      }
    boolean r;
    Marker m = enter_section_(b);
    r = parseTokens(b, 0, STRING, BLOCK);
      if (!r) {
          r = consumeToken(b, STRING);
      }
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // (string '@' | string ':' | string '!' | string '+' | string '-' | string '?' | string '/' | string)+
  public static boolean line(PsiBuilder b, int l) {
      if (!recursion_guard_(b, l, "line")) {
          return false;
      }
      if (!nextTokenIs(b, STRING)) {
          return false;
      }
      boolean r;
      Marker m = enter_section_(b);
      r = line_0(b, l + 1);
      while (r) {
          int c = current_position_(b);
          if (!line_0(b, l + 1)) {
              break;
          }
          if (!empty_element_parsed_guard_(b, "line", c)) {
              break;
          }
      }
      exit_section_(b, m, LINE, r);
      return r;
  }

    // string '@' | string ':' | string '!' | string '+' | string '-' | string '?' | string '/' | string
  private static boolean line_0(PsiBuilder b, int l) {
      if (!recursion_guard_(b, l, "line_0")) {
          return false;
      }
      boolean r;
      Marker m = enter_section_(b);
      r = parseTokens(b, 0, STRING, ANNOTATION);
      if (!r) {
          r = parseTokens(b, 0, STRING, COLON);
      }
      if (!r) {
          r = parseTokens(b, 0, STRING, UTTERANCE);
      }
      if (!r) {
          r = parseTokens(b, 0, STRING, BLOCK_CONCAT);
      }
      if (!r) {
          r = parseTokens(b, 0, STRING, BLOCK);
      }
      if (!r) {
          r = line_0_5(b, l + 1);
      }
      if (!r) {
          r = line_0_6(b, l + 1);
      }
      if (!r) {
          r = consumeToken(b, STRING);
      }
      exit_section_(b, m, null, r);
      return r;
  }

    // string '?'
    private static boolean line_0_5(PsiBuilder b, int l) {
        if (!recursion_guard_(b, l, "line_0_5")) {
            return false;
        }
        boolean r;
        Marker m = enter_section_(b);
        r = consumeToken(b, STRING);
        r = r && consumeToken(b, "?");
        exit_section_(b, m, null, r);
        return r;
    }

    // string '/'
    private static boolean line_0_6(PsiBuilder b, int l) {
        if (!recursion_guard_(b, l, "line_0_6")) {
            return false;
        }
        boolean r;
        Marker m = enter_section_(b);
        r = consumeToken(b, STRING);
        r = r && consumeToken(b, "/");
        exit_section_(b, m, null, r);
        return r;
    }

    /* ********************************************************** */
    // blockPart*
  public static boolean promptBlock(PsiBuilder b, int l) {
      if (!recursion_guard_(b, l, "promptBlock")) {
          return false;
      }
      Marker m = enter_section_(b, l, _NONE_, PROMPT_BLOCK, "<prompt block>");
      while (true) {
          int c = current_position_(b);
          if (!blockPart(b, l + 1)) {
              break;
          }
          if (!empty_element_parsed_guard_(b, "promptBlock", c)) {
              break;
          }
      }
      exit_section_(b, l, m, true, false, null);
      return true;
  }

  /* ********************************************************** */
  // (repromptLine commandDelimitter)*
  public static boolean repromptBlock(PsiBuilder b, int l) {
      if (!recursion_guard_(b, l, "repromptBlock")) {
          return false;
      }
    Marker m = enter_section_(b, l, _NONE_, REPROMPT_BLOCK, "<reprompt block>");
    while (true) {
        int c = current_position_(b);
        if (!repromptBlock_0(b, l + 1)) {
            break;
        }
        if (!empty_element_parsed_guard_(b, "repromptBlock", c)) {
            break;
        }
    }
    exit_section_(b, l, m, true, false, null);
    return true;
  }

    // repromptLine commandDelimitter
    private static boolean repromptBlock_0(PsiBuilder b, int l) {
        if (!recursion_guard_(b, l, "repromptBlock_0")) {
            return false;
        }
        boolean r;
        Marker m = enter_section_(b);
        r = repromptLine(b, l + 1);
        r = r && commandDelimitter(b, l + 1);
        exit_section_(b, m, null, r);
        return r;
    }

    /* ********************************************************** */
    // REPROMPT_MARKER Reprompt
    public static boolean repromptLine(PsiBuilder b, int l) {
        if (!recursion_guard_(b, l, "repromptLine")) {
            return false;
        }
        if (!nextTokenIs(b, REPROMPT_MARKER)) {
            return false;
        }
        boolean r;
        Marker m = enter_section_(b);
        r = consumeToken(b, REPROMPT_MARKER);
        r = r && Reprompt(b, l + 1);
        exit_section_(b, m, REPROMPT_LINE, r);
        return r;
    }

    /* ********************************************************** */
    // line
    public static boolean suggestion(PsiBuilder b, int l) {
        if (!recursion_guard_(b, l, "suggestion")) {
            return false;
        }
        if (!nextTokenIs(b, STRING)) {
            return false;
        }
        boolean r;
        Marker m = enter_section_(b);
        r = line(b, l + 1);
        exit_section_(b, m, SUGGESTION, r);
        return r;
    }

  /* ********************************************************** */
  // SUGGESTION_START suggestion SUGGESTION_END
  public static boolean suggestionBlock(PsiBuilder b, int l) {
      if (!recursion_guard_(b, l, "suggestionBlock")) {
          return false;
      }
      if (!nextTokenIs(b, SUGGESTION_START)) {
          return false;
      }
      boolean r;
      Marker m = enter_section_(b);
      r = consumeToken(b, SUGGESTION_START);
      r = r && suggestion(b, l + 1);
      r = r && consumeToken(b, SUGGESTION_END);
      exit_section_(b, m, SUGGESTION_BLOCK, r);
      return r;
  }

  /* ********************************************************** */
  // (' '* suggestionBlock)*
  public static boolean suggestionLine(PsiBuilder b, int l) {
      if (!recursion_guard_(b, l, "suggestionLine")) {
          return false;
      }
    Marker m = enter_section_(b, l, _NONE_, SUGGESTION_LINE, "<suggestion line>");
    while (true) {
        int c = current_position_(b);
        if (!suggestionLine_0(b, l + 1)) {
            break;
        }
        if (!empty_element_parsed_guard_(b, "suggestionLine", c)) {
            break;
        }
    }
    exit_section_(b, l, m, true, false, null);
    return true;
  }

  // ' '* suggestionBlock
  private static boolean suggestionLine_0(PsiBuilder b, int l) {
      if (!recursion_guard_(b, l, "suggestionLine_0")) {
          return false;
      }
    boolean r;
    Marker m = enter_section_(b);
    r = suggestionLine_0_0(b, l + 1);
    r = r && suggestionBlock(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // ' '*
  private static boolean suggestionLine_0_0(PsiBuilder b, int l) {
      if (!recursion_guard_(b, l, "suggestionLine_0_0")) {
          return false;
      }
    while (true) {
        int c = current_position_(b);
        if (!consumeToken(b, " ")) {
            break;
        }
        if (!empty_element_parsed_guard_(b, "suggestionLine_0_0", c)) {
            break;
        }
    }
    return true;
  }

  /* ********************************************************** */
  // ('-' TextPrompt commandDelimitter)+
  public static boolean textBlock(PsiBuilder b, int l) {
      if (!recursion_guard_(b, l, "textBlock")) {
          return false;
      }
      if (!nextTokenIs(b, BLOCK)) {
          return false;
      }
      boolean r;
      Marker m = enter_section_(b);
      r = textBlock_0(b, l + 1);
      while (r) {
          int c = current_position_(b);
          if (!textBlock_0(b, l + 1)) {
              break;
          }
          if (!empty_element_parsed_guard_(b, "textBlock", c)) {
              break;
          }
      }
      exit_section_(b, m, TEXT_BLOCK, r);
      return r;
  }

    // '-' TextPrompt commandDelimitter
  private static boolean textBlock_0(PsiBuilder b, int l) {
      if (!recursion_guard_(b, l, "textBlock_0")) {
          return false;
      }
    boolean r;
    Marker m = enter_section_(b);
      r = consumeToken(b, BLOCK);
      r = r && TextPrompt(b, l + 1);
      r = r && commandDelimitter(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // '!' utterence commandDelimitter
  public static boolean utteranceLine(PsiBuilder b, int l) {
      if (!recursion_guard_(b, l, "utteranceLine")) {
          return false;
      }
      if (!nextTokenIs(b, UTTERANCE)) {
          return false;
      }
      boolean r;
      Marker m = enter_section_(b);
      r = consumeToken(b, UTTERANCE);
      r = r && utterence(b, l + 1);
      r = r && commandDelimitter(b, l + 1);
      exit_section_(b, m, UTTERANCE_LINE, r);
      return r;
  }

  /* ********************************************************** */
  // utteranceLine*
  public static boolean utterancesBlock(PsiBuilder b, int l) {
      if (!recursion_guard_(b, l, "utterancesBlock")) {
          return false;
      }
    Marker m = enter_section_(b, l, _NONE_, UTTERANCES_BLOCK, "<utterances block>");
    while (true) {
        int c = current_position_(b);
        if (!utteranceLine(b, l + 1)) {
            break;
        }
        if (!empty_element_parsed_guard_(b, "utterancesBlock", c)) {
            break;
        }
    }
    exit_section_(b, l, m, true, false, null);
    return true;
  }

  /* ********************************************************** */
  // line
  public static boolean utterence(PsiBuilder b, int l) {
      if (!recursion_guard_(b, l, "utterence")) {
          return false;
      }
      if (!nextTokenIs(b, STRING)) {
          return false;
      }
      boolean r;
      Marker m = enter_section_(b);
      r = line(b, l + 1);
      exit_section_(b, m, UTTERENCE, r);
      return r;
  }

  /* ********************************************************** */
  // ('~' VoicePrompt commandDelimitter)+
  public static boolean voiceBlock(PsiBuilder b, int l) {
      if (!recursion_guard_(b, l, "voiceBlock")) {
          return false;
      }
      if (!nextTokenIs(b, VOICE_ONLY_BLOCK)) {
          return false;
      }
      boolean r;
      Marker m = enter_section_(b);
      r = voiceBlock_0(b, l + 1);
      while (r) {
          int c = current_position_(b);
          if (!voiceBlock_0(b, l + 1)) {
              break;
          }
          if (!empty_element_parsed_guard_(b, "voiceBlock", c)) {
              break;
          }
      }
      exit_section_(b, m, VOICE_BLOCK, r);
      return r;
  }

    // '~' VoicePrompt commandDelimitter
  private static boolean voiceBlock_0(PsiBuilder b, int l) {
      if (!recursion_guard_(b, l, "voiceBlock_0")) {
          return false;
      }
    boolean r;
    Marker m = enter_section_(b);
      r = consumeToken(b, VOICE_ONLY_BLOCK);
      r = r && VoicePrompt(b, l + 1);
      r = r && commandDelimitter(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

}
