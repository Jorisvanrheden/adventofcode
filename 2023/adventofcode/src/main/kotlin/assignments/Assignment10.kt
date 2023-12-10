package assignments

import models.Matrix
import models.Node
import toolkit.Vector2D

class Assignment10 : Assignment() {

    override fun getInput(): String {
        return "input_10"
    }

    private lateinit var nodeMap: Matrix
    private lateinit var shiftedNodeMap: Matrix

    override fun initialize(input: List<String>) {
        nodeMap = Matrix(input.count(), input[0].count())
        shiftedNodeMap = Matrix(input.count() + 1, input[0].count() + 1)

        for (i in 0 until nodeMap.rows) {
            for (j in 0 until nodeMap.columns) {
                nodeMap.values[i][j] = Node(input[i][j].toString())
            }
        }
        // construct neighbors
        for (i in 0 until nodeMap.rows) {
            for (j in 0 until nodeMap.columns) {
                nodeMap.values[i][j].neighbors = nodeMap.neighbors(Vector2D(i, j))
            }
        }

//        for (i in 0 until shiftedNodeMap.rows) {
//            for (j in 0 until shiftedNodeMap.columns) {
//                shiftedNodeMap.values[i][j] = Node(
//                    "",
//                    shiftedNodeMap.shiftedNeighbors(Vector2D(i, j), nodeMap),
//                )
//            }
//        }
    }

    override fun calculateSolutionA(): String {
        return nodeMap
            .calculatePath()
            .let { it.size / 2 }
            .toString()
    }

    override fun calculateSolutionB(): String {
        return nodeMap
            .calculatePath()
            .let { nodeMap.rows * nodeMap.columns - it.size }
            .toString()
    }

    private fun Matrix.calculatePath(): MutableList<Node> {
        // find starting point
        val startingNode = nodeMap.find { it.value == "S" }

        // for current point
        val path = mutableListOf<Node>()
        path.add(startingNode)
        while (true) {
            val currentNode = path.last()
            var previousNode = currentNode
            if (path.size > 1) {
                previousNode = path[path.lastIndex - 1]
            }

            //  check for all connecting neighbors
            val neighbors = currentNode.neighbors.map { nodeMap.values[it.x][it.y] }
            //  select neighbor[0] if neighbor[0] != previous node and set as current point
            neighbors
                .first { it != previousNode }
                .let { updatedNode ->
                    path.add(updatedNode)

                    //  if neighbor[0] == starting node, finish
                    if (updatedNode == startingNode) {
                        return path
                    }
                }
        }
    }

    private fun List<String>.find(input: String): Vector2D? {
        for (i in 0 until first().count()) {
            for (j in 0 until count()) {
                if (this[i][j].toString() == input) return Vector2D(i, j)
            }
        }
        return null
    }

    private fun Matrix.shiftedNeighbors(vector2D: Vector2D, matrix: Matrix): List<Vector2D> {
        // verify coordinate
        val up = vector2D.up()
        if (isWithinBounds(up)) {
            // fetch relevant coordinates from normal matrix
            val topLeft = up.left().let {
                matrix.values[it.x][it.y]
            }
            val topRight = up.right().let {
                matrix.values[it.x][it.y]
            }

            // if only one legal coordinate, then the connection should be possible
        }
        return emptyList()
    }

    private fun Matrix.neighbors(vector2D: Vector2D) =
        when (values[vector2D.x][vector2D.y].value) {
            "|" -> listOf(vector2D.up(), vector2D.down())
            "-" -> listOf(vector2D.left(), vector2D.right())
            "L" -> listOf(vector2D.up(), vector2D.right())
            "J" -> listOf(vector2D.up(), vector2D.left())
            "7" -> listOf(vector2D.left(), vector2D.down())
            "F" -> listOf(vector2D.right(), vector2D.down())
            "S" -> {
                listOf(
                    vector2D.right() to connectionsToWest,
                    vector2D.left() to connectionsToEast,
                    vector2D.up() to connectionsToSouth,
                    vector2D.down() to connectionsToNorth,
                ).filter {
                    it.second.contains(values[it.first.x][it.first.y].value)
                }.map { it.first }
            }
            else -> emptyList()
        }
            .filter { isWithinBounds(it) }

    private companion object {
        const val NORTH_SOUTH = "|"
        const val WEST_EAST = "-"
        const val NORTH_EAST = "L"
        const val NORTH_WEST = "J"
        const val SOUTH_WEST = "7"
        const val SOUTH_EAST = "F"

        val connectionsToNorth = listOf(NORTH_EAST, NORTH_SOUTH, NORTH_WEST)
        val connectionsToSouth = listOf(SOUTH_EAST, SOUTH_WEST, NORTH_SOUTH)
        val connectionsToEast = listOf(NORTH_EAST, SOUTH_EAST, WEST_EAST)
        val connectionsToWest = listOf(WEST_EAST, SOUTH_WEST, NORTH_WEST)
    }
}
