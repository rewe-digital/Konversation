// This is a generated file. Not intended for manual editing.
package org.rewedigital.konversation.editor.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;

import org.jetbrains.annotations.NotNull;
import org.rewedigital.konversation.editor.psi.KonversationUtterence;
import org.rewedigital.konversation.editor.psi.KonversationVisitor;

import static org.rewedigital.konversation.editor.psi.KonversationTypes.CHARSEQUENCE;

public class KonversationUtterenceImpl extends ASTWrapperPsiElement implements KonversationUtterence {

    public KonversationUtterenceImpl(@NotNull ASTNode node) {
        super(node);
    }

    public void accept(@NotNull KonversationVisitor visitor) {
        visitor.visitUtterence(this);
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
    public PsiElement getCharSequence() {
        return findNotNullChildByType(CHARSEQUENCE);
    }

}
