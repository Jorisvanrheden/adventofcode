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

    override fun calculateSolutionA(): String {
        return matrix
            .calculateVisitedNodesOrNull()
            ?.count()
            ?.toString() ?: "No answer found."
    }

    override fun calculateSolutionB(): String {
        return matrix
            .flattenCoordinates()
            .count { coordinate ->
                if (matrix.values[coordinate.x][coordinate.y] == '.') {
                    val copy = matrix.copy().apply { values[coordinate.x][coordinate.y] = '#' }
                    copy.calculateVisitedNodesOrNull() == null
                } else {
                    false
                }
            }.toString()
    }

    private fun CharMatrix.calculateVisitedNodesOrNull(): Set<Vector2D>? {
        var direction = Vector2D.UP
        var location = occurrencesOf('^').first()

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
}

