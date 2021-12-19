package cc.ekblad.kotline

import java.nio.file.Files
import java.nio.file.Paths

val winTerm: WinTerm by lazy { WinTerm() }

private val dllLoaded: Boolean by lazy {
    val homeDir = System.getProperty("user.home")
    val kotlineDir = Paths.get(homeDir, ".kotline")
    if (Files.notExists(kotlineDir)) {
        Files.createDirectory(kotlineDir)
    }
    val dllPath = Paths.get(kotlineDir.toString(), "windows_console_setup.dll").toAbsolutePath()
    Files.deleteIfExists(dllPath)
    WinTerm::class.java.getResourceAsStream("windows_console_setup.dll").use { source ->
        Files.newOutputStream(dllPath).use { source!!.copyTo(it) }
    }
    System.load(dllPath.toString())
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
