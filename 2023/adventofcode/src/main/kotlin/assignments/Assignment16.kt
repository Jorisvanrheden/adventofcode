package assignments

import models.MatrixChar
import toolkit.Vector2D

class Assignment16 : Assignment() {

    override fun getInput(): String {
        return "input_16"
    }

    private data class Node(val coordinate: Vector2D, val direction: Vector2D)

    private lateinit var matrix: MatrixChar

    override fun initialize(input: List<String>) {
        matrix = MatrixChar(input.size, input[0].length)

        for (i in input.indices) {
            for (j in input[i].indices) {
                matrix.values[i][j] = input[i][j]
            }
        }
    }

    override fun calculateSolutionA() =
        matrix
            .traverse(Node(Vector2D(0,0), Vector2D.RIGHT))
            .map { it.coordinate }
            .distinct()
            .size
            .toString()

    override fun calculateSolutionB(): String {
        val entryPoints = mutableListOf<Node>()
        // all top and bottom
        for (j in 0 until matrix.columns) {
            entryPoints.add(Node(Vector2D(0, j), Vector2D.DOWN))
            entryPoints.add(Node(Vector2D(matrix.rows - 1, j), Vector2D.UP))
        }
        // all left and right
        for (i in 0 until matrix.rows) {
            entryPoints.add(Node(Vector2D(i, 0), Vector2D.RIGHT))
            entryPoints.add(Node(Vector2D(i, matrix.columns - 1), Vector2D.LEFT))
        }
        return entryPoints
            .map {
                matrix
                    .traverse(it)
                    .map { node -> node.coordinate }
                    .distinct()
                    .size
            }
            .maxOf { it }
            .toString()
    }

    private fun MatrixChar.traverse(startNode: Node): MutableSet<Node> {
        val visitedNodes = mutableSetOf<Node>()
        
        val queue = mutableListOf<Node>()
        queue.add(startNode)

        while (queue.isNotEmpty()) {
            val currentNode = queue.removeFirst()
            val value = values[currentNode.coordinate.x][currentNode.coordinate.y]
            getDirections(value, currentNode.direction)
                .map { Node(currentNode.coordinate + it, it) }
                .filter { isWithinBounds(it.coordinate) }
                .filter { !visitedNodes.contains(it) }
                .forEach {
                    queue.add(it)
                }
            visitedNodes.add(currentNode)
        }
        return visitedNodes
    }

    private fun getDirections(input: Char, direction: Vector2D) =
        when (input) {
            // continue
            '.' -> listOf(direction)
            '-' -> {
                // continue when already going horizontally
                if (direction == Vector2D.LEFT || direction == Vector2D.RIGHT) {
                    listOf(direction)
                } else {
                    // if the beam was going vertically, split in two horizontal directions
                    listOf(Vector2D.LEFT, Vector2D.RIGHT)
                }
            }
            '|' -> {
                // continue when already going vertically
                if (direction == Vector2D.UP || direction == Vector2D.DOWN) {
                    listOf(direction)
                } else {
                    // if the beam was going vertically, split in two horizontal directions
                    listOf(Vector2D.UP, Vector2D.DOWN)
                }
            }
            '/' -> {
                when (direction) {
                    Vector2D.RIGHT -> listOf(Vector2D.UP)
                    Vector2D.LEFT -> listOf(Vector2D.DOWN)
                    Vector2D.DOWN -> listOf(Vector2D.LEFT)
                    Vector2D.UP -> listOf(Vector2D.RIGHT)
                    else -> emptyList()
                }
            }
            '\\' -> {
                when (direction) {
                    Vector2D.RIGHT -> listOf(Vector2D.DOWN)
                    Vector2D.LEFT -> listOf(Vector2D.UP)
                    Vector2D.DOWN -> listOf(Vector2D.RIGHT)
                    Vector2D.UP -> listOf(Vector2D.LEFT)
                    else -> emptyList()
                }
            }
            else -> emptyList()
        }
}
