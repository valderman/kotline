# Kotline
A minimalist library for interactive terminal applications for Kotlin/JVM.

## Features
- Line editing and history
- Single- and multiselect menus
  - With paging for arbitrarily small terminal windows
- Cursor control
- Multiplatform (Linux, OSX, Windows)
- No dependencies

## How to use

Add a dependency to your `build.gradle.kts`:

```kotlin
repositories {
    maven {
        url = uri("https://jitpack.io")
    }
}

dependencies {
    implementation("cc.ekblad:kotline:0.3.0")
}
```

Then write some code:

```kotlin
fun main() {
    // Note that kotline needs to mess with your terminal settings to do its magic.
    // Reading/writing standard input/output using non-kotline functions while a kotline block is executing
    // will most likely give you unexpected results, even if done from within the kotline block.
    // Kotline is also *NOT* thread-safe, so don't try to run concurrent kotline blocks.
    kotline {
        // Basic line input
        val line1 = readLine()
        val line2 = readLine("a prompt> ")

        // Single choice selection
        val preferredAnimal = ask("Cats", "Dogs", "Ducks", prompt = "Which do you prefer?")

        // Multiple choice selection
        val foodPreferences = select("Sushi", "Salad", "Pasta", "Sausage", prompt = "Select any foods you like")
    }
}
```
