package aoc2022

import java.io.File

val directions = getDirections(File("build/resources/main/day17").readText())

fun main() {
    var currentRock = 0L
    //val target = 2022L
    val target = 1000000000000

    var board: MutableList<Int> = mutableListOf()
    var gasPointer = 0

    var culledCount: Long = 0L

    val debug = false

    while (currentRock < target) {
        val rock = rockPatterns[(currentRock % rockPatterns.size).toInt()]
        val movableRock = mutableRockPatterns[(currentRock % rockPatterns.size).toInt()]
        currentRock += 1

        if (currentRock % 10_000_000L == 0L) {
            println(currentRock)
        }

        // First rock appears - place space underneath it
        repeat(3) { board.add(0b0000000) }

        // Place new rock
        board += rock

        if (debug) {
            board.print()
            Thread.sleep(500)
        }

        var rockStart = board.size - rock.size
        var rockEnd = board.size

        while (true) {
            val direction = directions[gasPointer++ % directions.size]

            // Push rock by gas one
            if (board.checkCanMoveHorizontal(rockStart, rockEnd, movableRock, direction)) {
                board.moveHorizontal(rockStart, rockEnd, movableRock, direction)
            }

            if (debug) {
                board.print()
                Thread.sleep(500)
            }

            // Fall down one
            val canMoveVertical = board.checkCanMoveVertical(rockStart, rockEnd, movableRock)
            if (canMoveVertical) {
                board.moveVertical(rockStart, rockEnd, movableRock)
                rockStart -= 1
                rockEnd -=1
            } else {
                break
            }

            while (board.last() == 0) {
                board.removeLast()
            }

            if (board.size > 10_000_000) {
                val dropped = board.size - 5_000_000
                board = board.takeLast(5_000_000).toMutableList()
                culledCount += dropped
                rockStart -= dropped
                rockEnd -= dropped
            }

            if (debug) {
                board.print()
                Thread.sleep(500)
            }
        }

        if (board.last() == 0b1111111) {
            println("new floor!")
        }

        // We messed around with the movable rock, let's fix it
        rock.forEachIndexed { index, rockRow ->
            movableRock[index] = rockRow
        }

    }

    println(culledCount + board.size)
}

data class CacheKey(
    val directionIndex: Int,
    val currentRockIndex: Int
)

data class CacheValue(
    val directionIndex: Int,
    val currentRockIndex: Int,
    val blockHeight: Long,
    val rockCount: Long
)

private fun getDirections(pattern: String): IntArray = pattern.map { if (it == '>') { 1 } else { -1 } }.toIntArray()

private fun MutableList<Int>.print() = println(this.reversed().map {
    var test = it
    buildString {
        repeat(7) {
            if (test % 2 == 1) {
                append('#')
            } else {
                append('.')
            }
            test /= 2
        }
    }.reversed()
}.joinToString("\n") + "\n")

private fun MutableList<Int>.checkCanMoveHorizontal(
    rockStart: Int, rockEnd: Int, rock: List<Int>, direction: Int
): Boolean {
    // [this] is the board
    rock.forEachIndexed { rockIndex, rockRow ->
        if (!this[rockStart + rockIndex].checkCanMoveHorizontalSingle(rockRow, direction)) {
            return false
        }
    }
    return true
}

private fun Int.checkCanMoveHorizontalSingle(rockRow: Int, direction: Int): Boolean {
    if (rockRow % 2 == 1 && direction == 1) {
        // no room to move to the right
        return false
    }

    if (rockRow and 0b1000000 != 0 && direction == -1) {
        // no room to move to the left
        return false
    }

    // [this] is the board row
    val boardAlone = this xor rockRow
    val shiftedRockRow = if (direction == 1) { rockRow shr 1 } else { rockRow shl 1 }

    if (boardAlone and shiftedRockRow != 0) {
        // overlap between the rock and the board
        return false
    }

    return true
}

private fun MutableList<Int>.moveHorizontal(
    rockStart: Int, rockEnd: Int, rock: MutableList<Int>, direction: Int
) {
    // [this] is the board
    rock.forEachIndexed { rockIndex, rockRow ->
        val boardAlone = this[rockStart + rockIndex] xor rockRow

        // Move the rock template
        rock[rockIndex] = if (direction == -1) { rock[rockIndex] shl 1} else { rock[rockIndex] shr 1 }

        // Update the board
        this[rockStart + rockIndex] = boardAlone or rock[rockIndex]
    }
}

private fun MutableList<Int>.checkCanMoveVertical(rockStart: Int, rockEnd: Int, rock: List<Int>): Boolean {
    // [this] is the board
    if (rockStart == 0) {
        // already at the bottom
        return false
    }

    rock.forEachIndexed { index, rockRow ->
        if (index == 0) {
            // first row of the rock is always moving to a clean place
            val boardToCheck = this[rockStart + index - 1]
            if (boardToCheck and rockRow != 0) {
                // rock would crash into something
                return false
            }
        } else {
            // intermediate rock rows need more attention
            val boardToCheck = this[rockStart + index - 1] xor rock[index - 1]
            if (boardToCheck and rockRow != 0) {
                // rock would crash into something
                return false
            }
        }
    }

    return true
}

private fun MutableList<Int>.moveVertical(rockStart: Int, rockEnd: Int, rock: List<Int>) {
    // [this] is the board
    rock.forEachIndexed { index, rockRow ->
        this[rockStart + index - 1] = this[rockStart + index - 1] or rockRow

        /*if (index == 0) {
            // first row of the rock is always moving to a clean place
            this[rockStart + index - 1] = this[rockStart + index - 1] or rockRow
        } else {
            // intermediate rock rows need more attention
            val boardToCheck = this[rockStart + index - 1] xor rock[index - 1]
            this[rockStart + index - 1] = boardToCheck or rockRow
        }*/

        this[rockStart + index] = this[rockStart + index] xor rockRow
    }
}

private val mutableRockPatterns =  listOf(
    mutableListOf(0b0011110),
    mutableListOf(
        0b0001000,
        0b0011100,
        0b0001000),
    mutableListOf(
        0b0000100,
        0b0000100,
        0b0011100),
    mutableListOf(
        0b0010000,
        0b0010000,
        0b0010000,
        0b0010000),
    mutableListOf(
        0b0011000,
        0b0011000)
).map { it.reversed().toMutableList() }

private val rockPatterns = listOf(
    listOf(0b0011110),
    listOf(
        0b0001000,
        0b0011100,
        0b0001000),
    listOf(
        0b0000100,
        0b0000100,
        0b0011100),
    listOf(
        0b0010000,
        0b0010000,
        0b0010000,
        0b0010000),
    listOf(
        0b0011000,
        0b0011000)
).map { it.reversed() }