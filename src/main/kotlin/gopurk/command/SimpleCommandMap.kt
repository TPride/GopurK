package gopurk.command

import gopurk.command.default.HelpCommand
import java.lang.Exception
import java.util.function.Predicate

class SimpleCommandMap() : CommandMap {
    private val commands: MutableMap<String, Command> = LinkedHashMap()

    init {
        defaultCommands()
    }

    private fun defaultCommands() {
        //register(HelpCommand())
    }

    override fun register(command: Command): Boolean {
        if (command.isRegistered())
            return false
        command.register(this)
        commands[command.getName()] = command
        return true
    }

    override fun unregister(commandName: String): Boolean {
        if (!commands.containsKey(commandName))
            return false
        commands.remove(commandName)
        return true
    }

    override fun unregister(command: Command): Boolean {
        if (!commands.containsKey(command.getName()))
            return false
        commands.values.removeIf(Predicate { command1: Command -> command1.equals(command) })
        return true
    }

    override fun getCommand(commandName: String): Command? = commands.getOrDefault(commandName, null)

    override fun dispatch(commandLine: String): Boolean = dispatch(CommandLine(commandLine))

    override fun dispatch(commandLine: CommandLine): Boolean {
        getCommand(commandLine.getName()!!)!!.execute(commandLine)
        return true
    }

    override fun clearCommands() {
        for (command in commands.values)
            command.unregister(this)
        commands.clear()
        defaultCommands()
    }

    fun getCommands(): Map<String, Command> = commands
}