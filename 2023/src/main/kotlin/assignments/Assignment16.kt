package assignments

import models.matrix.CharMatrix
import models.assignment.Assignment
import models.vector.Vector2D

class Assignment16 : Assignment(16) {
    private data class DirectionNode(val coordinate: Vector2D, val direction: Vector2D)

    private lateinit var matrix: CharMatrix

    override fun initialize(input: List<String>) {
        matrix = CharMatrix(input.size, input[0].length)

        for (i in input.indices) {
            for (j in input[i].indices) {
                matrix.values[i][j] = input[i][j]
            }
        }
    }

    override fun calculateSolutionA() =
        matrix
            .traverse(DirectionNode(Vector2D(0,0), Vector2D.RIGHT))
            .map { it.coordinate }
            .distinct()
            .size
            .toString()

    override fun calculateSolutionB(): String {
        val entryPoints = mutableListOf<DirectionNode>()
        // all top and bottom
        for (j in 0 until matrix.columns) {
            entryPoints.add(DirectionNode(Vector2D(0, j), Vector2D.DOWN))
            entryPoints.add(DirectionNode(Vector2D(matrix.rows - 1, j), Vector2D.UP))
        }
        // all left and right
        for (i in 0 until matrix.rows) {
            entryPoints.add(DirectionNode(Vector2D(i, 0), Vector2D.RIGHT))
            entryPoints.add(DirectionNode(Vector2D(i, matrix.columns - 1), Vector2D.LEFT))
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

    private fun CharMatrix.traverse(startNode: DirectionNode): MutableSet<DirectionNode> {
        val visitedNodes = mutableSetOf<DirectionNode>()
        
        val queue = mutableListOf<DirectionNode>()
        queue.add(startNode)

        while (queue.isNotEmpty()) {
            val currentNode = queue.removeFirst()
            val value = values[currentNode.coordinate.x][currentNode.coordinate.y]
            getDirections(value, currentNode.direction)
                .map { DirectionNode(currentNode.coordinate + it, it) }
                .filter { isWithinBounds(it.coordinate) }
                .filter { !visitedNodes.contains(it) }
                .forEach {
                    queue.add(it)
                }
            visitedNodes.add(currentNode)
        }
        return visitedNodes
    }

    private fun getDirections(input: Char?, direction: Vector2D) =
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
