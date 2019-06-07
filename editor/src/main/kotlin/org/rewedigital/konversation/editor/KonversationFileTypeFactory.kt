package org.rewedigital.konversation.editor

import com.intellij.openapi.fileTypes.FileTypeConsumer
import com.intellij.openapi.fileTypes.FileTypeFactory

class KonversationFileTypeFactory : FileTypeFactory() {
    override fun createFileTypes(fileTypeConsumer: FileTypeConsumer) =
        fileTypeConsumer.consume(KonversationFileType.INSTANCE)
}