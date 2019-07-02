// This is a generated file. Not intended for manual editing.
package org.rewedigital.konversation.editor.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;

import org.jetbrains.annotations.NotNull;
import org.rewedigital.konversation.editor.psi.KonversationSuggestionBlock;
import org.rewedigital.konversation.editor.psi.KonversationSuggestionLine;
import org.rewedigital.konversation.editor.psi.KonversationVisitor;

import java.util.List;

public class KonversationSuggestionLineImpl extends ASTWrapperPsiElement implements KonversationSuggestionLine {

    public KonversationSuggestionLineImpl(@NotNull ASTNode node) {
        super(node);
    }

    public void accept(@NotNull KonversationVisitor visitor) {
        visitor.visitSuggestionLine(this);
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
    public List<KonversationSuggestionBlock> getSuggestionBlockList() {
        return PsiTreeUtil.getChildrenOfTypeAsList(this, KonversationSuggestionBlock.class);
    }

}
