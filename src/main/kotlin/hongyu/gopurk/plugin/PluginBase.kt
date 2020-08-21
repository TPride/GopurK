package hongyu.gopurk.plugin

import hongyu.gopurk.plugin.loaders.PluginLoader
import hongyu.gopurk.utils.PluginLogger
import java.io.File

class PluginBase : Plugin {
    private lateinit var loader: PluginLoader
    private var isEnabled: Boolean = false
    private var isInitalized: Boolean = false
    private lateinit var description: PluginDescription
    private lateinit var dataFolder: File
    private lateinit var pluginLogger: PluginLogger


    override fun onLoad() {}

    override fun onEnable() {}

    override fun onDisable() {}

    override fun isDisabled(): Boolean = !isEnabled

    override fun isEnabled(): Boolean = isEnabled

    fun setEnabled() = setEnabled(true)

    fun setEnabled(s: Boolean) {
        if (isEnabled != s) {
            isEnabled = s
            if (isEnabled)
                onEnable()
            else {
                //Gopur.getInstance().getPluginManager().disablePlugin(this)
                onDisable()
            }
        }
    }

    override fun getPluginLoader(): PluginLoader = loader

    override fun getLogger(): PluginLogger = pluginLogger

    override fun getDataFolder(): File = dataFolder

    override fun getPluginDescription(): PluginDescription = description

    fun init(loader: PluginLoader, description: PluginDescription, dataFolder: File) {
        if (!isInitalized) { //如果本插件还未初始化
            isInitalized = true
            this.loader = loader
            this.description = description
            this.dataFolder = dataFolder
            pluginLogger = PluginLogger(this)
        }
    }
}