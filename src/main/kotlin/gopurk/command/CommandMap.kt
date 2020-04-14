package gopurk.command

interface CommandMap {
    fun register(command: Command): Boolean
    fun unregister(commandName: String): Boolean
    fun unregister(command: Command): Boolean
    fun dispatch(commandLine: String): Boolean
    fun dispatch(commandLine: CommandLine): Boolean
    fun getCommand(commandName: String): Command?
    fun clearCommands()
}