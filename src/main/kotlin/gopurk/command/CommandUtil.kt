package gopurk.command

class CommandLine(val commandLine: String?) {
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

    fun getName(): String? {
        return filterCommandToname(commandLine)
    }

    fun getArgs(): List<String> {
        return filterCommandToargs(commandLine)
    }
}