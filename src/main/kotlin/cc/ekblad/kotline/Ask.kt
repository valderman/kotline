package cc.ekblad.kotline

import kotlin.math.max
import kotlin.math.min

/**
 * Ask the user to choose one of the given options.
 *
 * @return The selected option.
 */
fun <T> Kotline.ask(vararg options: T, prompt: String? = null, marker: String = ">"): T =
    options[ask(options.map { it.toString() }, prompt, marker)]

/**
 * Ask the user to choose one of the given options, presented in a vertical list.
 * The user navigates using the arrow keys and selects an item using return.
 *
 * @param prompt Text to print above selection. Omitted if null.
 * @param marker Marker to indicate the currently selected option. Defaults to `>` if null.
 * @return The index of the chosen option.
 */
fun Kotline.ask(options: List<String>, prompt: String? = null, marker: String = ">"): Int {
    require(options.isNotEmpty()) {
        "at least one option must be given"
    }
    var selectedIndex = 0
    val emptyMarker = "".padEnd(marker.length)

    val (width, height) = term.getTermSize()
    val trimmedOptions = options.map { it.chop(width - marker.length).padEnd(width - marker.length - 1) }

    prompt?.let { println(it) }

    val itemsToShow = height - (if (prompt == null) 1 else 2)
    val menuSize = min(itemsToShow, trimmedOptions.size)
    var screenTop = 0
    while (true) {
        term.saveCursor()
        trimmedOptions.subList(screenTop, screenTop + menuSize).forEachIndexed { index, option ->
            val markerPrefix = if (index == selectedIndex - screenTop) marker else emptyMarker
            println("\r$markerPrefix $option")
        }
        if (itemsToShow < trimmedOptions.size) {
            print("↑$selectedIndex ↓${trimmedOptions.size - selectedIndex - 1}".padEnd(width))
        }

        term.cursorUp(menuSize - (selectedIndex - screenTop))
        val input = getInput(term)
        if (selectedIndex > 0) {
            term.restoreCursor()
        }
        when (input) {
            Input.Up -> selectedIndex = max(0, selectedIndex - 1)
            Input.Down -> selectedIndex = min(trimmedOptions.size - 1, selectedIndex + 1)
            Input.Return -> {
                if (prompt != null) {
                    term.cursorUp(1)
                }
                term.clearScreen()
                return selectedIndex
            }
            else -> { /* no-op */ }
        }

        if(selectedIndex >= itemsToShow + screenTop) {
            screenTop += 1
        }
        if(selectedIndex < screenTop) {
            screenTop -= 1
        }
    }
}

internal fun String.chop(maxLength: Int): String = if (this.length > maxLength) {
    substring(0, maxLength - 4) + "..."
} else {
    this
}
