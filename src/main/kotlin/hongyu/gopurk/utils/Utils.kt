package hongyu.gopurk.utils

import net.lingala.zip4j.core.ZipFile
import net.lingala.zip4j.exception.ZipException
import net.lingala.zip4j.model.ZipParameters
import net.lingala.zip4j.util.Zip4jConstants
import org.yaml.snakeyaml.DumperOptions
import org.yaml.snakeyaml.Yaml
import java.io.*
import java.nio.charset.StandardCharsets
import java.util.*
import java.util.function.Consumer
import kotlin.collections.LinkedHashMap

class Utils {
    companion object {
        @Throws(IOException::class)
        fun readFile(file: File): String {
            if (!file.exists() || file.isDirectory)
                throw FileNotFoundException()
            return readFile(FileInputStream(file))
        }

        @Throws(IOException::class)
        fun readFile(filename: String): String {
            val file = File(filename)
            if (!file.exists() || file.isDirectory)
                throw FileNotFoundException()
            return readFile(FileInputStream(file))
        }

        @Throws(IOException::class)
        fun readFile(inputStream: InputStream): String = readFile(InputStreamReader(inputStream, StandardCharsets.UTF_8))

        @Throws(IOException::class)
        private fun readFile(reader: Reader): String {
            BufferedReader(reader).use { br ->
                val stringBuilder = StringBuilder()
                var temp: String?
                temp = br.readLine()
                while (temp != null) {
                    if (stringBuilder.isNotEmpty())
                        stringBuilder.append("\n")
                    stringBuilder.append(temp)
                    temp = br.readLine()
                }
                return stringBuilder.toString()
            }
        }

        @Throws(IOException::class)
        fun writeFile(fileName: String, content: String): Unit = writeFile(fileName, ByteArrayInputStream(content.toByteArray(StandardCharsets.UTF_8)))

        @Throws(IOException::class)
        fun writeFile(fileName: String, content: InputStream): Unit = writeFile(File(fileName), content)

        @Throws(IOException::class)
        fun writeFile(file: File, content: String): Unit = writeFile(file, ByteArrayInputStream(content.toByteArray(StandardCharsets.UTF_8)))

        @Throws(IOException::class)
        fun writeFile(file: File, content: InputStream) {
            if (!file.exists())
                file.createNewFile()
            FileOutputStream(file).use { stream ->
                val buffer = ByteArray(1024)
                var length: Int
                while (content.read(buffer).also { length = it } != -1)
                    stream.write(buffer, 0, length)
            }
            content.close()
        }
    }
}

class Config {
    companion object {
        val YAML: Int = 0
        val format: MutableMap<String, Int> = TreeMap<String, Int>()
        init {
            format.put("yaml", YAML)
            format.put("yml", YAML)
        }
    }
    private lateinit var file: File
    private var type: Int = YAML
    private var correct: Boolean = false
    private var config: ConfigSection = ConfigSection()

    constructor() : this(YAML)
    constructor(file: String) : this(file, YAML, ConfigSection())
    constructor(file: File) : this(file.toString(), YAML, ConfigSection())
    constructor(file: String, type: Int): this(file, type, ConfigSection())
    constructor(file: File, type: Int): this(file.toString(), type, ConfigSection())
    constructor(file: File, type: Int, defaultMap: ConfigSection) : this(file.toString(), type, defaultMap)
    constructor(file: File, type: Int, defaultMap: LinkedHashMap<String, Any?>) : this(file.toString(), type, defaultMap)
    constructor(type: Int) {
        this.type = type
        this.correct = true
        this.config = ConfigSection()
    }
    constructor(file: String, type: Int, defaultMap: ConfigSection) {
        load(file, type, defaultMap)
    }
    constructor(file: String, type: Int, defaultMap: LinkedHashMap<String, Any?>) {
        load(file, type, ConfigSection(defaultMap))
    }

    /**
     * Load File
     */
    fun load(inputStream: InputStream): Boolean {
        if (correct) {
            val content: String?
            content = try {
                Utils.readFile(inputStream)
            } catch (e: IOException) {
                return false
            }
            parseContent(content)
        }
        return correct
    }
    fun load(file: String): Boolean = load(file, YAML)
    fun load(file: String, type: Int): Boolean = load(file, type, ConfigSection())
    fun load(file: String, type: Int, defaultMap: ConfigSection): Boolean {
        this.correct = true
        this.type = type
        this.file = File(file)
        if (!this.file.exists()) {
            try {
                this.file.parentFile.mkdirs()
                this.file.createNewFile()
            } catch (e: IOException) {
                //nocode
            }
            config = defaultMap
            save()
        } else {
            if (this.correct) {
                var content = ""
                try {
                    content = Utils.readFile(this.file).toString()
                } catch (e: IOException) {
                    //nocode
                }
                parseContent(content)
                if (!correct)
                    return false
                if (setDefault(defaultMap) > 0)
                    save()
            } else
                return false
        }
        return true
    }

    fun reload() {
        config.clear()
        correct = false
        load(file.toString(), type)
    }

    /**
     * Save File
     */
    fun save(): Boolean {
        return if (correct) {
            var content: String? = ""
            when (type) {
                YAML -> {
                    val dumperOptions = DumperOptions()
                    dumperOptions.defaultFlowStyle = DumperOptions.FlowStyle.BLOCK
                    val yaml = Yaml(dumperOptions)
                    content = yaml.dump(config)
                }
            }
            try {
                Utils.writeFile(file, content!!)
            } catch (e: IOException) {
                //nocode
            }
            true
        } else
            false
    }

    fun setDefault(map: java.util.LinkedHashMap<String, Any?>): Int = setDefault(ConfigSection(map))
    fun setDefault(map: ConfigSection): Int {
        val size: Int = config.size
        config = fillDefaults(map, config)
        return config.size - size
    }

    fun isCorrect(): Boolean = correct
    fun check(): Boolean = correct

    fun isSection(key: String): Boolean = config.isSection(key)
    fun isInt(key: String): Boolean = config.isInt(key)
    fun isLong(key: String): Boolean = config.isLong(key)
    fun isDouble(key: String): Boolean = config.isDouble(key)
    fun isShort(key: String): Boolean = config.isShort(key)
    fun isString(key: String): Boolean = config.isString(key)
    fun isBoolean(key: String): Boolean = config.isBoolean(key)
    fun isList(key: String): Boolean = config.isList(key)

    fun exists(key: String?): Boolean = if (key == null) false else config.exists(key)
    fun exists(key: String?, ignoreCase: Boolean): Boolean = if (key == null) false else config.exists(key, ignoreCase)

    fun remove(key: String?): Unit = config.remove(key)

    /**
     *
     * Set
     *
     */
    fun set(key: String, value: Any?): Unit = config.set(key, value)

    fun setAll(map: java.util.LinkedHashMap<String, Any?>): Unit {
        config = ConfigSection(map)
    }
    fun setAll(section: ConfigSection):Unit {
        config = section
    }

    /**
     *
     * Get
     *
     */
    fun get(key: String?): Any? = get<Any?>(key, null)
    fun <T> get(key: String?, defaultValue: T): T? = if (correct) config.get(key, defaultValue) else defaultValue

    fun getAll(): MutableMap<String, Any?> = config.getAllMap()

    fun getSection(key: String): ConfigSection = if (correct) config.getSection(key) else ConfigSection()
    fun getSections(key: String): ConfigSection = if (correct) config.getSections(key) else ConfigSection()
    fun getSections(): ConfigSection = if (correct) config.getSections() else ConfigSection()

    fun getInt(key: String): Int = getInt(key, 0)
    fun getInt(key: String, defaultValue: Int): Int = if (correct) config.getInt(key, defaultValue) else defaultValue

    fun getLong(key: String): Long = getLong(key, 0)
    fun getLong(key: String, defaultValue: Long): Long = if (correct) config.getLong(key, defaultValue) else defaultValue

    fun getDouble(key: String): Double = getDouble(key, 0.0)
    fun getDouble(key: String, defaultValue: Double): Double = if (correct) config.getDouble(key, defaultValue) else defaultValue

    fun getShort(key: String): Short = getShort(key, 0)
    fun getShort(key: String, defaultValue: Short): Short = if (correct) config.getShort(key, defaultValue) else defaultValue

    fun getString(key: String): String? = getString(key, "")
    fun getString(key: String, defaultValue: String): String? = if (correct) config.getString(key, defaultValue) else defaultValue

    fun getBoolean(key: String): Boolean = getBoolean(key, false)
    fun getBoolean(key: String, defaultValue: Boolean): Boolean = if (correct) config.getBoolean(key, defaultValue) else defaultValue

    fun getList(key: String): List<*>? = this.getList(key, null)
    fun getList(key: String, defaultList: List<*>?): List<*>? = if (correct) config.getList(key, defaultList) else defaultList
    fun getStringList(key: String): List<String> = config.getStringList(key)
    fun getIntegerList(key: String): List<Int> = config.getIntegerList(key)
    fun getBooleanList(key: String): List<Boolean> = config.getBooleanList(key)
    fun getDoubleList(key: String): List<Double> = config.getDoubleList(key)
    fun getFloatList(key: String): List<Float> = config.getFloatList(key)
    fun getLongList(key: String): List<Long> = config.getLongList(key)
    fun getShortList(key: String): List<Short> = config.getShortList(key)
    fun getByteList(key: String): List<Byte> = config.getByteList(key)
    fun getCharacterList(key: String): List<Char> = config.getCharacterList(key)
    fun getMapList(key: String): List<Map<*, *>> = config.getMapList(key)

    @Suppress("UNCHECKED_CAST")
    private fun parseContent(content: String?) {
        when (type) {
            YAML -> {
                val dumperOptions = DumperOptions()
                dumperOptions.defaultFlowStyle = DumperOptions.FlowStyle.BLOCK
                val yaml = Yaml(dumperOptions)
                config = ConfigSection(yaml.loadAs(content, LinkedHashMap::class.java) as LinkedHashMap<String, Any?>)
            }
            else -> correct = false
        }
    }

    private fun fillDefaults(defaultMap: ConfigSection, data: ConfigSection): ConfigSection {
        for (key in defaultMap.getKeys(false)) {
            if (!data.containsKey(key))
                data[key] = defaultMap[key]
        }
        return data
    }
}

class ConfigSection : LinkedHashMap<String, Any?> {
    constructor() : super()
    constructor(key: String, value : Any?) : super() {
        set(key, value)
    }

    @Suppress("UNCHECKED_CAST")
    constructor(map: Map<String, Any?>?) : super(){
        if (map == null || map.isEmpty())
            return
        for ((key, value) in map) {
            if (type<LinkedHashMap<String, Any?>>(value))
                super.put(key, ConfigSection(value as LinkedHashMap<String, Any?>))
            else if (type<List<*>>(value))
                super.put(key, parseList((value as List<*>?)!!))
            else
                super.put(key, value)
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun <T> get(key: String?, defaultValue: T): T? {
        if (key == null || key.isEmpty())
            return defaultValue
        if (super.containsKey(key))
            return super.get(key) as? T
        val keys = key.split("\\.".toRegex()).toTypedArray()
        if (!super.containsKey(keys[0]))
            return defaultValue
        val value = super.get(keys[0])
        if (value != null && value is ConfigSection)
            return value.get(keys[1], defaultValue)
        return defaultValue
    }

    fun set(key: String, value: Any?): Unit {
        val subKeys = key.split("\\.".toRegex()).toTypedArray()
        if (subKeys.size > 1) {
            var childSection: ConfigSection? = ConfigSection()
            if (containsKey(subKeys[0]) && super.get(subKeys[0]) is ConfigSection)
                childSection = super.get(subKeys[0]) as ConfigSection?
            childSection!![subKeys[1]] = value
            super.put(subKeys[0], childSection)
        } else
            super.put(subKeys[0], value)
    }

    fun remove(key: String?) {
        if (key == null || key.isEmpty())
            return
        if (super.containsKey(key))
            super.remove(key)
        else if (containsKey(".")) {
            val keys = key.split("\\.".toRegex()).toTypedArray()
            if (super.get(keys[0]) is ConfigSection) {
                val section = super.get(keys[0]) as ConfigSection?
                section!!.remove(keys[1])
            }
        }
    }

    fun getAll(): ConfigSection = ConfigSection(this)

    /*
     * Section
     */
    fun getSection(key: String): ConfigSection = get(key, ConfigSection())!!
    fun getSections(): ConfigSection = getSections(null)
    fun getSections(key: String?): ConfigSection {
        val sections = ConfigSection()
        val parent = (if (key == null || key.isEmpty()) getAll() else getSection(key)) ?: return sections
        for (e in parent.entries) {
            if (e.value is ConfigSection)
                sections.put(e.key, e.value)
        }
        return sections
    }
    fun isSection(key: String): Boolean = this[key] is ConfigSection

    /*
     * Int
     */
    fun getInt(key: String): Int = getInt(key, 0)
    fun getInt(key: String, defaultValue: Int): Int = get(key, defaultValue as Number)?.toInt() ?: 0
    fun isInt(key: String): Boolean = get(key) is Int

    /*
     *Long
     */
    fun getLong(key: String): Long = getLong(key, 0)
    fun getLong(key: String, defaultValue: Long): Long = get(key, defaultValue as Number)?.toLong() ?: 0
    fun isLong(key: String): Boolean = get(key) is Long

    /*
     * Double
     */
    fun getDouble(key: String): Double = getDouble(key, 0.0)
    fun getDouble(key: String, defaultValue: Double): Double = get(key, defaultValue as Number)?.toDouble() ?: 0.0
    fun isDouble(key: String): Boolean = get(key) is Double

    /*
     * Short
     */
    fun getShort(key: String): Short = getShort(key, 0)
    fun getShort(key: String, defaultValue: Short): Short = get(key, defaultValue as Number)?.toShort() ?: 0
    fun isShort(key: String): Boolean = get(key) is Short

    /*
     * String
     */
    fun getString(key: String): String? = getString(key, "")
    fun getString(key: String, defaultValue: String): String? = get(key, defaultValue)?.toString()
    fun isString(key: String): Boolean = get(key) is String

    /*
     * Boolean
     */
    fun getBoolean(key: String): Boolean = getBoolean(key, false)
    fun getBoolean(key: String, defaultValue: Boolean): Boolean = get(key, defaultValue) ?: false
    fun isBoolean(key: String): Boolean = get(key) is Boolean

    /*
     * List
     */
    /** List */
    fun getList(key: String): List<*>? = getList(key, null)
    fun getList(key: String, defaultList: List<*>?): List<*>? = get(key, defaultList)
    fun isList(key: String): Boolean = get(key) is List<*>
    /** StringList */
    fun getStringList(key: String): MutableList<String> {
        val value = getList(key) ?: return ArrayList(0)
        val result: MutableList<String> = ArrayList()
        for (o in value) {
            if (o is String || o is Number || o is Boolean || o is Char)
                result.add(o.toString())
        }
        return result
    }
    /** IntegerList */
    fun getIntegerList(key: String): MutableList<Int> {
        val list = getList(key) ?: return ArrayList(0)
        val result: MutableList<Int> = ArrayList()
        for (o in list) {
            if (o is Int)
                result.add(o)
            else if (o is String)
                try {
                    result.add(Integer.valueOf(o as String?))
                } catch (ex: Exception) {
                    //nocode
                }
            else if (o is Char)
                result.add((o).toInt())
            else if (o is Number)
                result.add(o.toInt())
        }
        return result
    }
    /** BooleanList */
    fun getBooleanList(key: String): MutableList<Boolean> {
        val list = getList(key) ?: return ArrayList(0)
        val result: MutableList<Boolean> = ArrayList()
        for (o in list) {
            if (o is Boolean)
                result.add(o)
            else if (o is String)
                if (java.lang.Boolean.TRUE.toString() == o)
                    result.add(true)
                else if (java.lang.Boolean.FALSE.toString() == o)
                    result.add(false)
        }
        return result
    }
    /** DoubleList */
    fun getDoubleList(key: String): MutableList<Double> {
        val list = getList(key) ?: return ArrayList(0)
        val result: MutableList<Double> = ArrayList()
        for (o in list) {
            if (o is Double)
                result.add(o)
            else if (o is String)
                try {
                    result.add(java.lang.Double.valueOf(o as String?))
                } catch (ex: Exception) {
                    //nocode
                }
            else if (o is Char)
                result.add(o.toDouble())
            else if (o is Number)
                result.add(o.toDouble())
        }
        return result
    }
    /** FloatList */
    fun getFloatList(key: String): MutableList<Float> {
        val list = getList(key) ?: return ArrayList(0)
        val result: MutableList<Float> = ArrayList()
        for (o in list) {
            if (o is Float)
                result.add(o)
            else if (o is String)
                try {
                    result.add(java.lang.Float.valueOf(o as String?))
                } catch (ex: Exception) {
                    //nocode
                }
            else if (o is Char)
                result.add((o as Char).toFloat())
            else if (o is Number)
                result.add(o.toFloat())
        }
        return result
    }
    /** LongList */
    fun getLongList(key: String): MutableList<Long> {
        val list = getList(key) ?: return ArrayList(0)
        val result: MutableList<Long> = ArrayList()
        for (o in list) {
            if (o is Long)
                result.add(o)
            else if (o is String)
                try {
                    result.add(java.lang.Long.valueOf(o as String?))
                } catch (ex: Exception) {
                    //nocode
                }
            else if (o is Char)
                result.add((o as Char).toLong())
            else if (o is Number)
                result.add(o.toLong())
        }
        return result
    }
    /** ShortList */
    fun getShortList(key: String): MutableList<Short> {
        val list = getList(key) ?: return ArrayList(0)
        val result: MutableList<Short> = ArrayList()
        for (o in list) {
            if (o is Short)
                result.add(o)
            else if (o is String)
                try {
                    result.add(o.toShort())
                } catch (ex: Exception) {
                    //ignore
                }
            else if (o is Char)
                result.add(o.toShort())
            else if (o is Number)
                result.add(o.toShort())
        }
        return result
    }
    /** ByteList */
    fun getByteList(key: String): MutableList<Byte> {
        val list = getList(key) ?: return ArrayList(0)
        val result: MutableList<Byte> = ArrayList()
        for (o in list) {
            if (o is Byte)
                result.add(o)
            else if (o is String)
                try {
                    result.add(java.lang.Byte.valueOf(o as String?))
                } catch (ex: Exception) {
                    //ignore
                }
            else if (o is Char)
                result.add(o.toByte())
            else if (o is Number)
                result.add(o.toByte())
        }
        return result
    }
    /** CharacterList */
    fun getCharacterList(key: String): MutableList<Char> {
        val list = getList(key) ?: return ArrayList(0)
        val result: MutableList<Char> = ArrayList()
        for (o in list) {
            if (o is Char)
                result.add(o)
            else if (o is String) {
                val str = o
                if (str.length == 1)
                    result.add(str[0])
            } else if (o is Number)
                result.add(o.toInt().toChar())
        }
        return result
    }
    /** MapList */
    @Suppress("UNCHECKED_CAST")
    fun getMapList(key: String): MutableList<Map<*, *>> {
        val result: MutableList<Map<*, *>> = ArrayList()
        val list: List<Map<*, *>> = getList(key) as List<Map<*, *>>
        for (o in list)
            result.add(o)
        return result
    }

    fun getAllMap(): MutableMap<String, Any?> {
        val map = java.util.LinkedHashMap<String, Any?>()
        map.putAll(this)
        return map
    }

    fun exists(key: String): Boolean = exists(key, false)
    fun exists(key1: String, ignoreCase: Boolean): Boolean {
        var key = key1
        if (ignoreCase)
            key = key.toLowerCase()
        for (existKey in getKeys(true)) {
            var r = existKey
            if (ignoreCase)
                r = r.toLowerCase()
            if (r == key)
                return true
        }
        return false
    }

    fun keys(): Set<String> = getKeys(true)
    fun getKeys(child: Boolean): Set<String> {
        val keys: MutableSet<String> = LinkedHashSet()
        for (i in entries) {
            keys.add(i.key)
            if (i.value is ConfigSection)
                if (child)
                    (i.value as ConfigSection).getKeys(true).forEach(Consumer { keys.add(i.key + "." + it) })
        }
        return keys
    }

    inline fun <reified T> type(value: Any?): Boolean = value is T

    @Suppress("UNCHECKED_CAST")
    private fun parseList(list: List<*>): List<*>? {
        val newList: MutableList<Any?> = ArrayList()
        for (o in list)
            newList.add(if (o is LinkedHashMap<*, *>) ConfigSection(o as Map<String, Any?>) else o)
        return newList
    }
}

class Zip {
    companion object {
        fun isZip(file: File): Boolean {
            if (!file.exists() || file.isDirectory || file.isFile && !file.name.contains(".")) return false
            return if (file.name.contains(".")) file.name
                .substring(file.name.lastIndexOf(".") + 1, file.name.length) == "zip" else false
        }

        fun isValidZip(file: File?): Boolean {
            return try {
                ZipFile(file).isValidZipFile
            } catch (e: ZipException) {
                false
            }
        }

        fun isEncrypted(filePath: String?): Boolean {
            return try {
                val file = File(filePath!!)
                if (!file.exists() || file.isDirectory) return false
                val zipFile = ZipFile(file)
                if (zipFile.isValidZipFile) zipFile.isEncrypted else false
            } catch (e: ZipException) {
                false
            }
        }

        fun enzip(srcFile: String?, dest: String, password: String?): Boolean {
            return try {
                val srcfile = File(srcFile!!)
                val destname = destFileName(srcfile, dest)
                val par = ZipParameters()
                par.compressionMethod = Zip4jConstants.COMP_DEFLATE
                par.compressionLevel = Zip4jConstants.DEFLATE_LEVEL_NORMAL
                val zipfile = ZipFile(destname)
                zipfile.setFileNameCharset("UTF-8")
                if (password != null) {
                    par.isEncryptFiles = true
                    par.encryptionMethod = Zip4jConstants.ENC_METHOD_STANDARD
                    par.setPassword(password)
                }
                if (srcfile.isDirectory) zipfile.addFolder(srcfile, par) else zipfile.addFile(srcfile, par)
                true
            } catch (e: ZipException) {
                false
            }
        }

        fun unzip(zipFile: String?, destino: String?, password: String?): Boolean {
            return try {
                val file = File(zipFile!!)
                if (!file.exists() || file.isDirectory)
                    return false
                val zFile = ZipFile(zipFile)
                zFile.setFileNameCharset("UTF-8")
                if (!zFile.isValidZipFile)
                    return false
                if (File(destino!!).isFile)
                    return false
                if (zFile.isEncrypted) {
                    if (password == null)
                        return false
                    zFile.setPassword(password)
                }
                zFile.extractAll(destino)
                true
            } catch (e: ZipException) {
                false
            }
        }

        private fun destFileName(srcFile: File, destino1: String?): String {
            var destino: String? = destino1
            if (destino == null) {
                destino = if (!srcFile.isDirectory) {
                    val filename = srcFile.name.substring(0, srcFile.name.lastIndexOf("."))
                    "${srcFile.parent}${File.separator}$filename.zip"
                } else
                    "${srcFile.parent}${File.separator}${srcFile.name}.zip"
            } else {
                paths(destino)
                if (destino.endsWith(File.separator)) {
                    val filename: String = if (srcFile.isDirectory) srcFile.name else srcFile.name.substring(0, srcFile.name.lastIndexOf("."))
                    destino += "$filename.zip"
                }
            }
            return destino
        }

        private fun paths(dest: String) {
            val destDir: File = if (dest.endsWith(File.separator)) File(dest) else File(dest.substring(0, dest.lastIndexOf(File.separator)))
            if (!destDir.exists())
                destDir.mkdirs()
        }
    }
}