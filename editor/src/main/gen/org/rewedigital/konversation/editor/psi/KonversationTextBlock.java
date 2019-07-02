// This is a generated file. Not intended for manual editing.
package org.rewedigital.konversation.editor.psi;

import com.intellij.psi.PsiElement;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface KonversationTextBlock extends PsiElement {

    @NotNull
    List<KonversationCommandDelimitter> getCommandDelimitterList();

    @NotNull
    List<KonversationOutput> getOutputList();

}
