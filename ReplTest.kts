import cc.ekblad.kotline.kotline

fun main() {
    println("kotline!")
    kotline {
        var input = this.readLine("prompt> ")
        while (input != null) {
            println(input)
            input = this.readLine("prompt> ")
        }
    }
}

main()
