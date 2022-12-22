package aoc2022

import java.io.File

fun main() {
    val first = File("build/resources/main/day3")
        .readLines().sumOf { line ->
            val firstHalf = line.substring(0, line.length / 2)
            val secondHalf = line.substring(line.length / 2)

            val doubled = firstHalf.first { secondHalf.contains(it) }

            if (doubled.isLowerCase()) {
                doubled.code - 'a'.code + 1
            } else {
                doubled.code - 'A'.code + 27
            }
        }

    println(first)

    val second = File("build/resources/main/day3")
        .readLines()
        .chunked(3).sumOf { (first, second, third) ->
            val common = first.first { second.contains(it) && third.contains(it) }

            if (common.isLowerCase()) {
                common.code - 'a'.code + 1
            } else {
                common.code - 'A'.code + 27
            }
        }

    println(second)
}