package cc.ekblad.kotline

interface Term {
    /**
     * Turn off terminal echo + line buffering.
     */
    fun setup()

    /**
     * Restore terminal to the mode in which it was prior to
     * setup() being called.
     */
    fun shutdown()

    /**
     * Get one byte of input from the terminal.
     */
    fun getChar(): Int
}
