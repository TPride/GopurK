package hongyu.gopurk.utils

internal class PluginException : Exception {
    constructor(msg: String?) : super(msg)
    constructor(msg: String?, cause: Throwable?) : super(msg, cause)
}