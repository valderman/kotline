package cc.ekblad.kotline

import java.nio.file.Files
import java.nio.file.Path

val winTerm: WinTerm by lazy { WinTerm() }

private val dllLoaded: Boolean by lazy {
    val tempDir = createTempDir("kotline")
    val dllPath = Path.of(tempDir.absolutePath, "winconsole.dll")
    WinTerm::class.java.getResourceAsStream("winconsole.dll").use { source ->
        Files.newOutputStream(dllPath).use { source.copyTo(it) }
    }
    System.load(dllPath.toAbsolutePath().toString())
    Runtime.getRuntime().addShutdownHook(Thread {
        Files.delete(dllPath)
        Files.delete(dllPath.parent)
    })
    true
}

/**
 * Terminal implementation for Windows systems.
 */
class WinTerm : Term {
    private external fun setupConsole()
    private external fun shutdownConsole()

    override fun init() {
        check(dllLoaded)
        setupConsole()
    }

    override fun close() {
        shutdownConsole()
    }
}
