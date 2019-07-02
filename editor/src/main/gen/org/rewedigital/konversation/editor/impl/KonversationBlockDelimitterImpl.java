// This is a generated file. Not intended for manual editing.
package org.rewedigital.konversation.editor.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;

import org.jetbrains.annotations.NotNull;
import org.rewedigital.konversation.editor.psi.KonversationBlockDelimitter;
import org.rewedigital.konversation.editor.psi.KonversationCommandDelimitter;
import org.rewedigital.konversation.editor.psi.KonversationVisitor;

public class KonversationBlockDelimitterImpl extends ASTWrapperPsiElement implements KonversationBlockDelimitter {

    public KonversationBlockDelimitterImpl(@NotNull ASTNode node) {
        super(node);
    }

    public void accept(@NotNull KonversationVisitor visitor) {
        visitor.visitBlockDelimitter(this);
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
    public KonversationCommandDelimitter getCommandDelimitter() {
        return findNotNullChildByClass(KonversationCommandDelimitter.class);
    }

}
