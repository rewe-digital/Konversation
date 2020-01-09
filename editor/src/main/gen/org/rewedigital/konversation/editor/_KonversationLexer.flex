package org.rewedigital.konversation.editor;

import com.intellij.lexer.FlexLexer;
import com.intellij.psi.tree.IElementType;

import static com.intellij.psi.TokenType.BAD_CHARACTER;
import static com.intellij.psi.TokenType.WHITE_SPACE;
import static org.rewedigital.konversation.editor.psi.KonversationTypes.*;

%%

%{
  public _KonversationLexer() {
    this((java.io.Reader)null);
  }
%}

%public
%class _KonversationLexer
%implements FlexLexer
%function advance
%type IElementType
%unicode

EOL=\R
WHITE_SPACE=\s+

STRING=[^\r\n:!+~\[\]?@/#-]+
REPROMPT_MARKER=\?[1-3]
WHITE_SPACE=[ \t\n\x0B\f\r]+

%%
<YYINITIAL> {
  {WHITE_SPACE}          { return WHITE_SPACE; }

  "@"                    { return ANNOTATION; }
  ":"                    { return COLON; }
  "!"                    { return UTTERANCE; }
  "-"                    { return BLOCK; }
  "~"                    { return VOICE_ONLY_BLOCK; }
  "+"                    { return BLOCK_CONCAT; }
  "{"                    { return LEFT_BRACE; }
  "}"                    { return RIGHT_BRACE; }
  "["                    { return SUGGESTION_START; }
  "]"                    { return SUGGESTION_END; }
  "$"                    { return VARIABLE_DOLLAR; }
  "%"                    { return VARIABLE_PERCENT; }
  "#"                    { return SHARP_COMMENT; }
  "//"                   { return SLASH_COMMENT; }

  {STRING}               { return STRING; }
  {REPROMPT_MARKER}      { return REPROMPT_MARKER; }
  {WHITE_SPACE}          { return WHITE_SPACE; }

}

[^] { return BAD_CHARACTER; }
