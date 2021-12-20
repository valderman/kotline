package cc.ekblad.kotline

import kotlin.math.max
import kotlin.math.min

/**
 * Ask the user to choose zero or more of the given options.
 *
 * @return All selected options.
 */
fun <T> Kotline.select(
    vararg options: T,
    prompt: String? = null,
    defaultSelectionState: Boolean = false,
    initiallySelected: List<Int> = emptyList()
): List<T> =
    select(
        options.map { it.toString() },
        prompt,
        defaultSelectionState,
        initiallySelected
    ).map { options[it] }

/**
 * Ask the user to choose zero or more of the given options.
 * The menu is navigated using the arrow keys, selections are toggled using the space key,
 * and the selection is confirmed using return.
 *
 * @param options The options available to choose from.
 * @param prompt Prompt to display above menu. Omitted if null.
 * @param defaultSelectionState If true, all options are initially selected. Otherwise, options are unselected.
 * @param initiallySelected Indices of any options to select by default, after applying the default selection state
 *  given by `defaultSelectionState`.
 * @return The indices of all selected options.
 */
fun Kotline.select(
    options: List<String>,
    prompt: String? = null,
    defaultSelectionState: Boolean = false,
    initiallySelected: List<Int> = emptyList()
): List<Int> {
    if (options.isEmpty()) {
        return emptyList()
    }
    val indicesOutOfBounds = initiallySelected.filter { it < 0 || it >= options.size }
    require(indicesOutOfBounds.isEmpty()) {
        "initially selected indices out of bounds: ${indicesOutOfBounds.joinToString()}"
    }

    val checked = options.map { defaultSelectionState }.toTypedArray()
    initiallySelected.forEach { checked[it] = true }

    var selectedIndex = 0
    val checkedMarker = "[*] "
    val uncheckedMarker = "[ ] "

    prompt?.let { println(it) }

    while (true) {
        options.forEachIndexed { index, option ->
            val markerPrefix = when {
                checked[index] -> checkedMarker
                else -> uncheckedMarker
            }
            println("\r$markerPrefix $option")
        }
        cursorUp(options.size - selectedIndex)
        print("\r[")

        val input = getInput(term)
        if (selectedIndex > 0) {
            cursorUp(selectedIndex)
        }
        when (input) {
            Up -> selectedIndex = max(0, selectedIndex - 1)
            Down -> selectedIndex = min(options.size - 1, selectedIndex + 1)
            Character(' ') -> checked[selectedIndex] = !checked[selectedIndex]
            Return -> {
                cursorDown(options.size)
                return checked.mapIndexedNotNull { index, selected ->
                    if (selected) {
                        index
                    } else {
                        null
                    }
                }
            }
            else -> { /* no-op */ }
        }
    }
}
