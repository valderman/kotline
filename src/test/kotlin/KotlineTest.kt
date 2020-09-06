import cc.ekblad.kotline.Kotline
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

class KotlineTest {
    @Test
    fun `kotline returns ascii input verbatim`() {
        val kotline = Kotline(TestTerm(listOf("foo", "", "bar", "baz")))
        assertEquals("foo", kotline.readLine())
        assertEquals("", kotline.readLine())
        assertEquals("bar", kotline.readLine())
        assertEquals("baz", kotline.readLine())
    }

    @Test
    fun `up arrow selects previous line`() {
        val kotline = Kotline(TestTerm(listOf("foo", "\u001b[A")))
        assertEquals("foo", kotline.readLine())
        assertEquals("foo", kotline.readLine())
    }

    @Test
    fun `up arrow positions cursor at end of selected line`() {
        val kotline = Kotline(TestTerm(listOf("foo", "a\u001b[Ax")))
        assertEquals("foo", kotline.readLine())
        assertEquals("foox", kotline.readLine())
    }

    @Test
    fun `up arrow is a no-op if there is no history`() {
        val kotline = Kotline(TestTerm(listOf("abc\u001b[Ad")))
        assertEquals("abcd", kotline.readLine())
    }

    @Test
    fun `down arrow selects next line`() {
        val kotline = Kotline(TestTerm(listOf("foo", "bar", "\u001b[A\u001B[A\u001B[B")))
        assertEquals("foo", kotline.readLine())
        assertEquals("bar", kotline.readLine())
        assertEquals("bar", kotline.readLine())
    }

    @Test
    fun `down arrow is a no-op if there is no history`() {
        val kotline = Kotline(TestTerm(listOf("abc\u001b[Bd")))
        assertEquals("abcd", kotline.readLine())
    }

    @Test
    fun `down arrow positions cursor at end of selected line`() {
        val kotline = Kotline(TestTerm(listOf("foo", "bar", "\u001b[A\u001B[Ax\u001B[By")))
        assertEquals("foo", kotline.readLine())
        assertEquals("bar", kotline.readLine())
        assertEquals("bary", kotline.readLine())
    }

    @Test
    fun `left arrow moves one character left`() {
        val kotline = Kotline(TestTerm(listOf("abc\u001b[Dx")))
        assertEquals("abxc", kotline.readLine())
    }

    @Test
    fun `right arrow moves one character right`() {
        val kotline = Kotline(TestTerm(listOf("abc\u001b[D\u001B[D\u001B[Cx")))
        assertEquals("abxc", kotline.readLine())
    }

    @Test
    fun `home key moves all the way to the left`() {
        val kotline = Kotline(TestTerm(listOf("abc def\u001b[1~x")))
        assertEquals("xabc def", kotline.readLine())
    }

    @Test
    fun `end key moves all the way to the right`() {
        val kotline = Kotline(TestTerm(listOf("abc def\u001B[1~\u001B[4~x")))
        assertEquals("abc defx", kotline.readLine())
    }

    @Test
    fun `ctrl-left moves one word to the left`() {
        val kotline = Kotline(TestTerm(listOf("abc def   \u001B[1;5Dx")))
        assertEquals("abc xdef   ", kotline.readLine())
    }

    @Test
    fun `ctrl-right moves one word to the right`() {
        val kotline = Kotline(TestTerm(listOf("abc   def   \u001B[1~\u001B[1;5Cx")))
        assertEquals("abcx   def   ", kotline.readLine())
    }

    @Test
    fun `backspace erases previous char`() {
        val kotline = Kotline(TestTerm(listOf("abc\u007f")))
        assertEquals("ab", kotline.readLine())
    }

    @Test
    fun `delete erases current char`() {
        val kotline = Kotline(TestTerm(listOf("abc\u001B[1~\u007f")))
        assertEquals("bc", kotline.readLine())
    }

    @Test
    fun `kotline returns unicode input verbatim`() {
        val kotline = Kotline(TestTerm(listOf("åäö感じ")))
        assertEquals("åäö感じ", kotline.readLine())
    }

    @Test
    fun `kotline determines character width correctly`() {
        val kotline = Kotline(TestTerm(listOf("abc感\u001B[Dx")))
        assertEquals("abcx感", kotline.readLine())
    }

    @Test
    fun `ctrl-left properly skips unicode chars`() {
        val kotline = Kotline(TestTerm(listOf("abc def    åäö感じ  \u001B[1;5Dx")))
        assertEquals("abc def    xåäö感じ  ", kotline.readLine())
    }

    @Test
    fun `ctrl-right properly skips unicode chars`() {
        val kotline = Kotline(TestTerm(listOf("åäö感じabc def  \u001B[1~\u001B[1;5Cx")))
        assertEquals("åäö感じabcx def  ", kotline.readLine())
    }

    @Test
    fun `ctrl-d on empty line sends eof`() {
        val kotline = Kotline(TestTerm(listOf("\u0004")))
        assertNull(kotline.readLine())
    }

    @Test
    fun `ctrl-d on non-empty line is a no-op`() {
        val kotline = Kotline(TestTerm(listOf("abc\u0004def")))
        assertEquals("abcdef", kotline.readLine())
    }

    @Test
    fun `eof with empty line causes kotline to return null`() {
        val kotline = Kotline(TestTerm(listOf()))
        assertNull(kotline.readLine())
    }

    @Test
    fun `eof with nonempty line is equivalent to return`() {
        val kotline = Kotline(TestTerm(listOf("abc")))
        assertEquals("abc", kotline.readLine())
        assertNull(kotline.readLine())
    }

    @Test
    fun `init and close are called exactly once, at init and close respectively`() {
        var initCalled = 0
        var closeCalled = 0
        val term = TestTerm(
            listOf("abc"),
            true,
            { initCalled += 1 },
            { closeCalled += 1 }
        )
        Kotline(term).use {
            assertEquals(1, initCalled)
            assertEquals(0, closeCalled)

            assertEquals("abc", it.readLine())
            assertEquals(1, initCalled)
            assertEquals(0, closeCalled)

            assertNull(it.readLine())
            assertEquals(1, initCalled)
            assertEquals(0, closeCalled)
        }
        assertEquals(1, initCalled)
        assertEquals(1, closeCalled)
    }

    @Test
    fun `empty lines are not added to history`() {
        val kotline = Kotline(TestTerm(listOf("foo", "", "\u001b[A")))
        assertEquals("foo", kotline.readLine())
        assertEquals("", kotline.readLine())
        assertEquals("foo", kotline.readLine())
    }

    @Test
    fun `committed history is not modified`() {
        val kotline = Kotline(TestTerm(listOf("foo", "\u001b[Aabc", "\u001b[A\u001B[A")))
        assertEquals("foo", kotline.readLine())
        assertEquals("fooabc", kotline.readLine())
        assertEquals("foo", kotline.readLine())
    }

    @Test
    fun `a closed kotline object is unusable`() {
        val kotline = Kotline(TestTerm(listOf("a", "b")))
        kotline.close()
        assertNull(kotline.readLine())
    }
}
