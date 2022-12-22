package aoc2022

import java.io.File

fun main() {
    val (starting, movesText) = File("build/resources/main/day5")
        .readText().split(" 1   2   3")//   4   5   6   7   8   9")

    val stacks = parseStartingStacks(starting, 9)
    val moves = parseMoves(movesText)

    //println(moves)

    var mutStacks = stacks
    moves.forEach { move ->
        mutStacks = doMove(mutStacks, move)
    }

    println(mutStacks.map { it.last() }.joinToString(""))

    //println(mutStacks)
}

private fun parseStartingStacks(starting: String, count: Int): List<List<String>> {
    val byRows = starting
        .split("\n")
        .map { row ->
            row.chunked(4).map { chunk -> chunk.substring(1, 2).let { if (it == "."){ " "} else { it } } }
        }

    //println(byRows)

    val result = mutableListOf<List<String>>()
    (0..8).forEach { column ->
        val newColumn = mutableListOf<String>()
        (0..7).forEach { row ->
            newColumn.add(byRows[row][column])
        }
        result.add(newColumn.reversed().filter { it != " "})
    }

    return result
}

private fun parseMoves(moves: String): List<Triple<Int, Int, Int>> {
    val re = Regex("""move\s+(\d+)\s+from\s+(\d+)\s+to\s+(\d+)""")
    return moves.split("\n")
        .drop(1)
        .map { line ->
        val result = re.matchEntire(line) ?: error("")
        Triple(result.groupValues[1].toInt(), result.groupValues[2].toInt(), result.groupValues[3].toInt())
    }
}

private fun doMove(stacks: List<List<String>>, move: Triple<Int, Int, Int>): List<List<String>> {
    val (count, from, to) = move

    val boxesToMove = stacks[from-1].reversed().subList(0, count).reversed()

    // var mutStacks = stacks

    val result = buildList {
        stacks.forEachIndexed { index, stack ->
            add(when {
                index == from - 1 -> stack.dropLast(count)
                index == to - 1 -> stack + boxesToMove
                else -> stack
            })
        }
    }

    /*repeat(count) {
        mutStacks = doSingleMove(mutStacks, from, to)
    }*/

    return result
    //return mutStacks
}

private fun doSingleMove(stacks: List<List<String>>, from: Int, to: Int): List<List<String>> {
    val boxToMove = stacks[from-1].last()
    val result = buildList {
        stacks.forEachIndexed { index, stack ->
            add(when {
                index == from - 1 -> stack.dropLast(1)
                index == to - 1 -> stack + listOf(boxToMove)
                else -> stack
            })
        }
    }

    return result
}