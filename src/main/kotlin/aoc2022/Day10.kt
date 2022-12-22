package aoc2022

import java.io.File

fun main() {
    doPart1()
    doPart2()
}

private fun doPart1() {
    var cycle = 0
    var x = 1
    var signalStrengths = 0
    val interestingCycles = setOf(20, 60, 100, 140, 180, 220)
    File("build/resources/main/day10")
        .readLines()
        .map {
            if (it == "noop") {
                cycle += 1
                if (interestingCycles.contains(cycle)) {
                    signalStrengths += cycle * x
                }
                return@map
            }

            if (it.startsWith("addx")) {
                val toAdd = it.substring(5).toInt()
                cycle += 1
                if (interestingCycles.contains(cycle)) {
                    signalStrengths += cycle * x
                }
                cycle += 1
                if (interestingCycles.contains(cycle)) {
                    signalStrengths += cycle * x
                }
                x += toAdd
            }
        }

    println(signalStrengths)
}

private fun doPart2() {
    val instructions = File("build/resources/main/day10").readLines().map { Instruction.parse(it) }

    val computer = Computer()
    computer.executeInstructions(instructions)
    println(computer.display)
}

sealed interface Instruction {
    companion object {
        fun parse(value: String): Instruction = when {
            value == "noop" -> Noop()
            else -> AddX(offset = value.substring(5).toInt())
        }
    }
}

class Noop: Instruction

data class AddX(val offset: Int): Instruction

val SCREEN_WIDTH = 40
val SCREEN_HEIGHT = 6

data class CrtDisplay(val pixels: List<String> = buildList { repeat(SCREEN_HEIGHT) { add(".".repeat(SCREEN_WIDTH)) } }) {

    fun drawSprite(cycle: Int, x: Int): CrtDisplay {
        val scanX = (cycle - 1) % SCREEN_WIDTH
        val scanY = (cycle - 1) / SCREEN_WIDTH

        val litPixels = listOf(x - 1, x, x + 1)

        if (scanX in litPixels) {
            // draw the pixel
            return copy(
                pixels = buildList {
                    pixels.forEachIndexed { index, row ->
                        val newRow = buildString {
                            row.forEachIndexed { index, pixel ->
                                if (index == scanX) {
                                    append('#')
                                } else {
                                    append(pixel)
                                }
                            }
                        }
                        if (index == scanY) {
                            add(newRow)
                        } else {
                            add(row)
                        }
                    }
                }
            )
        }

        return this
    }

    override fun toString(): String = pixels.joinToString("\n")
}

data class Computer(
    var cycle: Int = 1,
    var x: Int = 1,
    var display: CrtDisplay = CrtDisplay()
) {

    fun executeInstructions(instructions: List<Instruction>) {
        instructions.forEach { executeInstruction(it) }
    }

    private fun executeInstruction(instruction: Instruction) {
        display = display.drawSprite(cycle, x)

        if (instruction is Noop) {
            cycle += 1
            return
        }

        if (instruction is AddX) {
            cycle += 1

            display = display.drawSprite(cycle, x)

            cycle += 1
            x += instruction.offset
            return
        }
    }
}