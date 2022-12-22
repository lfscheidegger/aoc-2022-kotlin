package aoc2022

import java.io.File

fun main() {
    val total = File("build/resources/main/day4")
        .readLines()
        .map {
            val (left, right) = it.split(",")
            val (minLeft, maxLeft) = left.split("-").map { it.toInt() }
            val (minRight, maxRight) = right.split("-").map { it.toInt() }

            if (minLeft <= minRight && maxLeft >= maxRight) {
                1
            } else if (minRight <= minLeft && maxRight >= maxLeft) {
                1
            } else {
                0
            }
        }
        .sum()
    println(total)

    val total2 = File("build/resources/main/day4")
        .readLines()
        .map {
            val (left, right) = it.split(",")
            val (minLeft, maxLeft) = left.split("-").map { it.toInt() }
            val (minRight, maxRight) = right.split("-").map { it.toInt() }

            val leftRange = (minLeft..maxLeft).toSet()
            val rightRange = (minRight..maxRight).toSet()

            if (leftRange.intersect(rightRange).isNotEmpty()) {
                1
            } else {
                0
            }
        }
        .sum()
    println(total2)
}