package cc.ekblad.kotline

import java.io.Closeable
import java.util.Locale

class Kotline(internal val term: Term) : Closeable {
    private var history: ShadowHistory<AnsiLine> = ShadowHistory(AnsiLine(term), { AnsiLine(term, it.toString()) })
    private val currentLine: AnsiLine
        get() = history.current
    private var closed: Boolean = false
    private var shutdownHook = Thread { close(true) }

    init {
        term.init()
        Runtime.getRuntime().addShutdownHook(shutdownHook)
    }

    override fun close() {
        close(false)
    }

    private fun close(calledFromShutdownHook: Boolean) {
        closed = true
        term.close()
        if (!calledFromShutdownHook) {
            Runtime.getRuntime().removeShutdownHook(shutdownHook)
        }
    }

    fun readLine(prompt: String = ""): String? {
        if (closed) {
            return null
        }
        currentLine.moveCursor(prompt, 0)
        while (true) {
            when (val c = getInput(term)) {
                is Input.Character -> currentLine.addChar(prompt, c.char)
                is Input.Return -> return handleReturn()
                is Input.Left -> currentLine.moveCursor(prompt, -1)
                is Input.Right -> currentLine.moveCursor(prompt, 1)
                is Input.Delete -> currentLine.deleteCharAtCursor(prompt, false)
                is Input.Backspace -> currentLine.deleteCharAtCursor(prompt, true)
                is Input.Home -> currentLine.moveToStart(prompt)
                is Input.End -> currentLine.moveToEnd(prompt)
                is Input.ControlLeft -> currentLine.oneWordBack(prompt)
                is Input.ControlRight -> currentLine.oneWordForward(prompt)
                is Input.Up -> moveHistory(prompt, -1)
                is Input.Down -> moveHistory(prompt, 1)
                is Input.EOF -> when {
                    currentLine.toString().isEmpty() -> return handleEof()
                    c.hard -> return handleReturn()
                }
                null -> { /* no-op */ }
            }
        }
    }

    private fun handleEof(): String? {
        term.newLine()
        history.resetShadow(AnsiLine(term))
        return null
    }

    private fun handleReturn(): String {
        term.newLine()
        return if (currentLine.toString().isBlank()) {
            history.resetShadow(AnsiLine(term))
        } else {
            history.commitAndResetShadow(AnsiLine(term))
        }.toString()
    }

    private fun moveHistory(prompt: String, offset: Int) {
        require(offset in -1..1)
        val oldWidth = currentLine.lineWidth
        when (offset) {
            0 -> { }
            1 -> history.forward()
            -1 -> history.back()
        }
        currentLine.refresh(prompt, oldWidth - currentLine.lineWidth)
        currentLine.moveToEnd(prompt)
    }
}

fun <T> kotline(action: Kotline.() -> T): T =
    Kotline(autodetectTerminal()).use { it.action() }

private fun autodetectTerminal(): Term {
    val os = System.getProperty("os.name").lowercase(Locale.getDefault())
    return when {
        os.contains("linux") -> nixTerm
        os.contains("win") -> winTerm
        os.contains("mac") -> nixTerm
        os.contains("nix|aix|sunos") -> nixTerm
        else -> error("Unsupported OS: $os")
    }
}
