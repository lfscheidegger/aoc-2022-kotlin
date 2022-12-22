package aoc2022

import java.math.BigInteger

fun main() {
    val monkeys = buildMap {
        REAL_INPUT_MONKEYS.forEach { monkey -> put(monkey.id, monkey) }
    }

    val normalizer = monkeys.values.map { it.testDivisor }.reduce { acc, integer -> acc * integer }

    repeat(10000) {
        monkeys.forEach { key, monkey ->
            monkey.doRound(monkeys.getValue(monkey.trueThrowId), monkeys.getValue(monkey.falseThrowId), normalizer)
        }
    }

    println(monkeys.values.sortedByDescending { it.inspectedCount }.take(2).map { it.inspectedCount }.reduce { acc, i -> acc * i })
}

val REAL_INPUT_MONKEYS = listOf(
    Monkey(
        id = 0,
        items = mutableListOf(98L, 70L, 75L, 80L, 84L, 89L, 55L, 98L).map { BigInteger.valueOf(it) }.toMutableList(),
        operation = { it * BigInteger.valueOf(2L) },
        testDivisor = BigInteger.valueOf(11L),
        trueThrowId = 1,
        falseThrowId = 4
    ),
    Monkey(
        id = 1,
        items = mutableListOf(59L).map { BigInteger.valueOf(it) }.toMutableList(),
        operation = { it * it },
        testDivisor = BigInteger.valueOf(19L),
        trueThrowId = 7,
        falseThrowId = 3
    ),
    Monkey(
        id = 2,
        items = mutableListOf(77L, 95L, 54L, 65L, 89L).map { BigInteger.valueOf(it) }.toMutableList(),
        operation = { it + BigInteger.valueOf(6L) },
        testDivisor = BigInteger.valueOf(7L),
        trueThrowId = 0,
        falseThrowId = 5
    ),
    Monkey(
        id = 3,
        items = mutableListOf(71L, 64L, 75L).map { BigInteger.valueOf(it) }.toMutableList(),
        operation = { it + BigInteger.valueOf(2L) },
        testDivisor = BigInteger.valueOf(17L),
        trueThrowId = 6,
        falseThrowId = 2
    ),
    Monkey(
        id = 4,
        items = mutableListOf(74L, 55L, 87L, 98L).map { BigInteger.valueOf(it) }.toMutableList(),
        operation = { it * BigInteger.valueOf(11L) },
        testDivisor = BigInteger.valueOf(3L),
        trueThrowId = 1,
        falseThrowId = 7
    ),
    Monkey(
        id = 5,
        items = mutableListOf(90L, 98L, 85L, 52L, 91L, 60L).map { BigInteger.valueOf(it) }.toMutableList(),
        operation = { it + BigInteger.valueOf(7L) },
        testDivisor = BigInteger.valueOf(5L),
        trueThrowId = 0,
        falseThrowId = 4
    ),
    Monkey(
        id = 6,
        items = mutableListOf(99L, 51L).map { BigInteger.valueOf(it) }.toMutableList(),
        operation = { it + BigInteger.valueOf(1L) },
        testDivisor = BigInteger.valueOf(13L),
        trueThrowId = 5,
        falseThrowId = 2
    ),
    Monkey(
        id = 7,
        items = mutableListOf(98L, 94L, 59L, 76L, 51L, 65L, 75L).map { BigInteger.valueOf(it) }.toMutableList(),
        operation = { it + BigInteger.valueOf(5L) },
        testDivisor = BigInteger.valueOf(2L),
        trueThrowId = 3,
        falseThrowId = 6
    ),
)

val MINI_INPUT_MONKEYS = listOf(
    Monkey(
        id = 0,
        items = mutableListOf(79L, 98L).map { BigInteger.valueOf(it) }.toMutableList(),
        operation = { it * BigInteger.valueOf(19L) },
        testDivisor = BigInteger.valueOf(23L),
        trueThrowId = 2,
        falseThrowId = 3),
    Monkey(
        id = 1,
        items = mutableListOf(54L, 65L, 75L, 74L).map { BigInteger.valueOf(it) }.toMutableList(),
        operation = { it + BigInteger.valueOf(6L) },
        testDivisor = BigInteger.valueOf(19L),
        trueThrowId = 2,
        falseThrowId = 0),
    Monkey(
        id = 2,
        items = mutableListOf(79L, 60L, 97L).map { BigInteger.valueOf(it) }.toMutableList(),
        operation = { it * it },
        testDivisor = BigInteger.valueOf(13L),
        trueThrowId = 1,
        falseThrowId = 3),
    Monkey(
        id = 3,
        items = mutableListOf(74L).map { BigInteger.valueOf(it) }.toMutableList(),
        operation = { it + BigInteger.valueOf(3L) },
        testDivisor = BigInteger.valueOf(17L),
        trueThrowId = 0,
        falseThrowId = 1))

data class Monkey(
    val id: Long,
    val items: MutableList<BigInteger>,
    val operation: (BigInteger) -> BigInteger,
    val testDivisor: BigInteger,
    val trueThrowId: Long,
    val falseThrowId: Long,
    var inspectedCount: Long = 0) {

    fun doRound(trueTarget: Monkey, falseTarget: Monkey, normalizer: BigInteger) {
        items.forEach { item ->
            val toTest = operation(item) % normalizer
            if (toTest % testDivisor == BigInteger.ZERO) { trueTarget.items.add(toTest) } else { falseTarget.items.add(toTest) }
        }

        inspectedCount += items.size
        items.clear()
    }
}