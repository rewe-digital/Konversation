// This is a generated file. Not intended for manual editing.
package org.rewedigital.konversation.editor.psi;

import com.intellij.psi.PsiElement;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface KonversationVoiceBlock extends PsiElement {

  @NotNull
  List<KonversationVoicePrompt> getVoicePromptList();

  @NotNull
  List<KonversationCommandDelimitter> getCommandDelimitterList();
}
