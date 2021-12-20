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
    
    prompt?.let { println(it) }

    while (true) {
        options.forEachIndexed { index, option ->
            val markerPrefix = if (index == selectedIndex) marker else emptyMarker
            println("\r$markerPrefix $option")
        }

        when (getInput(term)) {
            Up -> selectedIndex = max(0, selectedIndex - 1)
            Down -> selectedIndex = min(options.size - 1, selectedIndex + 1)
            Return -> return selectedIndex
            else -> { /* no-op */}
        }
        cursorUp(options.size)
    }
}
