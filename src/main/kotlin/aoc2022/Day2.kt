package aoc2022

import java.io.File

fun main() {
    Play.init()

    val plays = File("build/resources/main/day2")
        .readText()
        .split("\n")
        .map { token ->
            val (opponent, me) = token.split(" ").map { it.trim() }

            decodePlay(opponent) to decodePlay(me)
        }

    println(plays.sumOf { (opponent, me) -> me.score(opponent) })
    println(plays.sumOf { (opponent, me) -> me.mapPlay(opponent).score(opponent) })
}

fun decodePlay(encodedPlay: String): Play = when (encodedPlay) {
    "A", "X" -> Play.ROCK
    "B", "Y" -> Play.PAPER
    "C", "Z" -> Play.SCISSORS
    else -> error("")
}

enum class Play(val score: Int) {
    ROCK(1),
    PAPER(2),
    SCISSORS(3);

    lateinit var beats: Play

    companion object {
        fun init() {
            ROCK.beats = SCISSORS
            PAPER.beats = ROCK
            SCISSORS.beats = PAPER
        }
    }

    fun score(other: Play): Int = this.score + when(other) {
        this -> 3
        this.beats -> 6
        else -> 0
    }

    fun mapPlay(other: Play): Play = when(this) {
        ROCK -> other.beats
        PAPER -> other
        SCISSORS -> Play.values().first { it.beats == other }
    }
}