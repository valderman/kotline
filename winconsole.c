/* gcc -shared -s -Os -fno-ident -nostdlib -o winconsole.dll winconsole.c -lkernel32 */
#include <windows.h>

void __stdcall Java_cc_ekblad_kotline_WinTerm_setupConsole(void* env, void* jobject) {
    HANDLE console = GetStdHandle(STD_INPUT_HANDLE);
    DWORD mode = 0;
    GetConsoleMode(console, &mode);
    mode &= ~ENABLE_LINE_INPUT;
    mode &= ~ENABLE_ECHO_INPUT;
    SetConsoleMode(console, mode);
}

void __stdcall Java_cc_ekblad_kotline_WinTerm_shutdownConsole(void* env, void* jobject) {
    HANDLE console = GetStdHandle(STD_INPUT_HANDLE);
    DWORD mode = 0;
    GetConsoleMode(console, &mode);
    mode |= ENABLE_LINE_INPUT;
    mode |= ENABLE_ECHO_INPUT;
    SetConsoleMode(console, mode);
}
