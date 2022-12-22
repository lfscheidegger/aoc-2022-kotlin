package aoc2022

import com.google.common.collect.Sets
import java.io.File
import java.lang.Integer.max
import java.lang.Integer.min

fun main() {
    // Part 1
    val state = State.parse()
    println(state.bestScoreForValves("AA", state.usefulValves.keys - setOf("AA"), 30))

    // Part 2
    val allCombinations =
        Sets.powerSet(state.usefulValves.keys).associateWith { subCombination ->
            val smallerState = state.copy(
                usefulValves = state.usefulValves.filterKeys { subCombination.contains(it) })
            smallerState.bestScoreForValves("AA", smallerState.usefulValves.keys - setOf("AA"), 26)
        }

    println(allCombinations.map { (key, best) ->
        val otherKey = state.usefulValves.keys - key

        best + allCombinations.getValue(otherKey)
    }.max())
}

data class State(
    val valves: Map<String, Valve>,
    val usefulValves: Map<String, Valve> = valves.filter { it.value.flowRate > 0 || it.key == "AA" },
    val distances: Map<Pair<String, String>, Int> = floydWarshall(valves)
) {

    companion object {
        fun parse(): State {
            val REGEX = Regex("""Valve (\w+) has flow rate=(\d+); tunnels? leads? to valves? (\w+(,\s+\w+)*)""")

            val valves = File("build/resources/main/day16")
                .readLines()
                .map { line ->
                    val groupValues = REGEX.matchEntire(line)?.groupValues ?: error("no match")
                    Valve(
                        id = groupValues[1],
                        flowRate = groupValues[2].toInt(),
                        connections = groupValues[3].split(", ")
                    )
                }
                .associateBy { it.id }

            return State(valves = valves)
        }

        private fun floydWarshall(valves: Map<String, Valve>): Map<Pair<String, String>, Int> {
            val dist: MutableMap<Pair<String, String>, Int> = mutableMapOf()

            val costs = buildMap {
                valves.values.forEach { valve ->
                    put(valve.id, valve.connections.associateWith { 1 })
                }
            }

            costs.keys.forEach { outer ->
                costs.keys.forEach { inner ->
                    dist[outer to inner] = Int.MAX_VALUE / 3
                }
            }

            costs.forEach { (source, destinations) ->
                destinations.forEach { (destination, cost) ->
                    dist[source to destination] = cost
                }
            }

            costs.forEach { (source, _) ->
                dist[source to source] = 0
            }

            val nodes = costs.keys

            nodes.forEach { k ->
                nodes.forEach { i ->
                    nodes.forEach { j ->
                        if (dist[i to j]!! > dist[i to k]!! + dist[k to j]!!) {
                            dist[i to j] = dist[i to k]!! + dist[k to j]!!
                        }
                    }
                }
            }

            val result: MutableMap<String, MutableMap<String, Int>> = mutableMapOf()
            dist.forEach {
                if (result.contains(it.key.first)) {
                    result[it.key.first]!![it.key.second] = it.value
                } else {
                    result[it.key.first] = mutableMapOf(it.key.second to it.value)
                }
            }

            return dist.toMap()
        }
    }

    private val cache: MutableMap<CacheKey, Int> = mutableMapOf()

    fun bestScoreForValves(
        head: String,
        valvesLeft: Set<String>,
        timeRemaining: Int
    ): Int {
        val key = CacheKey(head, valvesLeft, timeRemaining)
        cache[key]?.let { return it }

        if (timeRemaining <= 0) {
            val result = 0
            cache[key] = result
            return result
        }

        if (valvesLeft.isEmpty()) {
            // No more valves left to open
            val result = usefulValves.values.sumOf { it.flowRate * timeRemaining }
            cache[key] = result
            return result
        }

        var max = 0
        valvesLeft.forEach { target ->
            val distance = distances.getValue(head to target)
            val openValves = usefulValves.keys - valvesLeft
            val score = openValves.sumOf { usefulValves.getValue(it).flowRate * min(timeRemaining, distance + 1) }

            max = max(max, score + bestScoreForValves(target, valvesLeft - setOf(target), timeRemaining - distance - 1))
        }
        cache[key] = max
        return max
    }

    data class CacheKey(
        val head: String,
        val valvesLeft: Set<String>,
        val timeRemaining: Int
    )
}

data class Valve(
    val id: String,
    val flowRate: Int,
    val connections: List<String>
)