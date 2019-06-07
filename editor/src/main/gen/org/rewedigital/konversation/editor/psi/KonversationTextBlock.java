// This is a generated file. Not intended for manual editing.
package org.rewedigital.konversation.editor.psi;

import java.util.List;

import org.jetbrains.annotations.*;

import com.intellij.psi.PsiElement;

public interface KonversationTextBlock extends PsiElement {

    @NotNull
    List<KonversationCommandDelimitter> getCommandDelimitterList();

    @NotNull
    List<KonversationOutput> getOutputList();
}
