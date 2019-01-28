package org.rewedigital.konversation

interface LoggerFacade {
    fun log(msg: String = "")
    fun debug(msg:String = "")
    fun info(msg:String = "")
    fun error(msg:String = "")
    fun warn(msg:String = "")
}

class DefaultLogger: LoggerFacade {
    override fun log(msg: String) = println(msg)

    override fun debug(msg: String) = println(msg)

    override fun info(msg: String) = println(msg)

    override fun error(msg: String) = System.err.println(msg)

    override fun warn(msg: String) = System.err.println(msg)
}