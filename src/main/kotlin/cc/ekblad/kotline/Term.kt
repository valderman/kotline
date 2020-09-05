package cc.ekblad.kotline

import java.io.Closeable

interface Term : Closeable {
    /**
     * Turn off terminal echo + line buffering.
     */
    fun init()

    /**
     * Restore terminal to the mode in which it was prior to
     * setup() being called.
     */
    override fun close()

    /**
     * Get one byte of input from the terminal.
     */
    fun getChar(): Int
}
