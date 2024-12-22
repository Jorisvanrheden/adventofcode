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
        return matrix.traverse(
            start = matrix.occurrencesOf('S').first(),
            end = matrix.occurrencesOf('E').first(),
            Vector2D.RIGHT
        ).first.toString()
    }

    override fun calculateSolutionB(): String {
        val start = matrix.occurrencesOf('S').first()
        val end = matrix.occurrencesOf('E').first()
        val lowestCost = matrix.traverse(start, end, Vector2D.RIGHT)

        var c = 2
        for (i in 0 until matrix.rows) {
            println("$i/${matrix.rows}")
            for (j in 0 until matrix.columns) {
                if (Vector2D(i,j) == start || Vector2D(i,j) == end) continue
                val middle = Vector2D(i, j)
                if (matrix.values[i][j] == '#') continue
                val a = matrix.traverse(start, middle, Vector2D.RIGHT)
                val b = matrix.traverse(middle, end, middle - a.second[middle]!!)
                if (a.first == -1 || b.first == -1) continue
                if (a.first + b.first == lowestCost.first) {
                    c++
                }
            }
        }
        return c.toString()
    }

    private fun CharMatrix.traverse(start: Vector2D, end: Vector2D, startDirection: Vector2D): Pair<Int, Map<Vector2D, Vector2D>> {
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
                return fScores[current]!! to bestConnectionTo
            }

            val currentDirection = directionToPreviousNode(current, start, bestConnectionTo, startDirection)
            neighbors(current)
                .filter { values[it.x][it.y] != '#' }
                .forEach { neighbor ->
                    var costToNeighbor = 1
                    if (is90DegreesNeighbor(current, neighbor, currentDirection)) costToNeighbor += 1000
                    val tentativeGScore = gScores[current]!! + costToNeighbor
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
        return -1 to emptyMap()
    }

    private fun directionToPreviousNode(current: Vector2D, start: Vector2D, cameFrom: Map<Vector2D, Vector2D>, startDirection: Vector2D) =
        if (current == start) {
            startDirection
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

