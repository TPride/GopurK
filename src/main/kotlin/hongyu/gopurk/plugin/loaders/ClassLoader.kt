package hongyu.gopurk.plugin.loaders

import java.io.File
import java.net.URLClassLoader

open class ClassLoader @Throws(Exception::class) constructor(private var loader: JarLoader, classLoader: java.lang.ClassLoader, file: File): URLClassLoader(arrayOf(file.toURI().toURL()), classLoader) {
    /**
     * ClassLoader的作用是存储插件的字节码
     */


    private val classes: MutableMap<String, Class<*>> = HashMap()

    @Throws(ClassNotFoundException::class)
    public fun findClass(name: String, check: Boolean): Class<*> {
        if (name.startsWith("hongyu.gopurk."))
            throw ClassNotFoundException(name)
        var result : Class<*>? = classes[name] ?: throw ClassNotFoundException(name)
        if (check)
            result = loader.getClassByName(name)
        if (result == null) {
            result = super.findClass(name)
            if (result != null)
                loader.setClass(name, result)
        }
        classes[name] = result!!
        return result
    }

    @Throws(ClassNotFoundException::class)
    public override fun findClass(name: String): Class<*> = findClass(name, true)

    fun getClasses(): Set<String> = classes.keys
}