package cc.ekblad.kotline

import java.io.Closeable

interface Term : Closeable {
    /**
     * Turn off terminal echo and enable raw terminal input.
     */
    fun init()

    /**
     * Restore terminal to the mode in which it was prior to
     * init() being called.
     */
    override fun close()

    /**
     * Get one byte of input from the terminal.
     */
    fun getChar() = System.`in`.read()

    /**
     * Output the given text at the current cursor position.
     * May include ANSI control codes.
     */
    fun print(s: String) { kotlin.io.print(s) }

    /**
     * Ensure any buffered output is written to terminal.
     */
    fun flush() { System.out.flush() }

    /**
     * Move the cursor down to the next line.
     */
    fun newLine() {
        print("\n")
        flush()
    }
}
