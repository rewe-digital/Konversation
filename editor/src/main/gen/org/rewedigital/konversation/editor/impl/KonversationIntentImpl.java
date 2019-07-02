// This is a generated file. Not intended for manual editing.
package org.rewedigital.konversation.editor.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;

import org.jetbrains.annotations.NotNull;
import org.rewedigital.konversation.editor.psi.KonversationCommandDelimitter;
import org.rewedigital.konversation.editor.psi.KonversationIntent;
import org.rewedigital.konversation.editor.psi.KonversationIntentName;
import org.rewedigital.konversation.editor.psi.KonversationVisitor;

public class KonversationIntentImpl extends ASTWrapperPsiElement implements KonversationIntent {

    public KonversationIntentImpl(@NotNull ASTNode node) {
        super(node);
    }

    public void accept(@NotNull KonversationVisitor visitor) {
        visitor.visitIntent(this);
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

    @Override
    @NotNull
    public KonversationIntentName getIntentName() {
        return findNotNullChildByClass(KonversationIntentName.class);
    }

}
