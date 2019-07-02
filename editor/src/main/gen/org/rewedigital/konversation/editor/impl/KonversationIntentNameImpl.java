// This is a generated file. Not intended for manual editing.
package org.rewedigital.konversation.editor.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;

import org.jetbrains.annotations.NotNull;
import org.rewedigital.konversation.editor.psi.KonversationIntentName;
import org.rewedigital.konversation.editor.psi.KonversationVisitor;
import org.rewedigital.konversation.editor.psi.impl.KonversationNamedElementImpl;

public class KonversationIntentNameImpl extends KonversationNamedElementImpl implements KonversationIntentName {

    public KonversationIntentNameImpl(@NotNull ASTNode node) {
        super(node);
    }

    public void accept(@NotNull KonversationVisitor visitor) {
        visitor.visitIntentName(this);
    }

    public void accept(@NotNull PsiElementVisitor visitor) {
        if (visitor instanceof KonversationVisitor) {
            accept((KonversationVisitor) visitor);
        } else {
            super.accept(visitor);
        }
    }

}
