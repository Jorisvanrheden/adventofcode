package assignments

import models.assignment.Assignment
import models.matrix.CharMatrix
import models.vector.Vector2D
import java.util.*
import kotlin.math.abs

class Assignment20 : Assignment(20) {
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
        return findBetterPathCount(
            matrix,
            matrix.occurrencesOf('S').first(),
            matrix.occurrencesOf('E').first(),
            2,
            100
        ).toString()
    }

    override fun calculateSolutionB(): String {
        return findBetterPathCount(
            matrix,
            matrix.occurrencesOf('S').first(),
            matrix.occurrencesOf('E').first(),
            20,
            100
        ).toString()
    }

    private fun findBetterPathCount(matrix: CharMatrix, start: Vector2D, end: Vector2D, shortcutLength: Int, distanceThreshold: Int): Int {
        val distancesFromStart = matrix.distances(start)
        val distancesFromEnd = matrix.distances(end)

        val distanceWithoutCheats = distancesFromStart[end]!!

        var count = 0

        for ((key1, distance1) in distancesFromStart) {
            for ((key2, distance2) in distancesFromEnd) {
                val totalDistance = key1.manhattanDistance(key2)
                if (totalDistance <= shortcutLength) {
                    if (distanceWithoutCheats - (distance1 + distance2 + totalDistance) >= distanceThreshold) {
                        count++
                    }
                }
            }
        }
        return count
    }

    private fun CharMatrix.distances(from: Vector2D): Map<Vector2D, Int> {
        val distanceTo = mutableMapOf<Vector2D, Int>().withDefault { Int.MAX_VALUE }
        val priorityQueue = PriorityQueue<Pair<Vector2D, Int>>(compareBy { it.second })
        priorityQueue.add(Pair(from, 0))

        while (priorityQueue.isNotEmpty()) {
            val (currentNode, currentDistance) = priorityQueue.poll()
            neighbors(currentNode)
                .filter { values[it.x][it.y] != '#' }
                .forEach { neighbor ->
                    val newDistance = currentDistance + 1
                    if (newDistance < distanceTo.getValue(neighbor)) {
                        distanceTo[neighbor] = newDistance
                        priorityQueue.add(Pair(neighbor, newDistance))
                    }
                }
        }
        return distanceTo
    }

    private fun Vector2D.manhattanDistance(other: Vector2D) = abs(x - other.x) + abs(y - other.y)
}