package aoc2022

import java.io.File

fun main(args: Array<String>) {
    val elfBackpacks = File("build/resources/main/day1")
        .readText()
        .split("\n\n")
        .map { elfBlock ->
            elfBlock.split("\n").map { it.toInt() }.sum()
        }

    println(elfBackpacks.max())
    println(elfBackpacks.sorted().takeLast(3).sum())
}