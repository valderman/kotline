package cc.ekblad.kotline.menu

import cc.ekblad.kotline.Kotline
import cc.ekblad.kotline.terminal.Input
import cc.ekblad.kotline.terminal.clearScreen
import cc.ekblad.kotline.terminal.cursorRight
import cc.ekblad.kotline.terminal.cursorUp
import cc.ekblad.kotline.terminal.getInput
import cc.ekblad.kotline.terminal.getTermSize
import cc.ekblad.kotline.terminal.restoreCursor
import cc.ekblad.kotline.terminal.saveCursor
import kotlin.math.max
import kotlin.math.min

internal fun <T> Kotline.genericAsk(
    onCommit: (selectedIndex: Int) -> T,
    onCharacter: (selectedIndex: Int, character: Char) -> Unit,
    formatMarker: (index: Int, selectedIndex: Int) -> String,
    cursorOffset: Int,
    options: List<String>,
    prompt: String? = null
): T {
    require(options.isNotEmpty()) {
        "at least one option must be given"
    }

    var selectedIndex = 0
    val (width, height) = getTermSize()

    // Clamp options to the width of the terminal
    val markerSize = formatMarker(0, 0).length
    val trimmedOptions = options.map { it.chop(width - markerSize).padEnd(width - markerSize - 1) }

    prompt?.let { println(it) }

    // Set up sliding window if we have more options than the terminal can display
    val itemsToShow = height - (if (prompt == null) 1 else 2)
    val menuSize = min(itemsToShow, trimmedOptions.size)
    var screenTop = 0

    while (true) {
        saveCursor()
        trimmedOptions.subList(screenTop, screenTop + menuSize).forEachIndexed { index, option ->
            val markerPrefix = formatMarker(index + screenTop, selectedIndex)
            println("\r$markerPrefix $option")
        }

        // Print summary of how many options exist above and below the cursor respectively,
        // if we have more options than the terminal can display
        if (itemsToShow < trimmedOptions.size) {
            print("↑$selectedIndex ↓${trimmedOptions.size - selectedIndex - 1}".padEnd(width))
        }

        // Position the cursor at the currently selected element
        cursorUp(menuSize - (selectedIndex - screenTop))
        if (cursorOffset > 0) {
            cursorRight(cursorOffset)
        }

        // Return the cursor to the beginning of the menu as soon as we get our input
        val input = getInput(term)
        if (selectedIndex > 0) {
            restoreCursor()
        }
        when (input) {
            Input.Up -> selectedIndex = max(0, selectedIndex - 1)
            Input.Down -> selectedIndex = min(trimmedOptions.size - 1, selectedIndex + 1)
            is Input.Character -> onCharacter(selectedIndex, input.char)
            Input.Return -> {
                // Erase menu before returning
                if (prompt != null) {
                    cursorUp(1)
                }
                clearScreen()
                return onCommit(selectedIndex)
            }
            else -> { /* no-op */ }
        }

        // Adjust the sliding window of items if we have more options than the terminal can display
        if (selectedIndex >= itemsToShow + screenTop) {
            screenTop += 1
        }
        if (selectedIndex < screenTop) {
            screenTop -= 1
        }
    }
}

private fun String.chop(maxLength: Int): String = if (this.length > maxLength) {
    substring(0, maxLength - 4) + "..."
} else {
    this
}
