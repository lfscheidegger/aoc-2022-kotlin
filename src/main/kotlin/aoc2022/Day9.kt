package aoc2022

import java.io.File
import kotlin.math.abs

fun main() {
    val moves = File("build/resources/main/day9")
        .readLines()
        .map {
            val (move, count) = it.split(" ")

            move to count.toInt()
        }

    var rope = Rope(headPosition = 0 to 0, tailPosition = 0 to 0)
    moves.forEach { move ->
        rope = rope.advance(move.second, move.first)

    }

    println(rope.tailVisited.size)

    var rope2 = Rope2()
    moves.forEach { move ->
        rope2 = rope2.advance(move.second, move.first)
    }

    println(rope2.ropeSegments.last().tailVisited.size)
}

data class Rope(
    val headPosition: Pair<Int, Int> = 0 to 0,
    val tailPosition: Pair<Int, Int> = 0 to 0,
    val tailVisited: Set<Pair<Int, Int>> = setOf(tailPosition)) {

    fun advance(count: Int, move: String): Rope {
        var result: Rope = this
        repeat(count) {
            result = result.advanceOne(move)
        }

        return result
    }

    fun advanceOne(move: String): Rope {
        val newHeadPosition = when (move) {
            "R" -> headPosition.first + 1 to headPosition.second
            "L" -> headPosition.first - 1 to headPosition.second
            "U" -> headPosition.first to headPosition.second + 1
            "D" -> headPosition.first to headPosition.second - 1
            "RU" -> headPosition.first + 1 to headPosition.second + 1
            "RD" -> headPosition.first + 1 to headPosition.second - 1
            "LU" -> headPosition.first - 1 to headPosition.second + 1
            "LD" -> headPosition.first - 1 to headPosition.second - 1
            else -> error("bad move $move")
        }

        lateinit var newTailPosition: Pair<Int, Int>
        if (abs(newHeadPosition.first - tailPosition.first) == 2 && newHeadPosition.second == tailPosition.second) {
            // horizontal tail move
            newTailPosition = tailPosition.first + (newHeadPosition.first - headPosition.first) to tailPosition.second
        } else if (abs(newHeadPosition.second - tailPosition.second) == 2 && newHeadPosition.first == tailPosition.first) {
            // vertical tail move
            newTailPosition = tailPosition.first to tailPosition.second + (newHeadPosition.second - headPosition.second)
        } else if (newHeadPosition == tailPosition) {
            // tail doesn't need to move
            newTailPosition = tailPosition
        } else if (newHeadPosition.isAdjacent(tailPosition)) {
            // tail doesn't need to move
            newTailPosition = tailPosition
        } else {
            // diagonal tail position to keep up
            val horizontalMove = if (newHeadPosition.first - tailPosition.first > 0) { 1 } else { -1 }
            val verticalMove = if (newHeadPosition.second - tailPosition.second > 0) { 1 } else { -1 }
            newTailPosition = tailPosition.first + horizontalMove to tailPosition.second + verticalMove
        }

        return copy(headPosition = newHeadPosition, tailPosition = newTailPosition, tailVisited = tailVisited.plus(newTailPosition))
    }
}

data class Rope2(
    val ropeSegments: List<Rope> = buildList { repeat(9) { add(Rope()) } }
) {

    fun advance(count: Int, move: String): Rope2 {
        var result: Rope2 = this
        repeat(count) {
            result = result.advanceOne(move)

        }

        return result
    }

    fun advanceOne(move: String): Rope2 {
        val newHead = ropeSegments.first().advanceOne(move)
        val tail = ropeSegments.drop(1)

        if (tail.isEmpty()) {
            return Rope2(ropeSegments = listOf(newHead))
        }

        val innerMove = getMoves(tail[0].headPosition, newHead.tailPosition)
        var movedRope = Rope2(tail)
        innerMove?.let {
            movedRope = movedRope.advanceOne(it)
        }

        return Rope2(ropeSegments = listOf(newHead) + movedRope.ropeSegments)
    }
}

private fun Pair<Int,Int>.isDiagonal(other: Pair<Int, Int>): Boolean = abs(this.first - other.first) == 1 && abs(this.second - other.second) == 1
private fun Pair<Int, Int>.isAdjacent(other: Pair<Int, Int>): Boolean =
    (abs(this.first - other.first) == 1 && this.second == other.second) ||
    (abs(this.second - other.second) == 1 && this.first == other.first) || this.isDiagonal(other)

private fun getMoves(from: Pair<Int, Int>, to: Pair<Int, Int>): String? {
    if (from == to) {
        return null
    }

    if (to.isDiagonal(from)) {
        val horizontalMove = if(to.first - from.first == 1) { "R" } else { "L" }
        val verticalMove = if (to.second - from.second == 1) { "U" } else { "D" }
        return "$horizontalMove$verticalMove"
    }

    if (to.first - from.first == 1) {
        return "R"
    }

    if (to.first - from.first == -1) {
        return "L"
    }

    if (to.second - from.second == 1) {
        return "U"
    }

    if (to.second - from.second == -1) {
        return "D"
    }

    error("Bad getMove: $from, $to")
}

