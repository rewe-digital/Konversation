// This is a generated file. Not intended for manual editing.
package org.rewedigital.konversation.editor.impl;

import java.util.List;

import org.jetbrains.annotations.*;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;

import static org.rewedigital.konversation.editor.psi.KonversationTypes.*;

import com.intellij.extapi.psi.ASTWrapperPsiElement;

import org.rewedigital.konversation.editor.psi.*;

public class KonversationIntentBlockImpl extends ASTWrapperPsiElement implements KonversationIntentBlock {

    public KonversationIntentBlockImpl(@NotNull ASTNode node) {
        super(node);
    }

    public void accept(@NotNull KonversationVisitor visitor) {
        visitor.visitIntentBlock(this);
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
    public KonversationIntent getIntent() {
        return findNotNullChildByClass(KonversationIntent.class);
    }

    @Override
    @Nullable
    public KonversationPromptBlock getPromptBlock() {
        return findChildByClass(KonversationPromptBlock.class);
    }

    @Override
    @NotNull
    public KonversationSuggestionLine getSuggestionLine() {
        return findNotNullChildByClass(KonversationSuggestionLine.class);
    }

    @Override
    @NotNull
    public KonversationUtterancesBlock getUtterancesBlock() {
        return findNotNullChildByClass(KonversationUtterancesBlock.class);
    }
}
