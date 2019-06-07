package org.rewedigital.konversation.editor

import com.intellij.lang.properties.charset.Native2AsciiCharset
import com.intellij.openapi.fileEditor.impl.LoadTextUtil
import com.intellij.openapi.fileTypes.LanguageFileType
import com.intellij.openapi.vfs.CharsetToolkit
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.encoding.EncodingRegistry
import org.jetbrains.annotations.NonNls

class KonversationFileType private constructor() : LanguageFileType(KonversationLanguage.INSTANCE) {

    override fun getName(): String {
        return "Konversation"
    }

    override fun getDescription() = "Konversation File"

    override fun getDefaultExtension() = DEFAULT_EXTENSION

    override fun getIcon() = KonversationLanguage.ICON

    override fun getCharset(file: VirtualFile, content: ByteArray): String? {
        val guessed = LoadTextUtil.guessFromContent(file, content)
        var charset = if (guessed.hardCodedCharset == null) EncodingRegistry.getInstance().getDefaultCharsetForPropertiesFiles(file) else guessed.hardCodedCharset
        if (charset == null) {
            charset = CharsetToolkit.getDefaultSystemCharset()
        }
        if (EncodingRegistry.getInstance().isNative2Ascii(file)) {
            charset = Native2AsciiCharset.wrap(charset)
        }
        return charset!!.name()
    }

    companion object {
        val INSTANCE: LanguageFileType = KonversationFileType()
        @NonNls val DEFAULT_EXTENSION = "kvs"
        @NonNls val DOT_DEFAULT_EXTENSION = ".$DEFAULT_EXTENSION"
    }
}
