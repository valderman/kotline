package cc.ekblad.kotline.terminal

import cc.ekblad.kotline.Kotline

/**
 * Move the cursor left `cols` columns.
 */
fun Kotline.cursorLeft(cols: Int) {
    term.print("\u001b[${cols}D")
}

/**
 * Move the cursor right `cols` columns.
 */
fun Kotline.cursorRight(cols: Int) {
    term.print("\u001b[${cols}C")
}

/**
 * Move the cursor up `lines` lines.
 */
fun Kotline.cursorUp(lines: Int) {
    term.print("\u001b[${lines}A\r")
}

/**
 * Move the cursor down `lines` lines.
 */
fun Kotline.cursorDown(lines: Int) {
    term.print("\u001b[${lines}B\r")
}

/**
 * Save the position of the cursor.
 * The saved cursor position can then be returned to using [restoreCursor].
 */
fun Kotline.saveCursor() {
    term.saveCursor()
}

/**
 * Return the cursor to the position saved by the last call to [saveCursor].
 */
fun Kotline.restoreCursor() {
    term.restoreCursor()
}

/**
 * Clear the entire screen from the cursor and forward/down.
 */
fun Kotline.clearScreen() {
    term.print("\u001b[J")
}

/**
 * Returns a pair of the current terminal's width and height, expressed in number of characters.
 */
fun Kotline.getTermSize(): Pair<Int, Int> {
    saveCursor()
    term.print("\u001B[9999;9999H")
    term.print("\u001B[6n")
    restoreCursor()

    val chars = mutableListOf<Int>()
    var c = term.getChar()
    while (c != 'R'.code) {
        c = term.getChar()
        chars += c
    }

    val s = chars.map { it.toChar() }.joinToString("")
    val result = termSizeRegex.find(s)
    checkNotNull(result)
    return Pair(result.groupValues[2].toInt(), result.groupValues[1].toInt())
}

private val termSizeRegex = Regex("\\[([0-9]+);([0-9]+)R")

internal fun Term.saveCursor() {
    this.print("\u001b[s")
}

internal fun Term.restoreCursor() {
    this.print("\u001b[u")
}
