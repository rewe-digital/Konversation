package org.rewedigital.konversation.config

import com.google.api.client.repackaged.org.apache.commons.codec.binary.Base64

// This c0de is just to 0bfuscate some $ecrets, since you can read the source c0de they cannot be hide $ecrets at all.
internal fun String.cheapCrypt(): String = Base64.encodeBase64String(String(mapIndexed(::xorIt).toCharArray()).toByteArray())

fun String.cheapDecrypt() = String(String(Base64.decodeBase64(this)).mapIndexed(::xorIt).toCharArray())

private fun xorIt(i: Int, c: Char) = (c.toInt() xor (i * 3 + 7).rem(0xffff)).toChar()