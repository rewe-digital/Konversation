// This is a generated file. Not intended for manual editing.
package org.rewedigital.konversation.editor.psi;

import com.intellij.psi.PsiElement;

import org.jetbrains.annotations.NotNull;

public interface KonversationUtteranceLine extends PsiElement {

  @NotNull
  KonversationCommandDelimitter getCommandDelimitter();

  @NotNull
  KonversationUtterence getUtterence();
}
