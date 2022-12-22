package aoc2022

import java.io.File

fun main() {
    val forest = File("build/resources/main/day8").readLines()

    val rowCount = forest.size
    val colCount = forest[0].length

    // part 1
    var visibleTreeCount = 0
    forest.forEachIndexed { ri, row ->
        row.forEachIndexed { ci, tree ->
            // look left
            val tallestLeft = ((0 until ci).maxOfOrNull { c -> "${forest[ri][c]}".toInt() } ?: -1) < "$tree".toInt()

            // look right
            val tallestRight = ((ci + 1 until colCount).maxOfOrNull { c -> "${forest[ri][c]}".toInt() } ?: -1) < "$tree".toInt()

            // look up
            val tallestUp = ((0 until ri).maxOfOrNull { r -> "${forest[r][ci]}".toInt() } ?: -1) < "$tree".toInt()

            // look down
            val tallestDown = ((ri + 1 until rowCount).maxOfOrNull { r -> "${forest[r][ci]}".toInt() } ?: -1) < "$tree".toInt()

            if (tallestLeft || tallestRight || tallestUp || tallestDown) {
                visibleTreeCount += 1
            }
        }
    }
    println(visibleTreeCount)

    // part 2
    var maxScore = 0
    forest.forEachIndexed { ri, row ->
        row.forEachIndexed { ci, tree ->
            val treeInt = "$tree".toInt()
            // score left
            var outerMarker = true
            val leftScore = (0 until ci).map { c -> "${forest[ri][c]}".toInt() }.reversed().takeWhile {
                val result = outerMarker

                if (it >= treeInt) {
                    outerMarker = false
                }
                result
            }.size

            // score right
            outerMarker = true
            val rightScore = (ci + 1 until colCount).map { c -> "${forest[ri][c]}".toInt() }.takeWhile {
                val result = outerMarker

                if (it >= treeInt) {
                    outerMarker = false
                }
                result
            }.size

            // score top
            outerMarker = true
            val topScore = (0 until ri).map { r -> "${forest[r][ci]}".toInt() }.reversed().takeWhile {
                val result = outerMarker

                if (it >= treeInt) {
                    outerMarker = false
                }
                result
            }.size

            // score bottom
            outerMarker = true
            val bottomScore = (ri + 1 until rowCount).map { r -> "${forest[r][ci]}".toInt() }.takeWhile {
                val result = outerMarker

                if (it >= treeInt) {
                    outerMarker = false
                }
                result
            }.size

            val totalScore = leftScore * rightScore * topScore * bottomScore
            if (totalScore > maxScore) {
                maxScore = totalScore
            }
        }
    }
    println(maxScore)
}