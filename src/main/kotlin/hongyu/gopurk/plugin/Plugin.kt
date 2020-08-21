package hongyu.gopurk.plugin

import hongyu.gopurk.plugin.loaders.PluginLoader
import hongyu.gopurk.utils.PluginLogger
import java.io.File

interface Plugin {
    fun onLoad()

    fun onEnable()

    fun onDisable()

    fun isDisabled(): Boolean

    fun isEnabled(): Boolean

    fun getDataFolder(): File

    fun getLogger(): PluginLogger

    fun getPluginLoader(): PluginLoader

    fun getPluginDescription(): PluginDescription
}