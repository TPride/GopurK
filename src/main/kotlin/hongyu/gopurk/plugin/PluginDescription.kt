package hongyu.gopurk.plugin

import hongyu.gopurk.utils.PluginException
import org.yaml.snakeyaml.DumperOptions
import org.yaml.snakeyaml.Yaml
import java.util.*

class PluginDescription {
    /**
     * name: xxxx
     * main: xxx.xx.x
     * version: "0.0.1"
     * description: "描述"
     * load: POSTWORLD
     * author: TPride
     */
    private lateinit var name: String
    private lateinit var main: String
    private lateinit var version: String
    private lateinit var authors: MutableList<String>
    private var description: String = ""
    private var load: PluginLoadOrder = PluginLoadOrder.POSTWORLD

    constructor(yamlMap: Map<String, Any?>) {
        load(yamlMap)
    }

    constructor(yamlString: String) {
        val dumperOptions = DumperOptions()
        dumperOptions.defaultFlowStyle = DumperOptions.FlowStyle.BLOCK
        val yaml = Yaml(dumperOptions)
        this.load(yaml.loadAs(yamlString, LinkedHashMap::class.java) as Map<String, Any?>)
    }

    private fun load(pluginYamlMap: Map<String, Any?>) {
        name = (pluginYamlMap[PluginBaseOptions.NAME] as String).replace("[^A-Za-z0-9 _.-]".toRegex(), "")
        name = if (name.isEmpty()) return else name
        version = pluginYamlMap[PluginBaseOptions.VERSION] as String
        main = pluginYamlMap[PluginBaseOptions.MAIN] as String
        if (main.startsWith("hongyu.gopurk."))
            throw Exception("$name 的主文件路径无效: $main")
        if (pluginYamlMap.containsKey(PluginBaseOptions.DESCRIPTION))
            description = pluginYamlMap[PluginBaseOptions.DESCRIPTION] as String
        if (pluginYamlMap.containsKey(PluginBaseOptions.AUTHORS))
            authors.addAll(pluginYamlMap[PluginBaseOptions.AUTHORS] as Collection<String>)
        if (pluginYamlMap.containsKey(PluginBaseOptions.AUTHOR))
            authors.add(pluginYamlMap[PluginBaseOptions.AUTHOR] as String)
        if (pluginYamlMap.containsKey(PluginBaseOptions.LOAD))
            try {
                load = PluginLoadOrder.valueOf(pluginYamlMap[PluginBaseOptions.LOAD] as String)
            } catch (e: Exception) {
                throw PluginException("无法识别插件的加载方式")
            }
    }

    fun getName() = name

    fun getVersion() = version

    fun getMain() = main

    fun getDescription() = description

    fun getAuthors() = authors

    fun getLoad() = load

    fun getFullName() = "$name v$version"
}