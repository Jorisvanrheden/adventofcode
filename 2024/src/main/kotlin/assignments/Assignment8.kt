package assignments

import models.assignment.Assignment
import models.matrix.CharMatrix
import models.vector.Vector2D

class Assignment8 : Assignment(8) {
    private lateinit var matrix: CharMatrix

    override fun initialize(input: List<String>) {
        matrix = CharMatrix(input.size, input[0].length)
        for (i in input.indices) {
            for (j in input[i].indices) {
                matrix.values[i][j] = input[i][j]
            }
        }
    }

    override fun calculateSolutionA(): String {
        return matrix.flatten()
            .filter { it != '.' }
            .flatMap { matrix.findAntiNodesOf(it, ::findSingularAntiNode) }
            .toSet()
            .count { matrix.isWithinBounds(it) }
            .toString()
    }

    override fun calculateSolutionB(): String {
        return matrix.flatten()
            .filter { it != '.' }
            .flatMap { matrix.findAntiNodesOf(it, ::findExtendedAntiNodes) }
            .toSet()
            .count { matrix.isWithinBounds(it) }
            .toString()
    }

    private fun findSingularAntiNode(matrix: CharMatrix, startNode: Vector2D, endNode: Vector2D): Set<Vector2D> {
        val distance = endNode - startNode
        return setOf(startNode - distance)
    }

    private fun findExtendedAntiNodes(matrix: CharMatrix, startNode: Vector2D, endNode: Vector2D): Set<Vector2D> {
        val antiNodes = mutableSetOf<Vector2D>()
        val distance = endNode - startNode
        var currentNode = startNode
        while (matrix.isWithinBounds(currentNode)) {
            antiNodes.add(currentNode)
            currentNode -= distance
        }
        return antiNodes
    }

    private fun CharMatrix.findAntiNodesOf(c: Char, predicate: (CharMatrix, Vector2D, Vector2D) -> Set<Vector2D>): Set<Vector2D> {
        val nodes = occurrencesOf(c)
        val antiNodes = mutableSetOf<Vector2D>()
        for (a in nodes) {
            for (b in nodes) {
                if (a == b) continue
                antiNodes.addAll(predicate(this, a, b))
            }
        }
        return antiNodes
    }
}