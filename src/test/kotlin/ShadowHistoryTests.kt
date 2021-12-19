import cc.ekblad.kotline.ShadowHistory
import kotlin.test.Test
import kotlin.test.assertEquals

class ShadowHistoryTests {
    @Test
    fun `initial value starts off as current`() {
        val history = ShadowHistory("hej")
        assertEquals("hej", history.current)
    }

    @Test
    fun `history starts off empty`() {
        val history = ShadowHistory("hej")
        assertEquals(0, history.size)
    }

    @Test
    fun `stepping an empty history is a no-op`() {
        val history = ShadowHistory("hej")
        history.forward()
        assertEquals(0, history.size)
        assertEquals("hej", history.current)
        history.back()
        assertEquals(0, history.size)
        assertEquals("hej", history.current)
    }

    @Test
    fun `setting the current value overwrites the old one`() {
        val history = ShadowHistory("hej")
        history.current = "inte hej"
        assertEquals("inte hej", history.current)
        assertEquals(0, history.size)
    }

    @Test
    fun `committing writes the current value to history and sets a new current value`() {
        val history = ShadowHistory("hej")
        history.commitAndResetShadow("hej igen")
        assertEquals("hej igen", history.current)
        assertEquals(1, history.size)
        history.back()
        assertEquals("hej", history.current)
    }

    @Test
    fun `back and forward can browse history`() {
        val history = ShadowHistory("a")
        history.commitAndResetShadow("b")
        history.commitAndResetShadow("c")
        assertEquals("c", history.current)
        assertEquals(2, history.size)
        history.back()
        assertEquals("b", history.current)
        history.back()
        assertEquals("a", history.current)
        history.back()
        assertEquals("a", history.current)
        history.forward()
        history.forward()
        history.forward()
        history.forward()
        history.forward()
        assertEquals("c", history.current)
    }

    @Test
    fun `edits to history items are preserved when browsing`() {
        val history = ShadowHistory("hej")
        history.commitAndResetShadow("hej igen")
        history.commitAndResetShadow("hej x 3")
        history.back()
        assertEquals("hej igen", history.current)
        history.current = "inte hej"
        history.back()
        assertEquals("hej", history.current)
        history.forward()
        history.forward()
        assertEquals("hej x 3", history.current)
        history.back()
        assertEquals("inte hej", history.current)
    }

    @Test
    fun `edits to history items are reset by committing`() {
        val history = ShadowHistory("hej")
        history.commitAndResetShadow("hej igen")
        history.back()
        assertEquals("hej", history.current)
        history.current = "inte hej"
        assertEquals("inte hej", history.current)
        history.commitAndResetShadow("hej x 3")
        history.back()
        history.back()
        assertEquals("hej", history.current)
    }

    @Test
    fun `current value is committed regardless of history state`() {
        val history = ShadowHistory("a")
        history.commitAndResetShadow("b")
        history.commitAndResetShadow("c")
        history.back()
        history.back()
        history.commitAndResetShadow("x")
        history.back()
        assertEquals("a", history.current)
    }

    @Test
    fun `mutating shadow object does not mutate history`() {
        val history = ShadowHistory(arrayOf("a")) {
            it.clone()
        }
        history.commitAndResetShadow(arrayOf("b"))
        history.back()
        history.current[0] = "very much not a"
        history.commitAndResetShadow(arrayOf("c"))
        history.back()
        history.back()
        assertEquals(listOf("a"), history.current.toList())
    }

    @Test
    fun `committing adds the new value to the latest point in history`() {
        val history = ShadowHistory("a")
        history.commitAndResetShadow("b")
        history.commitAndResetShadow("c")
        assertEquals("c", history.current)
        history.forward()
        assertEquals("c", history.current)
    }

    @Test
    fun `resetting shadow does not modify history`() {
        val history = ShadowHistory("a")
        history.commitAndResetShadow("b")
        history.resetShadow("c")
        history.back()
        assertEquals("a", history.current)
    }

    @Test
    fun `commit returns latest value`() {
        val history = ShadowHistory("a")
        assertEquals("a", history.commitAndResetShadow("b"))
        history.current = "xyz"
        assertEquals("xyz", history.commitAndResetShadow("c"))
        history.back()
        history.current = history.current + "åäö"
        assertEquals("xyzåäö", history.commitAndResetShadow("c"))
    }

    @Test
    fun `reset returns latest value`() {
        val history = ShadowHistory("abc")
        history.commitAndResetShadow("a")
        assertEquals("a", history.resetShadow("b"))
        history.current = "xyz"
        assertEquals("xyz", history.resetShadow("c"))
        history.back()
        history.current = history.current + "åäö"
        assertEquals("abcåäö", history.resetShadow("c"))
    }
}
