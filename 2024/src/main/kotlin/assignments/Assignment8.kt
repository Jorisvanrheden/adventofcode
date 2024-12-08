package assignments

import models.assignment.Assignment
import models.matrix.CharMatrix
import models.vector.Vector2D

class Assignment8 : Assignment() {
    private lateinit var matrix: CharMatrix

    override fun getInput(): String {
        return "input_8"
    }

    override fun initialize(input: List<String>) {
        matrix = CharMatrix(input.size, input[0].length)
        for (i in input.indices) {
            for (j in input[i].indices) {
                matrix.values[i][j] = input[i][j]
            }
        }
    }

    override fun calculateSolutionA(): String {
        return matrix
            .flatten()
            .filter { it != '.' }
            .map { matrix.findAntiNodesOf(it) }
            .flatten()
            .toSet()
            .count { matrix.isWithinBounds(it) }
            .toString()
    }

    override fun calculateSolutionB(): String {
        return matrix
            .flatten()
            .filter { it != '.' }
            .map { matrix.findExtendedAntiNodesOf(it) }
            .flatten()
            .toSet()
            .count { matrix.isWithinBounds(it) }
            .toString()
    }

    private fun CharMatrix.findAntiNodesOf(c: Char): Set<Vector2D> {
        val nodes = occurrencesOf(c)
        val antiNodes = mutableSetOf<Vector2D>()
        for (a in nodes) {
            for (b in nodes) {
                if (a == b) continue
                val distance = b - a
                antiNodes.add(a - distance)
            }
        }
        return antiNodes
    }

    private fun CharMatrix.findExtendedAntiNodesOf(c: Char): Set<Vector2D> {
        val nodes = occurrencesOf(c)
        val antiNodes = mutableSetOf<Vector2D>()
        for (a in nodes) {
            for (b in nodes) {
                if (a == b) continue
                val distance = b - a
                var antiNode = a
                while (true) {
                    antiNodes.add(antiNode)
                    antiNode -= distance
                    if (!isWithinBounds(antiNode)) break
                }
            }
        }
        return antiNodes
    }
}

