package assignments

import models.assignment.Assignment
import models.matrix.IntMatrix
import models.vector.Vector2D

class Assignment10 : Assignment(10) {
    private lateinit var matrix: IntMatrix

    override fun initialize(input: List<String>) {
        matrix = IntMatrix(input.size, input[0].length)
        for (i in input.indices) {
            for (j in input[i].indices) {
                matrix.values[i][j] = input[i][j].digitToInt()
            }
        }
    }

    override fun calculateSolutionA(): String {
        return matrix.flattenCoordinates()
            .filter { matrix.values[it.x][it.y] == 0 }
            .sumOf { matrix.bfs(it) }
            .toString()
    }

    override fun calculateSolutionB(): String {
        return matrix.flattenCoordinates()
            .filter { matrix.values[it.x][it.y] == 0 }
            .sumOf { matrix.bfsDistinct(it) }
            .toString()
    }

    private fun IntMatrix.bfsDistinct(startNode: Vector2D): Int {
        val queue = ArrayDeque<Vector2D>()
        queue.add(startNode)

        var total = 0
        while (queue.isNotEmpty()) {
            val node = queue.removeFirst()
            if (values[node.x][node.y] == 9) {
                total ++
            }
            neighbors(node)
                .filter { values[it.x][it.y] - values[node.x][node.y] == 1 }
                .forEach { queue.add(it) }
        }
        return total
    }

    private fun IntMatrix.bfs(startNode: Vector2D): Int {
        val queue = ArrayDeque<Vector2D>()
        val seen = mutableSetOf<Vector2D>()
        queue.add(startNode)

        while (queue.isNotEmpty()) {
            val node = queue.removeFirst()
            neighbors(node)
                .filter { it !in seen && values[it.x][it.y] - values[node.x][node.y] == 1 }
                .forEach { queue.add(it) }
            seen.add(node)
        }
        return seen.count { values[it.x][it.y] == 9 }
    }

    private fun IntMatrix.neighbors(node: Vector2D) =
        listOf(
            node.right(),
            node.down(),
            node.left(),
            node.up(),
        ).filter { isWithinBounds(it) }
}

