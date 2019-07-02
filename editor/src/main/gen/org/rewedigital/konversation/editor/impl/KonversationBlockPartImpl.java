// This is a generated file. Not intended for manual editing.
package org.rewedigital.konversation.editor.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.rewedigital.konversation.editor.psi.KonversationBlockPart;
import org.rewedigital.konversation.editor.psi.KonversationTextBlock;
import org.rewedigital.konversation.editor.psi.KonversationVisitor;
import org.rewedigital.konversation.editor.psi.KonversationVoiceBlock;

public class KonversationBlockPartImpl extends ASTWrapperPsiElement implements KonversationBlockPart {

    public KonversationBlockPartImpl(@NotNull ASTNode node) {
        super(node);
    }

    public void accept(@NotNull KonversationVisitor visitor) {
        visitor.visitBlockPart(this);
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
    public KonversationTextBlock getTextBlock() {
        return findChildByClass(KonversationTextBlock.class);
    }

    @Override
    @Nullable
    public KonversationVoiceBlock getVoiceBlock() {
        return findChildByClass(KonversationVoiceBlock.class);
    }

}
