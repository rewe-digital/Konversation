// This is a generated file. Not intended for manual editing.
package org.rewedigital.konversation.editor.psi;

import com.intellij.psi.PsiElement;
import com.intellij.util.IncorrectOperationException;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface KonversationIntentName extends KonversationNamedElement {

    @NotNull
    KonversationIntentNameChars getIntentNameChars();

    //WARNING: getKey(...) is skipped
    //matching getKey(KonversationIntentName, ...)
    //methods are not found in null

    //WARNING: getValue(...) is skipped
    //matching getValue(KonversationIntentName, ...)
    //methods are not found in null

    //WARNING: getName(...) is skipped
    //matching getName(KonversationIntentName, ...)
    //methods are not found in null

    @Nullable
    PsiElement setName(@NotNull String name) throws IncorrectOperationException;

    @Nullable
    PsiElement getNameIdentifier();
}
