package hongyu.gopurk.plugin

import hongyu.gopurk.plugin.loaders.PluginLoader
import java.lang.reflect.Constructor
import java.util.*

open class PluginManager() {
    private val plugins: MutableMap<String, Plugin> = LinkedHashMap()
    private val fileAssociations: MutableMap<String, PluginLoader> = HashMap<String, PluginLoader>()

    fun getPlugin(name: String): Plugin? = plugins.getOrDefault(name, null)

    fun getPlugins(): Map<String, Plugin> = plugins

    fun registerInterface(loadedClass: Class<out PluginLoader?>?): Boolean {
        return if (loadedClass != null) {
            try {
                val constructor: Constructor<*> = loadedClass.getDeclaredConstructor()
                constructor.isAccessible = true
                fileAssociations[loadedClass.name] = constructor.newInstance() as PluginLoader
                true
            } catch (e: Exception) {
                false
            }
        } else false
    }


}