/**
 * Terminal implementation for Linux and OSX systems.
 */
package cc.ekblad.kotline

import java.nio.charset.Charset

val nixTerm: Term by lazy { NixTerm() }

private class NixTerm : Term {
    private var ttyConfig = ""

    override fun init() {
        ttyConfig = stty(arrayOf("-g")).trim()
        stty(arrayOf("-icanon", "min", "1"))
        stty(arrayOf("-echo"))
    }

    override fun close() {
        stty(arrayOf(ttyConfig))
    }

    private fun stty(args: Array<String>): String {
        val invocation = arrayOf("/bin/stty", "-F", "/dev/tty") + args
        val p = Runtime.getRuntime().exec(invocation)
        p.waitFor()
        return p.inputStream.use {
            it.readAllBytes().toString(Charset.forName("utf-8"))
        }
    }
}
