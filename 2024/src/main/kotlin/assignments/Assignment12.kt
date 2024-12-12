package assignments

import models.assignment.Assignment
import models.matrix.CharMatrix
import models.vector.Vector2D
import kotlin.math.abs

class Assignment12 : Assignment(12) {
    private lateinit var matrix: CharMatrix

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
            .toRegions()
            .sumOf { it.size * it.perimeter() }
            .toString()
    }

    override fun calculateSolutionB(): String {
        return matrix
            .toRegions()
            .sumOf { it.size * it.sides() }
            .toString()
    }

    private fun Set<Vector2D>.perimeter() =
        sumOf {
            listOf(
                it.up(),
                it.right(),
                it.down(),
                it.left()
            ).count { !contains(it) }
        }

    private fun Set<Vector2D>.sides(): Int {
        val perimeterNodes = flatMap {
            // Define the relationship between the original node and the perimeter node
            listOf(
                Pair(it, it.up()),
                Pair(it, it.right()),
                Pair(it, it.down()),
                Pair(it, it.left()),
            ).filter { !contains(it.second) }
        }

        val totalSections = mutableSetOf<Set<Pair<Vector2D, Vector2D>>>()
        for (node in perimeterNodes) {
            val seen = mutableSetOf<Pair<Vector2D, Vector2D>>()
            val queue = ArrayDeque<Pair<Vector2D, Vector2D>>()
            queue.add(node)
            while (queue.isNotEmpty()) {
                val currentNode = queue.removeFirst()
                seen.add(currentNode)

                perimeterNodes
                    .filter { currentNode.second.neighborLocations().contains(it.second) && it.first.manhattanDistance(currentNode.first) == 1 }
                    .forEach { neighbor ->
                        if (!seen.contains(neighbor)) {
                            queue.add(neighbor)
                        }
                    }
            }
            totalSections.add(seen)
        }
        return totalSections.size
    }

    private fun Vector2D.neighborLocations() = listOf(up(), right(), down(), left())
    private fun Vector2D.manhattanDistance(node: Vector2D) = abs(x - node.x) + abs(y - node.y)

    private fun CharMatrix.toRegions(): MutableList<Set<Vector2D>> {
        val regions = mutableListOf<Set<Vector2D>>()
        for (i in 0 until rows) {
            for (j in 0 until columns) {
                // check if value is already part of a region, if so skip
                if (regions.any { it.contains(Vector2D(i, j)) }) continue
                // otherwise, recursively (bfs) check for attached nodes
                regions.add(findRegion(Vector2D(i, j)))
            }
        }
        return regions
    }

    private fun CharMatrix.findRegion(startNode: Vector2D): MutableSet<Vector2D> {
        val queue = ArrayDeque<Vector2D>()
        val seen = mutableSetOf<Vector2D>()
        val region = mutableSetOf<Vector2D>()
        queue.add(startNode)
        while (queue.isNotEmpty()) {
            val node = queue.removeFirst()
            val neighbors = neighbors(node)
            for (neighbor in neighbors) {
                if (seen.contains(neighbor)) continue
                seen.add(neighbor)

                if (values[neighbor.x][neighbor.y] != values[node.x][node.y]) continue
                queue.add(neighbor)
            }
            region.add(node)
        }
        return region
    }
}

