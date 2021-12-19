package cc.ekblad.kotline

import kotlin.math.max
import kotlin.math.min

internal class AnsiLine(private val term: Term, initialValue: String = "") {
    private var charIndex: Int = 0
    private val cursorPosition: Int
        get() = line.take(charIndex).sumOf(::charWidth)
    private var line: String = initialValue
    private val lineChars: Int
        get() = line.length
    val lineWidth: Int
        get() = line.sumOf(::charWidth)

    override fun toString(): String = line
    fun set(newLine: String) {
        line = newLine
    }

    fun moveCursor(prompt: String, offset: Int) {
        when {
            offset > 0 -> charIndex = min(charIndex + offset, lineChars)
            offset < 0 -> charIndex = max(charIndex + offset, 0)
        }
        term.print("\r$prompt")
        if (cursorPosition > 0) {
            term.print("\u001b[${cursorPosition}C")
        }
        term.flush()
    }

    fun moveToStart(prompt: String) {
        moveCursor(prompt, -lineChars)
    }

    fun moveToEnd(prompt: String) {
        moveCursor(prompt, lineChars)
    }

    fun addChar(prompt: String, char: Char) {
        var before = line
        var after = ""
        if (charIndex < lineChars) {
            before = line.substring(0, charIndex)
            after = line.substring(charIndex)
        }
        line = before + char + after
        moveCursor(prompt, 1)
        refresh(prompt, 2)
    }

    fun oneWordBack(prompt: String) {
        val prefix = line.take(charIndex)
        val blank = prefix.takeLastWhile { it.isWhitespace() }.length
        val nonblank = prefix.trimEnd().takeLastWhile { !it.isWhitespace() }.length
        val offset = blank + nonblank
        moveCursor(prompt, -offset)
    }

    fun oneWordForward(prompt: String) {
        val prefix = line.drop(charIndex)
        val blank = prefix.takeWhile { it.isWhitespace() }.length
        val nonblank = prefix.trimStart().takeWhile { !it.isWhitespace() }.length
        val offset = blank + nonblank
        moveCursor(prompt, offset)
    }

    fun deleteCharAtCursor(prompt: String, before: Boolean) {
        if (!line.isEmpty()) {
            if (before) {
                moveCursor(prompt, -1)
            }
            line = line.removeRange(charIndex, min(charIndex + 1, lineChars))
            refresh(prompt, 2)
        }
    }

    fun refresh(prompt: String, padding: Int = 0) {
        saveCursor()
        term.print("\r$prompt$line${" ".repeat(max(0, padding))}")
        restoreCursor()
        term.flush()
    }
}
