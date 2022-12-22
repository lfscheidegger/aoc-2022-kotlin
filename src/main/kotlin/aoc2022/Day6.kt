package aoc2022

import java.io.File

fun main() {
    val stream = File("build/resources/main/day6").readText()

    // Part 1
    println(stream
        .windowed(4, 1)
        .zip(1 .. stream.length )
        .first { it.first.toSet().size == 4 }
        .second + 3 )

    // Part 2
    println(stream
        .windowed(14, 1)
        .zip(1 .. stream.length )
        .first { it.first.toSet().size == 14 }
        .second + 13 )
}