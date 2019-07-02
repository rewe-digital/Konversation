// This is a generated file. Not intended for manual editing.
package org.rewedigital.konversation.editor.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface KonversationPromptBlock extends PsiElement {

    @NotNull
    List<KonversationBlockDelimitter> getBlockDelimitterList();

    @NotNull
    List<KonversationBlockPart> getBlockPartList();

}
