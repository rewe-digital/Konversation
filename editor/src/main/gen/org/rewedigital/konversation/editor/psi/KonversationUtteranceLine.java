// This is a generated file. Not intended for manual editing.
package org.rewedigital.konversation.editor.psi;

import java.util.List;

import org.jetbrains.annotations.*;

import com.intellij.psi.PsiElement;

public interface KonversationUtteranceLine extends PsiElement {

    @NotNull
    KonversationCommandDelimitter getCommandDelimitter();

    @NotNull
    KonversationUtterence getUtterence();
}
