package aoc2022

import java.io.File

fun main() {
    var barriers = parseBarriers()
    //printBarriers(barriers)
    //return
    // Part 1
    var grainCounter = 0
    try {
        while (true) {
            barriers = simulateGrainPart1(barriers)
            grainCounter += 1
        }
    } catch (ex: IllegalStateException) {
        println(grainCounter)
    }

    // Part 2
    val mBarriers = parseBarriers().toMutableSet()
    grainCounter = 1
    val minRow = barriers.minBy { it.first }.first
    val maxRow = barriers.maxBy { it.first }.first
    try {
        val firstBarrier: MutableMap<Int, Int> = buildMap {
            barriers.groupBy { it.second }.forEach { column, rows -> put(column, rows.minBy { it.first }.first) }
        }.toMutableMap()
        var barriersSize = mBarriers.size
        simulateGrainPart2(mBarriers, firstBarrier, minRow, maxRow)
        var newBarriersSize = mBarriers.size
        //var newBarriers = simulateGrainPart2(barriers, minRow, maxRow)
        while (barriersSize != newBarriersSize) {
            barriersSize = newBarriersSize
            simulateGrainPart2(mBarriers, firstBarrier, minRow, maxRow)
            newBarriersSize = mBarriers.size
            grainCounter += 1

            if (grainCounter % 1000 == 0) {
                printBarriers(mBarriers)
            }
        }
    } catch (ex: IllegalStateException) {
        println(grainCounter)
    }

    printBarriers(mBarriers)
    println(grainCounter)
}

private fun parseBarriers(): Set<Pair<Int, Int>> = File("build/resources/main/day14")
    .readLines()
    .flatMap { line ->
        buildSet {
            line
                .split("->")
                .windowed(2, 1)
                .forEach { (startString, endString) ->
                    val start = startString.toPoint()
                    val end = endString.toPoint()

                    if (start == end) {
                        add(start.second to start.first)
                    } else if (start.first == end.first) {
                        (start.second doubleRange  end.second).forEach { second ->
                            add(second to start.first)
                        }
                    } else if (start.second == end.second) {
                        (start.first doubleRange end.first).forEach { first ->
                            add(start.second to first)
                        }
                    }
                }
        }
    }.toSet().plus(0 to 500)

private fun printBarriers(barriers: Set<Pair<Int, Int>>): List<String> {
    val minRow = barriers.minBy { it.first }.first
    val maxRow = barriers.maxBy { it.first }.first
    val minCol = barriers.minBy { it.second }.second
    val maxCol = barriers.maxBy { it.second }.second


    return buildList {
        (minRow .. maxRow).forEach { row ->
            val rowString =(minCol .. maxCol).map { col ->
                if (row to col == 0 to 500) {
                    '+'
                } else if (barriers.contains(row to col)) {
                    "#"
                } else {
                    "."
                }
            }.joinToString("")
            println(rowString)
            add(rowString)
        }
    }
}

private fun String.toPoint(): Pair<Int, Int> = this.strip().split(",").let {
    check(it.size == 2)
    it.first().toInt() to it.last().toInt()
}

private infix fun Int.doubleRange(other: Int) =
    if (this <= other) this .. other
    else other .. this


private fun simulateGrainPart1(barriers: Set<Pair<Int, Int>>): Set<Pair<Int, Int>> {
    val minRow = barriers.minBy { it.first }.first
    val maxRow = barriers.maxBy { it.first }.first
    val minCol = barriers.minBy { it.second }.second
    val maxCol = barriers.maxBy { it.second }.second

    var grainLocation = 0 to 500
    var nextGrainLocation = nextLocationPart1(grainLocation, barriers)
    while (grainLocation != nextGrainLocation) {
        grainLocation = nextGrainLocation
        nextGrainLocation = nextLocationPart1(grainLocation, barriers)

        if (!(minRow .. maxRow).contains(nextGrainLocation.first)) {
            throw IllegalStateException("Done")
        }

        if (!(minCol .. maxCol).contains(nextGrainLocation.second)) {
            throw IllegalStateException("Done")
        }
    }

    return barriers.plus(grainLocation)
}

private fun simulateGrainPart2(
    barriers: MutableSet<Pair<Int, Int>>,
    firstBarrier: MutableMap<Int, Int>,
    minRow: Int,
    maxRow: Int): MutableSet<Pair<Int, Int>> {
    var grainLocation = 0 to 500
    var nextGrainLocation = nextLocationPart2(grainLocation, barriers, firstBarrier, maxRow)
    while (grainLocation != nextGrainLocation) {
        grainLocation = nextGrainLocation
        nextGrainLocation = nextLocationPart2(grainLocation, barriers, firstBarrier, maxRow)

        if (nextGrainLocation == 0 to 500) {
            throw IllegalStateException("Done")
        }

        if (!(minRow .. maxRow + 2).contains(nextGrainLocation.first)) {
            nextGrainLocation = grainLocation
        }
    }

    barriers.add(grainLocation)
    firstBarrier[grainLocation.second] = grainLocation.first
    return barriers
    //return barriers.plus(grainLocation)
}

private fun nextLocationPart1(grainLocation: Pair<Int, Int>, barriers: Set<Pair<Int, Int>>): Pair<Int, Int> {
    var candidate = grainLocation.first + 1 to grainLocation.second
    if (!barriers.contains(candidate)) {
        // Room to fall below
        return candidate
    }

    candidate = grainLocation.first + 1 to grainLocation.second - 1
    if (!barriers.contains(candidate)) {
        // Room to fall to the left
        return candidate
    }

    candidate = grainLocation.first + 1 to grainLocation.second + 1
    if (!barriers.contains(candidate)) {
        // Room to fall to the right
        return candidate
    }

    // No room anywhere
    return grainLocation
}

private fun nextLocationPart2(
    grainLocation: Pair<Int, Int>,
    barriers: Set<Pair<Int, Int>>,
    firstBarrier: MutableMap<Int, Int>,
    maxRow: Int
): Pair<Int, Int> {
    val barriersWithFloor = barriers.plus(listOf(
        maxRow + 2 to grainLocation.second - 1,
        maxRow + 2 to grainLocation.second,
        maxRow + 2 to grainLocation.second + 1
    ))

    val candidateRow = (firstBarrier[grainLocation.second] ?: (maxRow + 2)) - 1
    //val candidateRow = (firstBarrier[grainLocation.second] ?: maxRow) + 1

    var candidate = grainLocation.first + 1 to grainLocation.second
    if (!barriersWithFloor.contains(candidate)) {
        // Room to fall below
        return nextLocationPart2(candidate, barriers, firstBarrier, maxRow)
        //return candidate
    }

    candidate = grainLocation.first + 1 to grainLocation.second - 1
    if (!barriersWithFloor.contains(candidate)) {
        // Room to fall to the left
        return nextLocationPart2(candidate, barriers, firstBarrier, maxRow)
    }

    candidate = grainLocation.first + 1 to grainLocation.second + 1
    if (!barriersWithFloor.contains(candidate)) {
        // Room to fall to the right
        return nextLocationPart2(candidate, barriers, firstBarrier, maxRow)
    }

    // No room anywhere
    return grainLocation
}