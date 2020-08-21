package hongyu.gopurk.plugin.loaders

import hongyu.gopurk.plugin.Plugin
import hongyu.gopurk.plugin.PluginBase
import hongyu.gopurk.plugin.PluginDescription
import hongyu.gopurk.utils.PluginException
import hongyu.gopurk.utils.Utils
import java.io.File
import java.io.IOException
import java.util.jar.JarEntry
import java.util.jar.JarFile
import java.util.regex.Pattern

class JarLoader() : PluginLoader {
    /**
     * JarLoader 插件加载器
     * 通过PluginDescription来加载对应的插件, 而PluginDescription是与对应插件的plugin.yml并存的, 是一种共生的关系
     */

    private val classes: MutableMap<String, Class<*>> = HashMap()
    private val classLoaders: MutableMap<String, ClassLoader> = HashMap()

    override fun enablePlugin(plugin: Plugin) {
        if (plugin is PluginBase && !plugin.isEnabled())
            plugin.setEnabled(true)
    }

    override fun disablePlugin(plugin: Plugin) {
        if (plugin is PluginBase && plugin.isEnabled()) {
            plugin.setEnabled(false)
            removeClass(plugin.getPluginDescription().getName())
        }
    }

    private fun initPlugin(plugin: PluginBase, description: PluginDescription, dataFolder: File) {
        plugin.init(this, description, dataFolder)
        plugin.onLoad()
    }

    override fun loadPlugin(filename: String): Plugin? = loadPlugin(File(filename))

    override fun loadPlugin(file: File): Plugin? {
        val description: PluginDescription? = getPluginDescription(file)
        if (description != null) {
            val dataFolder: File = File(file.parentFile, description.getName())
            if (dataFolder.exists() && !dataFolder.isDirectory)
                throw IllegalStateException(description.getName() + "的数据文件存在, 但不是一个目录")
            val main: String = description.getMain()
            val classLoader: ClassLoader = hongyu.gopurk.plugin.loaders.ClassLoader(this, javaClass.classLoader, file)
            classLoaders[description.getName()] = classLoader
            var pluginBase: PluginBase
            try {
                val jclass: Class<*> = classLoader.findClass(main)
                if (!PluginBase::class.java.isAssignableFrom(javaClass))
                    throw PluginException(description.getMain() + "不是一个插件(have no extends PluginBase)")
                try {
                    val pluginBaseClass: Class<PluginBase> = jclass.asSubclass(PluginBase::class.java) as Class<PluginBase>
                    pluginBase = pluginBaseClass.newInstance()
                    initPlugin(pluginBase, description, dataFolder)
                    return pluginBase
                } catch (e1: ClassCastException) {
                    throw PluginException("初始插件主类" + description.getMain() + "时出错")
                }
            } catch (e: ClassNotFoundException) {
                throw PluginException("无法加载插件, 无法找到" + description.getMain())
            }
        }
        return null
    }

    override fun getPluginDescription(file: File): PluginDescription? {
        try {
            JarFile(file).use { jar ->
                val entry: JarEntry = jar.getJarEntry("plugin.yml")
                jar.getInputStream(entry).use { stream -> return PluginDescription(Utils.readFile(stream)) }
            }
        } catch (e: IOException) {
            return null
        }
    }

    override fun getPluginDescription(jarpath: String): PluginDescription? = getPluginDescription(File(jarpath))

    override fun getFilters(): Array<Pattern> = arrayOf(Pattern.compile("^.+\\.jar$"))

    fun getClassByName(name: String): Class<*>? {
        var fclass: Class<*>? = classes[name]
        if (fclass == null)
            return fclass
        else {
            classLoaders.values.forEach { loader ->
                try {
                    fclass = loader.findClass(name, false)
                } catch (e: ClassNotFoundException) {
                    //nocode
                }
                if (fclass != null)
                    return fclass
            }
        }
        return null
    }

    fun setClass(name: String, clazz: Class<*>) {
        if (!classes.containsKey(name))
            classes[name] = clazz
    }

    private fun removeClass(name: String) {
        classes.remove(name)
    }
}