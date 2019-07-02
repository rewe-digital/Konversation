// This is a generated file. Not intended for manual editing.
package org.rewedigital.konversation.editor.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;

import org.jetbrains.annotations.NotNull;
import org.rewedigital.konversation.editor.psi.KonversationCommandDelimitter;
import org.rewedigital.konversation.editor.psi.KonversationComment;
import org.rewedigital.konversation.editor.psi.KonversationVisitor;

import java.util.List;

public class KonversationCommandDelimitterImpl extends ASTWrapperPsiElement implements KonversationCommandDelimitter {

    public KonversationCommandDelimitterImpl(@NotNull ASTNode node) {
        super(node);
    }

    public void accept(@NotNull KonversationVisitor visitor) {
        visitor.visitCommandDelimitter(this);
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
    public List<KonversationComment> getCommentList() {
        return PsiTreeUtil.getChildrenOfTypeAsList(this, KonversationComment.class);
    }

}
