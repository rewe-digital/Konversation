package org.rewedigital.konversation

/**
 * The type of a part of the prompt. All parts will be concatenated by the runtime. For the display test the voice only parts will be stripped out.
 * This is very helpful if you need to describe some visual parts as lists which would be redundant on display devices.
 * Please note that the Text parts will be also added to the voice output. If you really need TextOnly please add a bug report. With an example why you need that.
 */
enum class PartType {
    /** A Text block which should be spoken and displayed. */
    Text,
    /** A voice only block which should be just spoken and never displayed. */
    VoiceOnly
}