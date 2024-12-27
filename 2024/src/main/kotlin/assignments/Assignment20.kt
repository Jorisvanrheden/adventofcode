package assignments

import models.assignment.Assignment
import models.matrix.CharMatrix
import models.vector.Vector2D
import java.util.*

class Assignment20 : Assignment(20) {
    private lateinit var matrix: CharMatrix

    private data class Node(
        val position: Vector2D,
        var canCheat: Boolean = true,
    )

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
        val start = Node(matrix.occurrencesOf('S').first())
        val end = Node(matrix.occurrencesOf('E').first())

        val costWithoutCheats = matrix.dijkstra(start.position, end.position)

        val shortcuts = mutableSetOf<Vector2D>()
        for (i in 0 until matrix.rows) {
            println(i)
            for (j in 0 until matrix.columns) {
                if (matrix.values[i][j] != '#') continue

                // cache
                val c = matrix.values[i][j]

                // update
                matrix.values[i][j] = '.'

                val cost = matrix.dijkstra(start.position, end.position)
                if (costWithoutCheats - cost >= 100) {
                    shortcuts.add(Vector2D(i, j))
//                    println(matrix)
                }

                // reset
                matrix.values[i][j] = c
            }
        }
        return shortcuts.count().toString()
    }

    override fun calculateSolutionB(): String {
        return ""
    }

    private fun CharMatrix.dijkstra(start: Vector2D, end: Vector2D): Int {
        val distanceTo = mutableMapOf<Vector2D, Int>().withDefault { Int.MAX_VALUE }
        val priorityQueue = PriorityQueue<Pair<Vector2D, Int>>(compareBy { it.second })
        priorityQueue.add(Pair(start, 0))

        while (priorityQueue.isNotEmpty()) {
            val (currentNode, currentDistance) = priorityQueue.poll()
            if (currentNode == end) {
                return distanceTo[end]!!
            }
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
        return -1
    }
}