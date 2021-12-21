package cc.ekblad.kotline.menu

import cc.ekblad.kotline.Kotline

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
    val emptyMarker = "".padEnd(marker.length)
    return genericAsk(
        { it },
        { _, _ -> Unit },
        { index, selectedIndex -> if (index == selectedIndex) marker else emptyMarker },
        0,
        options,
        prompt
    )
}
