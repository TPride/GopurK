package gopurk

import gopurk.command.SimpleCommandMap
import java.util.regex.Pattern

val gopurK: GopurK = GopurK()
fun main(args: Array<String>) {

}

class GopurK {
    val information: Information = Information()
    val commandMap: SimpleCommandMap = SimpleCommandMap()

    class Information {
        val version: String = "0.0.1"
        val authors: List<String> = listOf("TPride")
        val path: String = System.getProperty("user.dir")
    }
}

class GopurKTool {
    companion object {
        val stringSort: StringSort = StringSort()

        fun getChineseCount(string: String?): Int {
            if (string == null || string.isEmpty())
                return 0
            var count = 0
            val matcher = Pattern.compile("[\\u4e00-\\u9fa5]").matcher(string)
            while (matcher.find())
                count++
            return count
        }

        class StringSort {
            /**
             * 键与值的行列排序算法
             * @param map Map
             * @param line 排序行数
             * @param format 格式
             * @param useTab 是否\t
             */
            fun keyValueLine(map: Map<String, String>, line: Int = 2, format: String = "{key} - {value}", useTab: Boolean = true): String = keyValueLine(map.keys.toList(), map.values.toList(), line, format, useTab)
            /**
             * 键与值的行列排序算法
             * @param keys 所有键
             * @param values 所有值
             * @param line 排序行数
             * @param format 格式
             * @param useTab 是否\t
             */
            fun keyValueLine(keys: List<String>, values: List<String>, line: Int = 2, format: String = "{key} - {value}", useTab: Boolean = true): String {
                if (keys.size != values.size)
                    return ""
                if (line <= 0)
                    return ""
                var i = 0
                var k = 0
                var longest = 0
                val stringBuffer = StringBuffer()
                val result: Array<String?> = arrayOfNulls(keys.size)
                val tab: String = if (useTab) "\t" else ""
                stringBuffer.append(tab)
                keys.indices.forEach { index ->
                    val merge: String = format.replace("{key}", keys[index]).replace("{value}", values[index])
                    if (longest < merge.length && (keys.size - 1) - i != 0)
                        longest = merge.length
                    result[i] = merge
                    i++
                }
                result.indices.forEach { index ->
                    if ((index + 1) % line != 0)
                        if (result[index]?.length!! < longest) {
                            val count = getChineseCount(result[index]);
                            val reduce = longest - result[index]?.length!! - (if (count >= 5) count - 4 else 0)
                            for (n in 1..reduce)
                                result[index] += " "
                        }
                }
                result.forEach { returnString ->
                    k++
                    stringBuffer.append(returnString).append(if (k % line == 0) "\n${tab}" else (if (result.size - k != 0) tab else ""))
                }
                return stringBuffer.toString()
            }
        }
    }
}