// This is a generated file. Not intended for manual editing.
package org.rewedigital.konversation.editor.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.rewedigital.konversation.editor.psi.KonversationIntentBlock;
import org.rewedigital.konversation.editor.psi.KonversationIntentDeclaration;
import org.rewedigital.konversation.editor.psi.KonversationPromptBlock;
import org.rewedigital.konversation.editor.psi.KonversationRepromptBlock;
import org.rewedigital.konversation.editor.psi.KonversationSuggestionLine;
import org.rewedigital.konversation.editor.psi.KonversationUtterancesBlock;
import org.rewedigital.konversation.editor.psi.KonversationVisitor;

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
    public KonversationIntentDeclaration getIntentDeclaration() {
        return findNotNullChildByClass(KonversationIntentDeclaration.class);
    }

    @Override
    @Nullable
    public KonversationPromptBlock getPromptBlock() {
        return findChildByClass(KonversationPromptBlock.class);
    }

    @Override
    @Nullable
    public KonversationRepromptBlock getRepromptBlock() {
        return findChildByClass(KonversationRepromptBlock.class);
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
