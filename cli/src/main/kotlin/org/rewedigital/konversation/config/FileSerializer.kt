package org.rewedigital.konversation.config

import kotlinx.serialization.*
import kotlinx.serialization.internal.StringDescriptor
import java.io.File

@ImplicitReflectionSerializer
@Serializer(forClass = File::class)
object FileSerializer : KSerializer<File> {
    override val descriptor: SerialDescriptor
        get() = StringDescriptor.withName("File")

    override fun serialize(encoder: Encoder, obj: File) {
        encoder.encodeString(obj.absolutePath)
    }

    override fun deserialize(decoder: Decoder): File {
        val path: String = decoder.decode()
        return File(path)
    }
}