package cc.ekblad.kotline

import kotlin.math.max
import kotlin.math.min

class Kotline(private val term: Term) {
    private var committedHistory: ArrayList<String> = ArrayList()
    private val workingHistory: ArrayList<String> = ArrayList(listOf(""))
    private var currentLineIndex: Int = 0
    private var charIndex: Int = 0
    private val cursorPosition: Int
        get() = currentLine.take(charIndex).sumBy(::charWidth)
    private var currentLine: String
        get() = workingHistory[currentLineIndex]
        set(value) { workingHistory[currentLineIndex] = value }
    private val currentLineChars: Int
        get() = currentLine.length
    private val currentLineWidth: Int
        get() = currentLine.sumBy(::charWidth)
    private var prompt: String = ""

    init {
        term.setup()
        Runtime.getRuntime().addShutdownHook(Thread { term.shutdown() })
    }

    fun readLine(prompt: String = ""): String? {
        this.prompt = prompt
        moveCursor(0)
        while (true) {
            when (val c = getInput(term)) {
                is Character -> addToCurrentLine(c.char)
                is Return -> return handleReturn()
                is Left -> moveCursor(-1)
                is Right -> moveCursor(1)
                is Delete -> deleteCharAtCursor(false)
                is Backspace -> deleteCharAtCursor(true)
                is Home -> moveCursor(-currentLineChars)
                is End -> moveCursor(currentLineChars)
                is ControlLeft -> oneWordBack()
                is ControlRight -> oneWordForward()
                is Up -> moveHistory(-1)
                is Down -> moveHistory(1)
                is EOF -> if (currentLine.isEmpty()) {
                    return handleEof()
                }
            }
        }
    }

    private fun oneWordBack() {
        val prefix = currentLine.take(charIndex)
        val blank = prefix.takeLastWhile { it.isWhitespace() }.length
        val nonblank = prefix.trimEnd().takeLastWhile { !it.isWhitespace() }.length
        val offset = blank + nonblank
        moveCursor(-offset)
    }

    private fun oneWordForward() {
        val prefix = currentLine.drop(charIndex)
        val blank = prefix.takeWhile { it.isWhitespace() }.length
        val nonblank = prefix.trimStart().takeWhile { !it.isWhitespace() }.length
        val offset = blank + nonblank
        moveCursor(offset)
    }

    private fun handleEof(): String? {
        newLine()
        return null
    }

    private fun handleReturn(): String {
        newLine()
        return if(currentLine.isBlank()) {
            currentLine
        } else {
            commitLine()
        }
    }

    private fun moveHistory(offset: Int) {
        val oldWidth = currentLineWidth
        currentLineIndex = min(workingHistory.size-1, max(0, currentLineIndex + offset))
        refreshCurrentLine(oldWidth - currentLineWidth)
        moveCursor(currentLineChars)
    }

    private fun addToCurrentLine(char: Char) {
        var before = currentLine
        var after = ""
        if(charIndex < currentLineChars) {
            before = currentLine.substring(0, charIndex)
            after = currentLine.substring(charIndex)
        }
        currentLine = before + char + after
        moveCursor(1)
        refreshCurrentLine(2)
    }

    private fun deleteCharAtCursor(before: Boolean) {
        if (!currentLine.isEmpty()) {
            if(before) {
                moveCursor(-1)
            }
            currentLine = currentLine.removeRange(charIndex, min(charIndex + 1, currentLineChars))
            refreshCurrentLine(2)
        }
    }

    private fun refreshCurrentLine(padding: Int = 0) {
        saveCursor()
        print("\r$prompt$currentLine${" ".repeat(max(0, padding))}")
        restoreCursor()
    }

    private fun moveCursor(offset: Int) {
        when {
            offset > 0 -> charIndex = min(charIndex + offset, currentLineChars)
            offset < 0 -> charIndex = max(charIndex + offset, 0)
        }
        print("\r$prompt")
        if(cursorPosition > 0) {
            print("\u001b[${cursorPosition}C")
        }
    }

    private fun newLine() {
        println()
        moveCursor(-currentLineChars)
    }

    private fun commitLine(): String {
        val line = currentLine
        committedHistory.add(currentLine)
        workingHistory.clear()
        workingHistory.addAll(committedHistory)
        workingHistory.add("")
        currentLineIndex = workingHistory.size - 1
        return line
    }
}
