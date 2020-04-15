package gopurk

//                       _oo0oo_
//                      o8888888o
//                      88" . "88
//                      (| -_- |)
//                      0\  =  /0
//                    ___/`---'\___
//                  .' \\|     |// '.
//                 / \\|||  :  |||// \
//                / _||||| -:- |||||- \
//               |   | \\\  -  /// |   |
//               | \_|  ''\---/''  |_/ |
//               \  .-\__  '-'  ___/-. /
//             ___'. .'  /--.--\  `. .'___
//          ."" '<  `.___\_<|>_/___.' >' "".
//         | | :  `- \`.;`\ _ /`;.`/ - ` : | |
//         \  \ `_.   \_ __\ /__ _/   .-` /  /
//     =====`-.____`.___ \_____/___.-`___.-'=====
//                       `=---='
//     ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
//               佛祖保佑         永无BUG

import gopurk.command.SimpleCommandMap
import java.util.regex.Pattern

val gopurK: GopurK = GopurK()
fun main(args: Array<String>) {
}

class GopurK {
    val information: Information = Information()
    private val commandMap: SimpleCommandMap = SimpleCommandMap()

    fun getCommandMap(): SimpleCommandMap = commandMap

    class Information {
        val version: String = "0.0.1"
        val authors: Array<String> = arrayOf(
            "TPride"
        )
        val path: String = System.getProperty("user.dir")
    }
}

class GopurKTool {
    companion object {
        fun getChineseCount(string: String?): Int {
            if (string == null || string.isEmpty())
                return 0
            var count = 0
            val matcher = Pattern.compile("[\\u4e00-\\u9fa5]").matcher(string)
            while (matcher.find())
                count++
            return count
        }
    }
}