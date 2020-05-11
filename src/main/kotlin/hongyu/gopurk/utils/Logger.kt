package hongyu.gopurk.utils

import hongyu.gopurk.plugin.Plugin

interface Logger {
    fun info(message: String): Unit

    fun error(message: String): Unit
}

class GopurKLooger : Logger {
    override fun info(message: String) {
        TODO("Not yet implemented")
    }

    override fun error(message: String) {
        TODO("Not yet implemented")
    }
}

class PluginLogger : Logger {
    private var pluginName: String

    constructor(plugin: Plugin) {
        pluginName = "[${plugin.getPluginDescription().getName()}]"
    }

    override fun info(message: String) {
        TODO("Not yet implemented")
    }

    override fun error(message: String) {
        TODO("Not yet implemented")
    }
}