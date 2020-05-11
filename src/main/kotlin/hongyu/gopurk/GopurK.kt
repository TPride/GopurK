package hongyu.gopurk

import hongyu.gopurk.command.SimpleCommandMap
import hongyu.gopurk.utils.GopurKLooger

val gopurK: GopurK = GopurK()
fun main(args: Array<String>) {

}

class GopurK {
    val information: Information = Information()
    val commandMap: SimpleCommandMap = SimpleCommandMap()
    val logger: GopurKLooger = GopurKLooger()

    class Information {
        val version: String = "0.0.1"
        val authors: List<String> = listOf("TPride")
        val path: String = System.getProperty("user.dir")
    }
}