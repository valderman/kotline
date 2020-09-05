/**
 * Terminal implementation for Linux and OSX systems.
 */
package cc.ekblad.kotline

import java.nio.charset.Charset

val nixTerm: Term by lazy { NixTerm() }

private class NixTerm  : Term {
    private var ttyConfig = ""

    override fun setup() {
        ttyConfig = stty(arrayOf("-g")).trim()
        stty(arrayOf("-icanon", "min", "1"))
        stty(arrayOf("-echo"))
    }

    override fun shutdown() {
        stty(arrayOf(ttyConfig))
    }

    override fun getChar() = System.`in`.read()

    private fun stty(args: Array<String>): String {
        val invocation = arrayOf("/bin/stty", "-F", "/dev/tty") + args
        val p = Runtime.getRuntime().exec(invocation)
        p.waitFor()
        return p.inputStream.use {
            it.readAllBytes().toString(Charset.forName("utf-8"))
        }
    }
}
