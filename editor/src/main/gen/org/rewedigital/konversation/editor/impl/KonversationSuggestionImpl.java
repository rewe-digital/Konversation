// This is a generated file. Not intended for manual editing.
package org.rewedigital.konversation.editor.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;

import org.jetbrains.annotations.NotNull;
import org.rewedigital.konversation.editor.psi.KonversationLine;
import org.rewedigital.konversation.editor.psi.KonversationSuggestion;
import org.rewedigital.konversation.editor.psi.KonversationVisitor;

public class KonversationSuggestionImpl extends ASTWrapperPsiElement implements KonversationSuggestion {

    public KonversationSuggestionImpl(@NotNull ASTNode node) {
        super(node);
    }

    public void accept(@NotNull KonversationVisitor visitor) {
        visitor.visitSuggestion(this);
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
