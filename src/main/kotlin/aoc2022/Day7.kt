package aoc2022

import java.io.File

sealed class Node {
    abstract fun computeSize(): Int
}

data class DirNode(val name: String, val children: MutableList<Node> = mutableListOf()): Node() {
    override fun computeSize(): Int = children.sumOf { it.computeSize() }

    fun getAllDirs(): List<DirNode> = listOf(this) + children.filterIsInstance<DirNode>().flatMap { it.getAllDirs() }
}

data class FileNode(val name: String, val size: Int): Node() {
    override fun computeSize(): Int = size
}

fun main() {
    val commands = File("build/resources/main/day7").readLines()

    lateinit var root: DirNode
    var dirStack = mutableListOf<DirNode>()
    commands.forEach { command ->
        if (command == "$ cd /") {
            root = DirNode("/")
            dirStack.add(root)
        } else if (command == "$ cd ..") {
            dirStack = dirStack.dropLast(1).toMutableList()
        } else if (command.startsWith("$ cd")) {
            val newFolder = DirNode(name = command.substring(5))
            dirStack.last().children.add(newFolder)
            dirStack.add(newFolder)
        } else if (command == "$ ls") {
        } else if (!command.startsWith("dir")) {
            val (filesize, filename) = command.split(" ")
            val newFile = FileNode(name = filename, size = filesize.toInt())
            dirStack.last().children.add(newFile)
        }
    }

    // Part 1
    println(root.getAllDirs().filter { it.computeSize() <= 100000 }.sumBy { it.computeSize() })

    // Part 2
    val freeSpace = 70000000 - root.computeSize()
    val spaceNeeded = 30000000 - freeSpace
    println(root.getAllDirs().sortedBy { it.computeSize() }.first { it.computeSize() >= spaceNeeded }.computeSize())
}
