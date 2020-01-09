// This is a generated file. Not intended for manual editing.
package org.rewedigital.konversation.editor.psi;

import com.intellij.psi.PsiElement;

import org.jetbrains.annotations.Nullable;

public interface KonversationBlockPart extends PsiElement {

    @Nullable
    KonversationCommandDelimitter getCommandDelimitter();

    @Nullable
    KonversationConcatLine getConcatLine();

    @Nullable
    KonversationConcatLineBreak getConcatLineBreak();

    @Nullable
    KonversationTextBlock getTextBlock();

    @Nullable
    KonversationVoiceBlock getVoiceBlock();
}
