/**
 * Unicode and UTF-8 utilities.
 */
package cc.ekblad.kotline

internal fun convertUtf8Char(vararg chars: Byte) =
    String(chars, Charsets.UTF_8).codePointAt(0)

internal fun charWidth(char: Char) =
    when (char.toInt()) {
        in 0x1100..0x115F -> 2
        in 0x231A..0x231B -> 2
        in 0x2329..0x232A -> 2
        in 0x23E9..0x23EC -> 2
        0x23F0 -> 2
        0x23F3 -> 2
        in 0x25FD..0x25FE -> 2
        in 0x2614..0x2615 -> 2
        in 0x2648..0x2653 -> 2
        0x267F -> 2
        2693 -> 2
        0x26A1 -> 2
        in 0x26AA..0x26AB -> 2
        in 0x26BD..0x26BE -> 2
        in 0x26C4..0x26C5 -> 2
        0x26CE -> 2
        0x26D4 -> 2
        0x26EA -> 2
        in 0x26F2..0x26F3 -> 2
        0x26F5 -> 2
        0x26FA -> 2
        0x26FD -> 2
        0x2705 -> 2
        in 0x270A..0x270B -> 2
        0x2728 -> 2
        0x274C -> 2
        0x274E -> 2
        in 0x2753..0x2755 -> 2
        0x2757 -> 2
        in 0x2795..0x2797 -> 2
        0x27B0 -> 2
        0x27BF -> 2
        in 0x2B1B..0x2B1C -> 2
        0x2B50 -> 2
        0x2B55 -> 2
        in 0x2E80..0x2E99 -> 2
        in 0x2E9B..0x2EF3 -> 2
        in 0x2F00..0x2FD5 -> 2
        in 0x2FF0..0x2FFB -> 2
        in 0x3001..0x303E -> 2
        in 0x3041..0x3096 -> 2
        in 0x3099..0x309A -> 2
        in 0x309B..0x30FF -> 2
        in 0x3105..0x312F -> 2
        in 0x3131..0x318E -> 2
        in 0x3190..0x31E3 -> 2
        in 0x31F0..0x321E -> 2
        in 0x3220..0x3247 -> 2
        in 0x3250..0x4DBF -> 2
        in 0x4E00..0x9FFF -> 2
        in 0xA000..0xA4C6 -> 2
        in 0xA960..0xA97C -> 2
        in 0xAC00..0xD7A3 -> 2
        in 0xF900..0xFAFF -> 2
        in 0xFE10..0xFE19 -> 2
        in 0xFE30..0xFE66 -> 2
        in 0xFE68..0xFE6B -> 2
        else -> 1
    }

internal fun readUtf8Char(term: Term): Int {
    val c = term.getChar()
    return when {
        c <= 0x7f -> c
        c in 0xc2..0xdf -> {
            val c2 = term.getChar().toByte()
            convertUtf8Char(c.toByte(), c2)
        }
        c in 0xe0..0xef -> {
            val c2 = term.getChar().toByte()
            val c3 = term.getChar().toByte()
            convertUtf8Char(c.toByte(), c2, c3)
        }
        else -> {
            val c2 = term.getChar().toByte()
            val c3 = term.getChar().toByte()
            val c4 = term.getChar().toByte()
            convertUtf8Char(c.toByte(), c2, c3, c4)
        }
    }
}