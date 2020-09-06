package cc.ekblad.kotline

import java.io.Closeable
import java.lang.IllegalStateException

class Kotline(private val term: Term) : Closeable {
    private var history: ShadowHistory<AnsiLine> = ShadowHistory(AnsiLine(), { AnsiLine(it.toString()) })
    private val currentLine: AnsiLine
        get() = history.current
    private var closed: Boolean = false
    private var shutdownHook = Thread { close() }

    init {
        term.init()
        Runtime.getRuntime().addShutdownHook(shutdownHook)
    }

    override fun close() {
        closed = true
        term.close()
        try {
            Runtime.getRuntime().removeShutdownHook(shutdownHook)
        } catch (e: IllegalStateException) {
            // Just ignore; we're in the middle of shutting down the VM
        }
    }

    fun readLine(prompt: String = ""): String? {
        if (closed) {
            return null
        }
        currentLine.moveCursor(prompt, 0)
        while (true) {
            when (val c = getInput(term)) {
                is Character -> currentLine.addChar(prompt, c.char)
                is Return -> return handleReturn()
                is Left -> currentLine.moveCursor(prompt, -1)
                is Right -> currentLine.moveCursor(prompt, 1)
                is Delete -> currentLine.deleteCharAtCursor(prompt, false)
                is Backspace -> currentLine.deleteCharAtCursor(prompt, true)
                is Home -> currentLine.moveToStart(prompt)
                is End -> currentLine.moveToEnd(prompt)
                is ControlLeft -> currentLine.oneWordBack(prompt)
                is ControlRight -> currentLine.oneWordForward(prompt)
                is Up -> moveHistory(prompt, -1)
                is Down -> moveHistory(prompt, 1)
                is EOF -> when {
                    currentLine.toString().isEmpty() -> return handleEof()
                    c.hard -> return handleReturn()
                }
            }
        }
    }

    private fun handleEof(): String? {
        newLine()
        history.resetShadow(AnsiLine())
        return null
    }

    private fun handleReturn(): String {
        newLine()
        return if(currentLine.toString().isBlank()) {
            history.resetShadow(AnsiLine())
        } else {
            history.commitAndResetShadow(AnsiLine())
        }.toString()
    }

    private fun moveHistory(prompt: String, offset: Int) {
        require(offset in -1 .. 1)
        val oldWidth = currentLine.lineWidth
        when (offset) {
            0 -> { }
            1 -> history.forward()
            -1 -> history.back()
        }
        currentLine.refresh(prompt, oldWidth - currentLine.lineWidth)
        currentLine.moveToEnd(prompt)
    }

    private fun newLine() {
        println()
    }
}
