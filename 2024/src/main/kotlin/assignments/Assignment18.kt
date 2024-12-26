package assignments

import models.assignment.Assignment
import models.matrix.CharMatrix
import models.vector.Vector2D
import kotlin.math.abs

class Assignment18 : Assignment(18) {
    private lateinit var coordinates: List<Vector2D>

    override fun initialize(input: List<String>) {
        coordinates = input.map {
            Vector2D(
                it.split(",")[1].toInt(),
                it.split(",")[0].toInt(),
            )
        }
    }

    override fun calculateSolutionA(): String {
        return CharMatrix(MAP_SIZE + 1, MAP_SIZE + 1)
            .apply {
                coordinates.subList(0, BYTES_TO_PROCESS).forEach {
                    values[it.x][it.y] = '#'
                }
            }
            .traverse(Vector2D(0,0), Vector2D(MAP_SIZE, MAP_SIZE))
            .let { it.count() - 2 }
            .toString()
    }

    override fun calculateSolutionB(): String {
        val matrix = CharMatrix(MAP_SIZE + 1, MAP_SIZE + 1)
        for (i in coordinates.indices) {
            matrix.values[coordinates[i].x][coordinates[i].y] = '#'
            val path = matrix.traverse(Vector2D(0, 0), Vector2D(MAP_SIZE, MAP_SIZE))
            if (path.isEmpty()) {
                return "${coordinates[i].y},${coordinates[i].x}"
            }
        }
        return "No solution found"
    }

    private fun CharMatrix.traverse(start: Vector2D, end: Vector2D): List<Vector2D> {
        // Cost from start to node
        val gScores = mutableMapOf<Vector2D, Int>()
        // Cost from start to finish through this node
        val fScores = mutableMapOf<Vector2D, Int>()
        // Store connections
        val bestConnectionTo = mutableMapOf<Vector2D, Vector2D>()

        val queue = mutableSetOf(start)
        gScores[start] = 0
        fScores[start] = start.manhattanDistance(end)
        while (queue.isNotEmpty()) {
            val current = queue.minByOrNull { fScores[it]!! }!!
            if (current == end) {
                return createPath(end, bestConnectionTo)
            }

            neighbors(current)
                .filter { values[it.x][it.y] != '#' }
                .forEach { neighbor ->
                    val tentativeGScore = gScores[current]!! + 1
                    if (tentativeGScore < gScores.getOrDefault(neighbor, Int.MAX_VALUE)) {
                        bestConnectionTo[neighbor] = current
                        gScores[neighbor] = tentativeGScore
                        fScores[neighbor] = tentativeGScore + neighbor.manhattanDistance(end)

                        if (!queue.contains(neighbor)) {
                            queue.add(neighbor)
                        }
                    }
                }
            queue.remove(current)
        }
        return emptyList()
    }

    private fun createPath(end: Vector2D, bestConnections: Map<Vector2D, Vector2D>): List<Vector2D> {
        val path = mutableListOf<Vector2D>()
        path.add(end)
        var current = end
        while(bestConnections[current] != null) {
            current = bestConnections[current]!!
            path.add(current)
        }
        path.add(current)
        return path.reversed()
    }

    private fun Vector2D.manhattanDistance(other: Vector2D) = abs(x - other.x) + abs(y - other.y)

    private companion object {
        const val MAP_SIZE = 70
        const val BYTES_TO_PROCESS = 1024
    }
}

