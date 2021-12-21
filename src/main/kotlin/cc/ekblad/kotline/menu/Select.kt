package cc.ekblad.kotline.menu

import cc.ekblad.kotline.Kotline

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
    initiallySelected: List<Int> = emptyList(),
    marker: Char = '*'
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

    val checkedMarker = "[$marker] "
    val uncheckedMarker = "[ ] "

    @Suppress("UNUSED_PARAMETER")
    fun onCommit(_index: Int) =
        checked.mapIndexedNotNull { index, selected ->
            if (selected) {
                index
            } else {
                null
            }
        }

    fun onCharacter(selectedIndex: Int, character: Char) {
        if (character == ' ') {
            checked[selectedIndex] = !checked[selectedIndex]
        }
    }

    return genericAsk(
        ::onCommit,
        ::onCharacter,
        { index, _ -> if (checked[index]) checkedMarker else uncheckedMarker },
        1,
        options,
        prompt
    )
}
