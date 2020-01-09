// This is a generated file. Not intended for manual editing.
package org.rewedigital.konversation.editor.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.rewedigital.konversation.editor.psi.KonversationCommandDelimitter;
import org.rewedigital.konversation.editor.psi.KonversationComment;
import org.rewedigital.konversation.editor.psi.KonversationVisitor;

import static org.rewedigital.konversation.editor.psi.KonversationTypes.WHITE_SPACE;

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
    @Nullable
    public KonversationComment getComment() {
        return findChildByClass(KonversationComment.class);
    }

    @Override
    @NotNull
    public PsiElement getWhiteSpace() {
        return findNotNullChildByType(WHITE_SPACE);
    }
}
