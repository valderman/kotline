/**
 * Input/output utilities for ANSI terminals.
 */
package cc.ekblad.kotline

private const val ACTUAL_EOF = -1

private const val K_RETURN = 10
private const val K_ESCAPE = 27
private const val K_BACKSPACE = 127
private const val K_EOF = 4
private const val K_BRACKET = 91
private const val K_TILDE = 126
private const val K_SEMICOLON = 59

private const val E_LEFT = 68
private const val E_RIGHT = 67
private const val E_UP = 65
private const val E_DOWN = 66
private const val E_DELETE = 51
private const val E_END = 52
private const val E_ESCAPE_MORE = 49

private const val SEQ_CTRL_LEFT = "5D"
private const val SEQ_CTRL_RIGHT = "5C"

internal fun getInput(term: Term): Input? {
    val c = readUtf8Char(term)
    return when (c) {
        K_RETURN -> Return
        K_ESCAPE -> handleEscape(term)
        K_BACKSPACE -> Backspace
        K_EOF -> EOF
        ACTUAL_EOF -> null
        else -> Character(c.toChar())
    }
}

private fun handleEscape(term: Term): Input? {
    val c = term.getChar()
    return if (c == K_BRACKET) {
        handleEscapeSequence(term)
    } else {
        null
    }
}

private fun handleEscapeSequence(term: Term) =
    when (term.getChar()) {
        E_LEFT -> Left
        E_RIGHT -> Right
        E_UP -> Up
        E_DOWN -> Down
        E_DELETE -> { term.getChar(); Delete }
        E_END -> { term.getChar(); End }
        E_ESCAPE_MORE -> handleEscapeCombo(term)
        else -> null
    }

private fun handleEscapeCombo(term: Term) =
    when (term.getChar()) {
        K_TILDE -> Home
        K_SEMICOLON -> {
            val c3 = term.getChar()
            val c4 = term.getChar()
            when ("${c3.toChar()}${c4.toChar()}") {
                SEQ_CTRL_LEFT -> ControlLeft
                SEQ_CTRL_RIGHT -> ControlRight
                else -> null
            }
        }
        else -> null
    }

internal fun saveCursor() {
    print("\u001b[s")
}

internal fun restoreCursor() {
    print("\u001b[u")
}
