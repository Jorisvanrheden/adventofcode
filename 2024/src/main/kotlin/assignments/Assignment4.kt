package assignments

import models.assignment.Assignment
import models.matrix.CharMatrix
import models.vector.Vector2D

class Assignment4 : Assignment(4) {
    private lateinit var matrix: CharMatrix

    private data class Line(
        val start: Vector2D,
        val end: Vector2D,
    )

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
            .flattenCoordinates()
            .flatMap { matrix.findLinesWithString("XMAS", it.x, it.y) }
            .count()
            .toString()
    }

    override fun calculateSolutionB(): String {
        return matrix
            .flattenCoordinates()
            .flatMap { matrix.findLinesWithString("MAS", it.x, it.y) }
            .filter { it.isDiagonal() }
            .map { it.center() }
            .groupingBy { it }
            .eachCount()
            // There is an overlap (X) if two centers are found
            .filterValues { it == 2 }
            .count()
            .toString()
    }

    private fun Line.center() = Vector2D((start.x + end.x)/2,(start.y + end.y)/2)
    private fun Line.isDiagonal() = start.x != end.x && start.y != end.y

    private fun CharMatrix.findLinesWithString(input: String, x: Int, y: Int) =
        listOf(
            Vector2D.UP,
            Vector2D(-1, 1),
            Vector2D.RIGHT,
            Vector2D(1, 1),
            Vector2D.DOWN,
            Vector2D(1, -1),
            Vector2D.LEFT,
            Vector2D(-1, -1),
        ).mapNotNull { findLineInDirectionOrNull(input, x, y, it) }

    private fun CharMatrix.findLineInDirectionOrNull(input: String, x: Int, y: Int, direction: Vector2D): Line? {
        var found = ""
        for (i in 0..input.length) {
            val coordinate = Vector2D(x + direction.x * i, y + direction.y * i)
            if (!isWithinBounds(coordinate)) break

            found += values[coordinate.x][coordinate.y]
            if (found == input) return Line(coordinate, Vector2D(x, y))
        }
        return null
    }
}

