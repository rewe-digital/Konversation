// This is a generated file. Not intended for manual editing.
package org.rewedigital.konversation.editor;

import com.intellij.lang.ASTNode;
import com.intellij.lang.LightPsiParser;
import com.intellij.lang.PsiBuilder;
import com.intellij.lang.PsiBuilder.Marker;
import com.intellij.lang.PsiParser;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.IFileElementType;

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
import static org.rewedigital.konversation.editor.KonversationParserUtil.recursion_guard_;
import static org.rewedigital.konversation.editor.psi.KonversationTypes.BLOCK;
import static org.rewedigital.konversation.editor.psi.KonversationTypes.BLOCK_CONCAT;
import static org.rewedigital.konversation.editor.psi.KonversationTypes.BLOCK_DELIMITTER;
import static org.rewedigital.konversation.editor.psi.KonversationTypes.BLOCK_PART;
import static org.rewedigital.konversation.editor.psi.KonversationTypes.CHARSEQUENCE;
import static org.rewedigital.konversation.editor.psi.KonversationTypes.COLON;
import static org.rewedigital.konversation.editor.psi.KonversationTypes.COMMAND_DELIMITTER;
import static org.rewedigital.konversation.editor.psi.KonversationTypes.COMMENT;
import static org.rewedigital.konversation.editor.psi.KonversationTypes.INTENT;
import static org.rewedigital.konversation.editor.psi.KonversationTypes.INTENT_BLOCK;
import static org.rewedigital.konversation.editor.psi.KonversationTypes.INTENT_NAME;
import static org.rewedigital.konversation.editor.psi.KonversationTypes.OUTPUT;
import static org.rewedigital.konversation.editor.psi.KonversationTypes.PROMPT_BLOCK;
import static org.rewedigital.konversation.editor.psi.KonversationTypes.SUGGESTION;
import static org.rewedigital.konversation.editor.psi.KonversationTypes.SUGGESTION_BLOCK;
import static org.rewedigital.konversation.editor.psi.KonversationTypes.SUGGESTION_END;
import static org.rewedigital.konversation.editor.psi.KonversationTypes.SUGGESTION_LINE;
import static org.rewedigital.konversation.editor.psi.KonversationTypes.SUGGESTION_START;
import static org.rewedigital.konversation.editor.psi.KonversationTypes.TEXT;
import static org.rewedigital.konversation.editor.psi.KonversationTypes.TEXT_BLOCK;
import static org.rewedigital.konversation.editor.psi.KonversationTypes.UTTERANCE;
import static org.rewedigital.konversation.editor.psi.KonversationTypes.UTTERANCES_BLOCK;
import static org.rewedigital.konversation.editor.psi.KonversationTypes.UTTERANCE_LINE;
import static org.rewedigital.konversation.editor.psi.KonversationTypes.UTTERENCE;
import static org.rewedigital.konversation.editor.psi.KonversationTypes.VOICE_BLOCK;
import static org.rewedigital.konversation.editor.psi.KonversationTypes.VOICE_ONLY_BLOCK;

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
        if (t instanceof IFileElementType) {
            r = parse_root_(t, b, 0);
        } else {
            r = false;
        }
        exit_section_(b, 0, m, t, r, true, TRUE_CONDITION);
    }

    protected boolean parse_root_(IElementType t, PsiBuilder b, int l) {
        return file(b, l + 1);
    }

    /* ********************************************************** */
    // "-" commandDelimitter | "+" commandDelimitter
    public static boolean blockDelimitter(PsiBuilder b, int l) {
        if (!recursion_guard_(b, l, "blockDelimitter")) {
            return false;
        }
        if (!nextTokenIs(b, "<block delimitter>", BLOCK, BLOCK_CONCAT)) {
            return false;
        }
        boolean r;
        Marker m = enter_section_(b, l, _NONE_, BLOCK_DELIMITTER, "<block delimitter>");
        r = blockDelimitter_0(b, l + 1);
        if (!r) {
            r = blockDelimitter_1(b, l + 1);
        }
        exit_section_(b, l, m, r, false, null);
        return r;
    }

    // "-" commandDelimitter
    private static boolean blockDelimitter_0(PsiBuilder b, int l) {
        if (!recursion_guard_(b, l, "blockDelimitter_0")) {
            return false;
        }
        boolean r;
        Marker m = enter_section_(b);
        r = consumeToken(b, BLOCK);
        r = r && commandDelimitter(b, l + 1);
        exit_section_(b, m, null, r);
        return r;
    }

    // "+" commandDelimitter
    private static boolean blockDelimitter_1(PsiBuilder b, int l) {
        if (!recursion_guard_(b, l, "blockDelimitter_1")) {
            return false;
        }
        boolean r;
        Marker m = enter_section_(b);
        r = consumeToken(b, BLOCK_CONCAT);
        r = r && commandDelimitter(b, l + 1);
        exit_section_(b, m, null, r);
        return r;
    }

    /* ********************************************************** */
    // textBlock | voiceBlock
    public static boolean blockPart(PsiBuilder b, int l) {
        if (!recursion_guard_(b, l, "blockPart")) {
            return false;
        }
        if (!nextTokenIs(b, "<block part>", BLOCK, VOICE_ONLY_BLOCK)) {
            return false;
        }
        boolean r;
        Marker m = enter_section_(b, l, _NONE_, BLOCK_PART, "<block part>");
        r = textBlock(b, l + 1);
        if (!r) {
            r = voiceBlock(b, l + 1);
        }
        exit_section_(b, l, m, r, false, null);
        return r;
    }

    /* ********************************************************** */
    // comment*
    public static boolean commandDelimitter(PsiBuilder b, int l) {
        if (!recursion_guard_(b, l, "commandDelimitter")) {
            return false;
        }
        Marker m = enter_section_(b, l, _NONE_, COMMAND_DELIMITTER, "<command delimitter>");
        while (true) {
            int c = current_position_(b);
            if (!comment(b, l + 1)) {
                break;
            }
            if (!empty_element_parsed_guard_(b, "commandDelimitter", c)) {
                break;
            }
        }
        exit_section_(b, l, m, true, false, null);
        return true;
    }

    /* ********************************************************** */
    // ('#' | '//') text
    public static boolean comment(PsiBuilder b, int l) {
        if (!recursion_guard_(b, l, "comment")) {
            return false;
        }
        boolean r;
        Marker m = enter_section_(b, l, _NONE_, COMMENT, "<comment>");
        r = comment_0(b, l + 1);
        r = r && consumeToken(b, TEXT);
        exit_section_(b, l, m, r, false, null);
        return r;
    }

    // '#' | '//'
    private static boolean comment_0(PsiBuilder b, int l) {
        if (!recursion_guard_(b, l, "comment_0")) {
            return false;
        }
        boolean r;
        Marker m = enter_section_(b);
        r = consumeToken(b, "#");
        if (!r) {
            r = consumeToken(b, "//");
        }
        exit_section_(b, m, null, r);
        return r;
    }

    /* ********************************************************** */
    // (intentBlock commandDelimitter)*
    static boolean file(PsiBuilder b, int l) {
        if (!recursion_guard_(b, l, "file")) {
            return false;
        }
        while (true) {
            int c = current_position_(b);
            if (!file_0(b, l + 1)) {
                break;
            }
            if (!empty_element_parsed_guard_(b, "file", c)) {
                break;
            }
        }
        return true;
    }

    // intentBlock commandDelimitter
    private static boolean file_0(PsiBuilder b, int l) {
        if (!recursion_guard_(b, l, "file_0")) {
            return false;
        }
        boolean r;
        Marker m = enter_section_(b);
        r = intentBlock(b, l + 1);
        r = r && commandDelimitter(b, l + 1);
        exit_section_(b, m, null, r);
        return r;
    }

    /* ********************************************************** */
    // intentName ":" commandDelimitter
    public static boolean intent(PsiBuilder b, int l) {
        if (!recursion_guard_(b, l, "intent")) {
            return false;
        }
        if (!nextTokenIs(b, CHARSEQUENCE)) {
            return false;
        }
        boolean r;
        Marker m = enter_section_(b);
        r = intentName(b, l + 1);
        r = r && consumeToken(b, COLON);
        r = r && commandDelimitter(b, l + 1);
        exit_section_(b, m, INTENT, r);
        return r;
    }

    /* ********************************************************** */
    // intent utterancesBlock promptBlock? suggestionLine
    public static boolean intentBlock(PsiBuilder b, int l) {
        if (!recursion_guard_(b, l, "intentBlock")) {
            return false;
        }
        if (!nextTokenIs(b, CHARSEQUENCE)) {
            return false;
        }
        boolean r;
        Marker m = enter_section_(b);
        r = intent(b, l + 1);
        r = r && utterancesBlock(b, l + 1);
        r = r && intentBlock_2(b, l + 1);
        r = r && suggestionLine(b, l + 1);
        exit_section_(b, m, INTENT_BLOCK, r);
        return r;
    }

    // promptBlock?
    private static boolean intentBlock_2(PsiBuilder b, int l) {
        if (!recursion_guard_(b, l, "intentBlock_2")) {
            return false;
        }
        promptBlock(b, l + 1);
        return true;
    }

    /* ********************************************************** */
    // (charSequence "-"?)+
    public static boolean intentName(PsiBuilder b, int l) {
        if (!recursion_guard_(b, l, "intentName")) {
            return false;
        }
        if (!nextTokenIs(b, CHARSEQUENCE)) {
            return false;
        }
        boolean r;
        Marker m = enter_section_(b);
        r = intentName_0(b, l + 1);
        while (r) {
            int c = current_position_(b);
            if (!intentName_0(b, l + 1)) {
                break;
            }
            if (!empty_element_parsed_guard_(b, "intentName", c)) {
                break;
            }
        }
        exit_section_(b, m, INTENT_NAME, r);
        return r;
    }

    // charSequence "-"?
    private static boolean intentName_0(PsiBuilder b, int l) {
        if (!recursion_guard_(b, l, "intentName_0")) {
            return false;
        }
        boolean r;
        Marker m = enter_section_(b);
        r = consumeToken(b, CHARSEQUENCE);
        r = r && intentName_0_1(b, l + 1);
        exit_section_(b, m, null, r);
        return r;
    }

    // "-"?
    private static boolean intentName_0_1(PsiBuilder b, int l) {
        if (!recursion_guard_(b, l, "intentName_0_1")) {
            return false;
        }
        consumeToken(b, BLOCK);
        return true;
    }

    /* ********************************************************** */
    // charSequence
    public static boolean output(PsiBuilder b, int l) {
        if (!recursion_guard_(b, l, "output")) {
            return false;
        }
        if (!nextTokenIs(b, CHARSEQUENCE)) {
            return false;
        }
        boolean r;
        Marker m = enter_section_(b);
        r = consumeToken(b, CHARSEQUENCE);
        exit_section_(b, m, OUTPUT, r);
        return r;
    }

    /* ********************************************************** */
    // (blockPart blockDelimitter)* blockPart
    public static boolean promptBlock(PsiBuilder b, int l) {
        if (!recursion_guard_(b, l, "promptBlock")) {
            return false;
        }
        if (!nextTokenIs(b, "<prompt block>", BLOCK, VOICE_ONLY_BLOCK)) {
            return false;
        }
        boolean r;
        Marker m = enter_section_(b, l, _NONE_, PROMPT_BLOCK, "<prompt block>");
        r = promptBlock_0(b, l + 1);
        r = r && blockPart(b, l + 1);
        exit_section_(b, l, m, r, false, null);
        return r;
    }

    // (blockPart blockDelimitter)*
    private static boolean promptBlock_0(PsiBuilder b, int l) {
        if (!recursion_guard_(b, l, "promptBlock_0")) {
            return false;
        }
        while (true) {
            int c = current_position_(b);
            if (!promptBlock_0_0(b, l + 1)) {
                break;
            }
            if (!empty_element_parsed_guard_(b, "promptBlock_0", c)) {
                break;
            }
        }
        return true;
    }

    // blockPart blockDelimitter
    private static boolean promptBlock_0_0(PsiBuilder b, int l) {
        if (!recursion_guard_(b, l, "promptBlock_0_0")) {
            return false;
        }
        boolean r;
        Marker m = enter_section_(b);
        r = blockPart(b, l + 1);
        r = r && blockDelimitter(b, l + 1);
        exit_section_(b, m, null, r);
        return r;
    }

    /* ********************************************************** */
    // charSequence
    public static boolean suggestion(PsiBuilder b, int l) {
        if (!recursion_guard_(b, l, "suggestion")) {
            return false;
        }
        if (!nextTokenIs(b, CHARSEQUENCE)) {
            return false;
        }
        boolean r;
        Marker m = enter_section_(b);
        r = consumeToken(b, CHARSEQUENCE);
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
    // ("-" ' '* output commandDelimitter)+
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

    // "-" ' '* output commandDelimitter
    private static boolean textBlock_0(PsiBuilder b, int l) {
        if (!recursion_guard_(b, l, "textBlock_0")) {
            return false;
        }
        boolean r;
        Marker m = enter_section_(b);
        r = consumeToken(b, BLOCK);
        r = r && textBlock_0_1(b, l + 1);
        r = r && output(b, l + 1);
        r = r && commandDelimitter(b, l + 1);
        exit_section_(b, m, null, r);
        return r;
    }

    // ' '*
    private static boolean textBlock_0_1(PsiBuilder b, int l) {
        if (!recursion_guard_(b, l, "textBlock_0_1")) {
            return false;
        }
        while (true) {
            int c = current_position_(b);
            if (!consumeToken(b, " ")) {
                break;
            }
            if (!empty_element_parsed_guard_(b, "textBlock_0_1", c)) {
                break;
            }
        }
        return true;
    }

    /* ********************************************************** */
    // "!" " "* utterence commandDelimitter
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
        r = r && utteranceLine_1(b, l + 1);
        r = r && utterence(b, l + 1);
        r = r && commandDelimitter(b, l + 1);
        exit_section_(b, m, UTTERANCE_LINE, r);
        return r;
    }

    // " "*
    private static boolean utteranceLine_1(PsiBuilder b, int l) {
        if (!recursion_guard_(b, l, "utteranceLine_1")) {
            return false;
        }
        while (true) {
            int c = current_position_(b);
            if (!consumeToken(b, " ")) {
                break;
            }
            if (!empty_element_parsed_guard_(b, "utteranceLine_1", c)) {
                break;
            }
        }
        return true;
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
    // charSequence
    public static boolean utterence(PsiBuilder b, int l) {
        if (!recursion_guard_(b, l, "utterence")) {
            return false;
        }
        if (!nextTokenIs(b, CHARSEQUENCE)) {
            return false;
        }
        boolean r;
        Marker m = enter_section_(b);
        r = consumeToken(b, CHARSEQUENCE);
        exit_section_(b, m, UTTERENCE, r);
        return r;
    }

    /* ********************************************************** */
    // (VOICE_ONLY_BLOCK output commandDelimitter)+
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

    // VOICE_ONLY_BLOCK output commandDelimitter
    private static boolean voiceBlock_0(PsiBuilder b, int l) {
        if (!recursion_guard_(b, l, "voiceBlock_0")) {
            return false;
        }
        boolean r;
        Marker m = enter_section_(b);
        r = consumeToken(b, VOICE_ONLY_BLOCK);
        r = r && output(b, l + 1);
        r = r && commandDelimitter(b, l + 1);
        exit_section_(b, m, null, r);
        return r;
    }

}
