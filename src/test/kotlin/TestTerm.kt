import cc.ekblad.kotline.Term

class TestTerm(
    inputLines: List<String>,
    sendReturnAtEol: Boolean = true,
    private val onInit: () -> Unit = { },
    private val onClose: () -> Unit = { }
) : Term {
    override fun init() { onInit() }
    override fun close() { onClose() }

    @ExperimentalUnsignedTypes
    @ExperimentalStdlibApi
    private val chars : Iterator<Int> = iterator {
        inputLines.forEach {
            it.encodeToByteArray().forEach { yield(it.toUByte().toInt()) }
            if (sendReturnAtEol) {
                yield('\n'.toInt())
            }
        }
    }

    @ExperimentalUnsignedTypes
    @ExperimentalStdlibApi
    override fun getChar(): Int =
        if (chars.hasNext()) {
            chars.next()
        } else {
            -1
        }

}
