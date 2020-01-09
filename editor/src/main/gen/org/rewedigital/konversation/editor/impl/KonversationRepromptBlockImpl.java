// This is a generated file. Not intended for manual editing.
package org.rewedigital.konversation.editor.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;

import org.jetbrains.annotations.NotNull;
import org.rewedigital.konversation.editor.psi.KonversationCommandDelimitter;
import org.rewedigital.konversation.editor.psi.KonversationRepromptBlock;
import org.rewedigital.konversation.editor.psi.KonversationRepromptLine;
import org.rewedigital.konversation.editor.psi.KonversationVisitor;

import java.util.List;

public class KonversationRepromptBlockImpl extends ASTWrapperPsiElement implements KonversationRepromptBlock {

    public KonversationRepromptBlockImpl(@NotNull ASTNode node) {
        super(node);
    }

    public void accept(@NotNull KonversationVisitor visitor) {
        visitor.visitRepromptBlock(this);
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
    public List<KonversationCommandDelimitter> getCommandDelimitterList() {
        return PsiTreeUtil.getChildrenOfTypeAsList(this, KonversationCommandDelimitter.class);
    }

    @Override
    @NotNull
    public List<KonversationRepromptLine> getRepromptLineList() {
        return PsiTreeUtil.getChildrenOfTypeAsList(this, KonversationRepromptLine.class);
    }
}
