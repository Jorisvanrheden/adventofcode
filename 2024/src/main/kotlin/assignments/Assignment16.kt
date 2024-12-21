package assignments

import models.assignment.Assignment
import models.matrix.CharMatrix
import models.vector.Vector2D
import kotlin.math.abs

class Assignment16 : Assignment(16) {
    private lateinit var matrix: CharMatrix

    override fun initialize(input: List<String>) {
        matrix = CharMatrix(input.size, input[0].length).apply {
            for (i in 0 until rows) {
                for (j in 0 until columns) {
                    values[i][j] = input[i][j]
                }
            }
        }
    }

    override fun calculateSolutionA(): String {
        val path = matrix.traverse(
            start = matrix.occurrencesOf('S').first(),
            end = matrix.occurrencesOf('E').first(),
        )
        return calculateScore(path).toString()
    }

    override fun calculateSolutionB(): String {
        val path = matrix.traverse(
            start = matrix.occurrencesOf('S').first(),
            end = matrix.occurrencesOf('E').first(),
        )
        return calculateScore(path).toString()
    }

    private fun calculateScore(path: List<Vector2D>): Int {
        var lastDirection = Vector2D.RIGHT
        var directionChanges = 0
        for (i in 1 until path.lastIndex) {
            val direction = path[i] - path[i - 1]
            if (direction != lastDirection) {
                lastDirection = direction
                directionChanges++
            }
        }
        return (directionChanges * 1000 + path.size)
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
                return constructPath(end, bestConnectionTo)
            }

            val currentDirection = directionToPreviousNode(current, start, bestConnectionTo)
            neighbors(current)
                .filter { values[it.x][it.y] != '#' }
                .forEach { neighbor ->
                    val weightToNeighbor = if (is90DegreesNeighbor(current, neighbor, currentDirection)) 1000 else 1
                    val tentativeGScore = gScores[current]!! + weightToNeighbor
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

    private fun constructPath(end: Vector2D, cameFrom: Map<Vector2D, Vector2D>): List<Vector2D> {
        val path = mutableListOf<Vector2D>()
        var latestNode = end
        while (cameFrom.containsKey(latestNode)) {
            path.add(latestNode)
            latestNode = cameFrom[latestNode]!!
        }
        return path.reversed()
    }

    private fun directionToPreviousNode(current: Vector2D, start: Vector2D, cameFrom: Map<Vector2D, Vector2D>) =
        if (current == start) {
            Vector2D.RIGHT
        } else {
            current - cameFrom[current]!!
        }

    private fun is90DegreesNeighbor(current: Vector2D, neighbor: Vector2D, currentDirection: Vector2D) =
        when (currentDirection) {
            neighbor - current -> false
            current - neighbor -> false
            else -> true
        }

    private fun Vector2D.manhattanDistance(other: Vector2D) = abs(x - other.x) + abs(y - other.y)
}

