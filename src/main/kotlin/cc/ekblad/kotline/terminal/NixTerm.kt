/**
 * Terminal implementation for Linux and OSX systems.
 */
package cc.ekblad.kotline.terminal

import java.io.InputStreamReader

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
        return p.inputStream.use { stream ->
            InputStreamReader(stream).use { reader ->
                reader.readText()
            }
        }
    }
}
