package aoc2022

import java.io.File
import kotlin.math.abs

fun main() {
    part2()
}

private fun part1() {
    // Part 1
    val sensors = File("build/resources/main/day15")
        .readLines()
        .map { line ->
            val capture = Regex("""Sensor at x=(-?\d+), y=(-?\d+): closest beacon is at x=(-?\d+), y=(-?\d+)""").matchEntire(line)
            capture ?: error("bad pattern")
            Sensor(
                capture.groupValues[1].toInt() to capture.groupValues[2].toInt(),
                capture.groupValues[3].toInt() to capture.groupValues[4].toInt())
        }

    var result = 0
    val rowToTest = 2_000_000

    val beacons = sensors.map { it.closestBeacon }.toSet()

    (-10*rowToTest .. 10*rowToTest).forEach { column ->
        val candidate = column to rowToTest

        if(!beacons.contains(candidate) && sensors.firstOrNull { candidate.manhattan(it.position) <= it.closestDistance } != null) {
            result += 1
        }
    }
    println(result)
}

private fun part2() {
    // Part 1
    val sensors = File("build/resources/main/day15")
        .readLines()
        .map { line ->
            val capture = Regex("""Sensor at x=(-?\d+), y=(-?\d+): closest beacon is at x=(-?\d+), y=(-?\d+)""").matchEntire(line)
            capture ?: error("bad pattern")
            Sensor(
                capture.groupValues[1].toInt() to capture.groupValues[2].toInt(),
                capture.groupValues[3].toInt() to capture.groupValues[4].toInt())
        }

    val beacons = sensors.map { it.closestBeacon }.toSet()

    val bounds = 4_000_000
    var foundX: Int = -1
    var foundY: Int= -1
    (0 .. bounds).forEach outer@{ row ->
        var column = 0
        while (column <= bounds) {
            var candidate = column to row
            while (beacons.contains(candidate)) {
                column += 1
                candidate = column to row
            }

            var shouldSkip = false
            sensors.firstOrNull { candidate.manhattan(it.position) <= it.closestDistance }?.let { sensor ->
                column = sensor.position.first + sensor.closestDistance - abs(candidate.second - sensor.position.second) + 1
                candidate = column to row
                shouldSkip = true
            }

            if (shouldSkip) {
                continue
            }

            if (column <= bounds) {
                foundX = column
                foundY = row
                break
            }
        }
    }

    println(foundX.toLong() * 4_000_000L + foundY.toLong())
}

data class Sensor(
    val position: Pair<Int, Int>,
    val closestBeacon: Pair<Int, Int>,
    val closestDistance: Int = position.manhattan(closestBeacon)
)

fun Pair<Int, Int>.manhattan(rhs: Pair<Int, Int>): Int = abs(this.first - rhs.first) + abs(this.second - rhs.second)