package hongyu.gopurk.plugin.loaders

import hongyu.gopurk.plugin.Plugin
import hongyu.gopurk.plugin.PluginDescription
import java.io.File
import java.util.regex.Pattern

interface PluginLoader {
    @Throws(Exception::class)
    fun loadPlugin(filename: String): Plugin?

    @Throws(Exception::class)
    fun loadPlugin(file: File): Plugin?

    fun getPluginDescription(jarpath: String): PluginDescription?

    fun getPluginDescription(file: File): PluginDescription?

    fun getFilters(): Array<Pattern>

    fun enablePlugin(plugin: Plugin)

    fun disablePlugin(plugin: Plugin)
}