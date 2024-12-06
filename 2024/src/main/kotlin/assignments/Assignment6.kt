package assignments

import models.assignment.Assignment
import models.matrix.CharMatrix
import models.vector.Vector2D

class Assignment6 : Assignment() {

    private lateinit var matrix: CharMatrix

    override fun getInput(): String {
        return "input_6"
    }

    override fun initialize(input: List<String>) {
        matrix = CharMatrix(input.size, input[0].length)
        for (i in input.indices) {
            for (j in input[i].indices) {
                matrix.values[i][j] = input[i][j]
            }
        }
    }

    private fun CharMatrix.coordinatesWithChar(char: Char): List<Vector2D> {
        val vectors = mutableListOf<Vector2D>()
        for (i in 0 until rows) {
            for (j in 0 until columns) {
                if (values[i][j] == char) vectors.add(Vector2D(i, j))
            }
        }
        return vectors
    }

    private fun CharMatrix.traverse(): Set<Vector2D>? {
        var direction = Vector2D.UP
        var location = coordinatesWithChar('^').first()

        // If the same consecutive two coordinates are found again, then a cycle is detected
        val keys = mutableMapOf<Pair<Vector2D, Vector2D>, Int>()
        val locations = mutableListOf<Vector2D>(location)

        while(true) {
            val nextLocation = location + direction
            if (!isWithinBounds(nextLocation)) break

            when (values[nextLocation.x][nextLocation.y]) {
                '#' -> direction = direction.rotateRight()
                else -> {
                    location = nextLocation
                    locations.add(nextLocation)

                    val lastCoordinates = locations.takeLast(2)
                    if (lastCoordinates.size == 2) {
                        val key = Pair(lastCoordinates[0], lastCoordinates[1])
                        if (keys.contains(key)) {
                            return null
                        } else {
                            keys[key] = 0
                        }
                    }
                }
            }
        }
        return locations.toSet()
    }

    override fun calculateSolutionA(): String {
        return matrix
            .traverse()
            ?.count()
            ?.toString() ?: "No answer found."
    }

    override fun calculateSolutionB(): String {
        var timesStuckInLoop = 0

        matrix.forEach { x, y ->
            matrix.copy().apply {
                if (values[x][y] == '.') {
                    values[x][y] = '#'
                    if (traverse() == null) {
                        timesStuckInLoop++
                    }
                }
            }
        }

        return timesStuckInLoop.toString()
    }
}

