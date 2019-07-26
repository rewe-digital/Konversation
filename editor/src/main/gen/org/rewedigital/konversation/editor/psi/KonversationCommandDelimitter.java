// This is a generated file. Not intended for manual editing.
package org.rewedigital.konversation.editor.psi;

import com.intellij.psi.PsiElement;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface KonversationCommandDelimitter extends PsiElement {

    @NotNull
    List<KonversationComment> getCommentList();

    @Nullable
    PsiElement getLineBreak();

}
