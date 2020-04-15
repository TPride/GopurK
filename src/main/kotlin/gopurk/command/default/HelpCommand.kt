package gopurk.command.default

import gopurk.GopurKTool
import gopurk.command.Command
import gopurk.command.CommandLine
import gopurk.gopurK
import java.util.function.Consumer
import kotlin.collections.ArrayList

class HelpCommand() : Command("help", "GopurK帮助", "help") {
    override fun execute(commandLine: CommandLine): Boolean {
        when (commandLine.getArgs().size) {
            0 -> {

                return true
            }
        }
        return true
    }

    private fun sort() {
        var i = 0
        var k = 0
        var longest = 0
        val commands: ArrayList<Command> = ArrayList(gopurK.getCommandMap().getCommands().values)
        val result = arrayOfNulls<String>(commands.size)
        val stringBuffer = StringBuffer()
        stringBuffer.append("\n\t")
        commands.forEach(Consumer { command ->
            val s = command.getName() + " - " + command.getDescription()
            if (longest < s.length && commands.size - 1 - i != 0)
                longest = s.length
            result[i] = s
            i++
        })
        for (ii in result.indices) {
            if ((ii + 1) % 2 != 0) {
                if (result[ii]!!.length < longest) {
                    val count: Int = GopurKTool.getChineseCount(result[ii])
                    val reduce = longest - result[ii]!!.length - if (count >= 5) count - 4 else 0
                    for (j in 0..reduce)
                        result[ii] += " "
                }
            }
        }
        for (command in result) {
            k++
            stringBuffer.append(command).append(if (k % 2 == 0) "\n\t" else if (commands.size - k != 0) "\t" else "")
        }
    }
}