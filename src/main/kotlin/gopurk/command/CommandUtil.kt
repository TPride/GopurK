package gopurk.command

import java.lang.StringBuilder

class CommandLine(private var commandLine: String) {
    companion object {
        fun filterCommandToname(commandLine: String?): String? {
            if (commandLine == null || commandLine.isEmpty())
                return null
            val commandLine1 = commandLine.trim()
            if (commandLine1.contains(" "))
                return commandLine1.substring(0, commandLine1.indexOf(" "))
            return commandLine1
        }

        fun filterCommandToargs(commandLine: String?): List<String> {
            if (commandLine == null || commandLine.isEmpty())
                return listOf()
            val commandLine1 = commandLine.trim()
            if (commandLine1.contains(" "))
                return commandLine1.substring(commandLine1.indexOf(" ") + 1, commandLine1.length).split(" ")
            return listOf()
        }
    }

    fun getName(): String? = filterCommandToname(commandLine)

    fun getArgs(): List<String> = filterCommandToargs(commandLine)

    fun getCommandLine(): String = commandLine

    fun parseCommandLine(): String = toString()

    fun toCommandLine(): CommandLine = CommandLine(parseCommandLine())

    override fun toString(): String {
        val name = getName() ?: return ""
        val s: StringBuilder = StringBuilder()
        s.append(name)
        val args: List<String> = getArgs()
        for (ss in args.indices) {
            s.append(args[ss])
            if (ss + 1 < args.size)
                s.append(" ")
        }
        return s.toString()
    }
}