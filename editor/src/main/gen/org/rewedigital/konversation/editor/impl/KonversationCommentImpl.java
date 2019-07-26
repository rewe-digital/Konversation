// This is a generated file. Not intended for manual editing.
package org.rewedigital.konversation.editor.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;

import org.jetbrains.annotations.NotNull;
import org.rewedigital.konversation.editor.psi.KonversationComment;
import org.rewedigital.konversation.editor.psi.KonversationLine;
import org.rewedigital.konversation.editor.psi.KonversationVisitor;

public class KonversationCommentImpl extends ASTWrapperPsiElement implements KonversationComment {

    public KonversationCommentImpl(@NotNull ASTNode node) {
        super(node);
    }

    public void accept(@NotNull KonversationVisitor visitor) {
        visitor.visitComment(this);
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
    public KonversationLine getLine() {
        return findNotNullChildByClass(KonversationLine.class);
    }

}
