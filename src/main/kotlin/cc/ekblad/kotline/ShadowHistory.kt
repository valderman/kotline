package cc.ekblad.kotline

internal class ShadowHistory<T>(
    firstValue: T,
    private val clone: (T) -> T = { it }
) {
    private val history: ArrayList<T> = ArrayList()
    private val shadow: ArrayList<T> = arrayListOf(firstValue)
    private var shadowIndex: Int = 0
    private val historyIndex
        get() = history.size - shadowIndex

    val size: Int
        get() = history.size

    var current: T
        get() = shadow[shadowIndex]
        set(value) { shadow[shadowIndex] = value }

    fun back() {
        if (historyIndex == 0) {
            return
        }
        if (shadowIndex >= shadow.size-1) {
            shadow.add(clone(history[historyIndex-1]))
        }
        shadowIndex += 1
    }

    fun forward() {
        if (shadowIndex == 0) {
            return
        }
        shadowIndex -= 1
    }

    fun commitAndResetShadow(newValue: T): T {
        val itemToCommit = resetShadow(newValue)
        history.add(itemToCommit)
        return itemToCommit
    }

    fun resetShadow(newValue: T): T {
        val itemToCommit = current
        shadowIndex = 0
        shadow.clear()
        shadow.add(newValue)
        return itemToCommit
    }
}
