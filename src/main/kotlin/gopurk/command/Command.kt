package gopurk.command

abstract class Command {
    private lateinit var name: String
    private lateinit var description: String
    private lateinit var usage: String
    private var commandMap: CommandMap? = null

    constructor(name: String, description: String, usage: String) {
        this.name = name
        this.description = description
        this.usage = usage
    }

    abstract fun execute(commandLine: CommandLine): Boolean

    fun getName(): String = name

    fun getDescription(): String = description

    fun getUsage(): String = usage

    fun setDescription(description: String): Unit {
        this.description = description
    }

    fun setUsage(usage: String): Unit {
        this.usage = usage
    }

    fun register(commandMap: CommandMap?): Boolean {
        if (commandMap == null || commandMap == this.commandMap)
            return false
        this.commandMap = commandMap
        return true
    }

    fun unregister(commandMap: CommandMap?): Boolean {
        if (commandMap == null || commandMap != this.commandMap)
            return false
        this.commandMap = commandMap
        return true
    }

    fun isRegistered(): Boolean = commandMap != null

    override fun toString(): String = name
}