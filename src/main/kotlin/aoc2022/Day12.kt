package aoc2022

import java.io.File

fun main() {
    val mountains = File("build/resources/main/day12").readLines()

    lateinit var startingPosition: Tile
    lateinit var endingPosition: Tile
    mountains.forEachIndexed { rowIdx, row ->
        row.forEachIndexed { columnIdx, tile ->
            if (tile == 'S') {
                startingPosition = Tile(
                    position = rowIdx to columnIdx,
                    height = tile.height,
                    tile = tile)
            }

            if (tile == 'E') {
                endingPosition = Tile(
                    position = rowIdx to columnIdx,
                    height = tile.height,
                    tile = tile)
            }
        }
    }

    // Part 1
    val nSteps = shortestPathStartingAt(startingPosition, endingPosition, mountains)
    println(nSteps)

    // Part 2
    val startingPositions = buildList {
        mountains.forEachIndexed { rowIdx, row ->
            row.forEachIndexed { columnIdx, tile ->
                if (tile in listOf('S', 'a')) {
                    add(Tile(
                        position = rowIdx to columnIdx,
                        height = tile.height,
                        tile = tile))
                }
            }
        }
    }

    println(startingPositions.map { shortestPathStartingAt(it, endingPosition, mountains) }.filter { it != 0 }.min())
}

private fun shortestPathStartingAt(startingPosition: Tile, endingPosition: Tile, mountains: List<String>): Int {
    val visited: MutableSet<Tile> = mutableSetOf(startingPosition)
    val deque: ArrayDeque<Tile> = ArrayDeque(listOf(startingPosition))
    val previousMap: MutableMap<Tile, Tile> = mutableMapOf()

    while (!deque.isEmpty()) {
        val current = deque.removeFirst()
        val neighbors = current.getNeighbors(mountains).filter { !visited.contains(it) }
        neighbors.forEach { previousMap[it] = current }
        deque += neighbors
        visited += neighbors

        if (current == endingPosition) {
            break
        }
    }

    var nSteps = 0
    var current = previousMap[endingPosition]
    while (current != null) {
        nSteps += 1
        current = previousMap[current]
    }

    return nSteps
}

data class Tile(
    val position: Pair<Int, Int>,
    val height: Int,
    val tile: Char) {

    fun getNeighbors(mountains: List<String>): List<Tile> {
        val left = if (position.second > 0) {
            val tile = mountains[position.first][position.second - 1]
            Tile(
                position = position.first to position.second - 1,
                height = tile.height,
                tile = tile)
        } else null

        val right = if (position.second < mountains[0].length - 1) {
            val tile = mountains[position.first][position.second + 1]
            Tile(
                position = position.first to position.second + 1,
                height = tile.height,
                tile = tile)
        } else null

        val top = if (position.first > 0) {
            val tile = mountains[position.first - 1][position.second]
            Tile(
                position = position.first - 1 to position.second,
                height = tile.height,
                tile = tile)
        } else null

        val bottom = if (position.first < mountains.size - 1) {
            val tile = mountains[position.first + 1][position.second ]
            Tile(
                position = position.first + 1 to position.second,
                height = tile.height,
                tile = tile)
        } else null

        return listOfNotNull(left, right, top, bottom).filter { this.height - it.height >= -1 }
    }
}

val Char.height: Int get() = when(this) {
    'S' -> 0
    'E' -> 'z'.code - 'a'.code
    else -> this.code - 'a'.code
}