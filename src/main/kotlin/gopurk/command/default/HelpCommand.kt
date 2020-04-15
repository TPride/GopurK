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
}