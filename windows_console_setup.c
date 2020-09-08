/* gcc -shared -s -Os -fno-ident -nostdlib -o windows_console_setup.dll windows_console_setup.c -lkernel32 */
#include <windows.h>

#ifndef ENABLE_VIRTUAL_TERMINAL_PROCESSING
#define ENABLE_VIRTUAL_TERMINAL_PROCESSING 0x0004
#endif

#ifndef ENABLE_VIRTUAL_TERMINAL_INPUT
#define ENABLE_VIRTUAL_TERMINAL_INPUT 0x0200
#endif

DWORD stdin_mode = 0;
DWORD stdout_mode = 0;

void __stdcall Java_cc_ekblad_kotline_WinTerm_setupConsole(void* env, void* jobject) {
    HANDLE console = GetStdHandle(STD_INPUT_HANDLE);
    DWORD mode = 0;
    GetConsoleMode(console, &mode);
    stdin_mode = mode;
    mode &= ~ENABLE_LINE_INPUT;
    mode &= ~ENABLE_ECHO_INPUT;
    mode |= ENABLE_VIRTUAL_TERMINAL_INPUT;
    SetConsoleMode(console, mode);

    console = GetStdHandle(STD_OUTPUT_HANDLE);
    GetConsoleMode(console, &mode);
    stdout_mode = mode;
    mode |= ENABLE_PROCESSED_OUTPUT;
    mode |= ENABLE_VIRTUAL_TERMINAL_PROCESSING;
    SetConsoleMode(console, mode);
}

void __stdcall Java_cc_ekblad_kotline_WinTerm_shutdownConsole(void* env, void* jobject) {
    SetConsoleMode(GetStdHandle(STD_INPUT_HANDLE), stdin_mode);
    SetConsoleMode(GetStdHandle(STD_OUTPUT_HANDLE), stdout_mode);
}

DWORD __stdcall DllMainCRTStartup() {
    return 1;
}
