package gopurk

lateinit var gopurK: GopurK

fun main(args: Array<String>) {
    gopurK = GopurK()
}

class GopurK {
    val information: Information = Information()

    class Information {
        val version: String = "0.0.1"
        val authors: Array<String> = arrayOf("TPride")
        val path: String = System.getProperty("user.dir")
    }
}