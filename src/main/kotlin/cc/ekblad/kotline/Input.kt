package cc.ekblad.kotline

internal sealed class Input
internal class Character(val char: Char): Input()
internal object Down : Input()
internal object Up: Input()
internal object Right: Input()
internal object Left: Input()
internal object Home: Input()
internal object End: Input()
internal object Delete: Input()
internal object Backspace: Input()
internal object Return: Input()
internal object ControlLeft: Input()
internal object ControlRight: Input()
internal class EOF(val hard: Boolean): Input()