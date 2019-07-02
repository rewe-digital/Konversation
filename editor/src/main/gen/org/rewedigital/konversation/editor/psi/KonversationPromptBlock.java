// This is a generated file. Not intended for manual editing.
package org.rewedigital.konversation.editor.psi;

import com.intellij.psi.PsiElement;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface KonversationPromptBlock extends PsiElement {

    @NotNull
    List<KonversationBlockDelimitter> getBlockDelimitterList();

    @NotNull
    List<KonversationBlockPart> getBlockPartList();

}
