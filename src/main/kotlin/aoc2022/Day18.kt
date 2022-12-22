package aoc2022

import java.io.File

fun main() {
    val offsets = listOf(
        Triple(1, 0, 0),
        Triple(-1, 0, 0),
        Triple(0, 1, 0),
        Triple(0, -1, 0),
        Triple(0, 0, 1),
        Triple(0, 0, -1),
    )

    val voxels = File("build/resources/main/day18")
        .readLines()
        .map { line ->
            val (x, y, z) = line.split(",").map { coord -> coord .toInt() }

            Triple(x, y, z)
        }

    val voxelsByLocation = buildMap {
        voxels.mapIndexed { index, voxel -> put(voxel, index) }
    }


    // Part 1
    println(voxels.map { voxel ->
        val neigbhorCandidates = offsets.map { offset ->
            Triple(voxel.first + offset.first, voxel.second + offset.second, voxel.third + offset.third)
        }

        6 - neigbhorCandidates.filter { voxelsByLocation.contains(it) }.size
    }.sum())

    // Part2
    val minX = voxels.minBy { it.first }.first -1
    val minY = voxels.minBy { it.second }.second -1
    val minZ = voxels.minBy { it.third }.third -1

    val maxX = voxels.maxBy { it.first }.first +1
    val maxY = voxels.maxBy { it.second }.second +1
    val maxZ = voxels.maxBy { it.third }.third +1

    val outside = getOutside(voxelsByLocation, minX, maxX, minY, maxY, minZ, maxZ)

    var result = 0
    (minX .. maxX).forEach { x ->
        (minY .. maxY).forEach { y ->
            var z = minZ
            while (z <= maxZ) {
                val current = Triple(x, y, z)
                val next = Triple(x, y, z + 1)

                if (!voxelsByLocation.contains(current) && voxelsByLocation.contains(next) && outside.contains(current)) {
                    result += 1
                }

                if (voxelsByLocation.contains(current) && !voxelsByLocation.contains(next) && outside.contains(next)) {
                    result += 1
                }
                z += 1
            }
        }
    }

    (minX .. maxX).forEach { x ->
        (minZ .. maxZ).forEach { z ->
            var y = minY
            while (y <= maxY) {
                val current = Triple(x, y, z)
                val next = Triple(x, y + 1, z)

                if (!voxelsByLocation.contains(current) && voxelsByLocation.contains(next) && outside.contains(current)) {
                    result += 1
                }

                if (voxelsByLocation.contains(current) && !voxelsByLocation.contains(next) && outside.contains(next)) {
                    result += 1
                }
                y += 1
            }
        }
    }

    (minY .. maxY).forEach { y ->
        (minZ .. maxZ).forEach { z ->
            var x = minX
            while (x <= maxX) {
                val current = Triple(x, y, z)
                val next = Triple(x + 1, y, z)

                if (!voxelsByLocation.contains(current) && voxelsByLocation.contains(next) && outside.contains(current)) {
                    result += 1
                }

                if (voxelsByLocation.contains(current) && !voxelsByLocation.contains(next) && outside.contains(next)) {
                    result += 1
                }
                x += 1
            }
        }
    }

    println(result)
}


private fun getOutside(
    voxels: Map<Triple<Int, Int, Int>, Int>,
    minX: Int,
    maxX: Int,
    minY: Int,
    maxY: Int,
    minZ: Int,
    maxZ: Int,
): Set<Triple<Int, Int, Int>> {
    val offsets = listOf(
        Triple(1, 0, 0),
        Triple(-1, 0, 0),
        Triple(0, 1, 0),
        Triple(0, -1, 0),
        Triple(0, 0, 1),
        Triple(0, 0, -1),
    )

    val result: MutableSet<Triple<Int, Int, Int>> = mutableSetOf()
    val dequeSet: MutableSet<Triple<Int, Int, Int>> = mutableSetOf()
    val deque: ArrayDeque<Triple<Int, Int, Int>> = ArrayDeque(listOf(Triple(minX, minY, minZ)))
    while (deque.isNotEmpty()) {
        val head = deque.removeFirst()

        offsets.forEach { offset ->
            val candidate = Triple(head.first + offset.first, head.second + offset.second,head.third + offset.third)
            if (candidate.first >= minX && candidate.first <= maxX &&
                candidate.second >= minY && candidate.second <= maxY &&
                candidate.third >= minZ && candidate.third <= maxZ &&
                !dequeSet.contains(candidate) &&
                !voxels.contains(candidate) &&
                !result.contains(candidate)) {
                deque += candidate
                dequeSet += candidate
            }
        }

        result += head
    }

    return result
}
