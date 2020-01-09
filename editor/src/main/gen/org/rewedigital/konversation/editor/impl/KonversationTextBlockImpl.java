// This is a generated file. Not intended for manual editing.
package org.rewedigital.konversation.editor.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;

import org.jetbrains.annotations.NotNull;
import org.rewedigital.konversation.editor.psi.KonversationCommandDelimitter;
import org.rewedigital.konversation.editor.psi.KonversationTextBlock;
import org.rewedigital.konversation.editor.psi.KonversationTextPrompt;
import org.rewedigital.konversation.editor.psi.KonversationVisitor;

import java.util.List;

public class KonversationTextBlockImpl extends ASTWrapperPsiElement implements KonversationTextBlock {

    public KonversationTextBlockImpl(@NotNull ASTNode node) {
        super(node);
    }

    public void accept(@NotNull KonversationVisitor visitor) {
        visitor.visitTextBlock(this);
    }

    public void accept(@NotNull PsiElementVisitor visitor) {
        if (visitor instanceof KonversationVisitor) {
            accept((KonversationVisitor) visitor);
        } else {
            super.accept(visitor);
        }
    }

    @Override
    @NotNull
    public List<KonversationTextPrompt> getTextPromptList() {
        return PsiTreeUtil.getChildrenOfTypeAsList(this, KonversationTextPrompt.class);
    }

    @Override
    @NotNull
    public List<KonversationCommandDelimitter> getCommandDelimitterList() {
        return PsiTreeUtil.getChildrenOfTypeAsList(this, KonversationCommandDelimitter.class);
    }
}
