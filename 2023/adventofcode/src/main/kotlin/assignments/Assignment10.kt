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
                nodeMap.values[i][j] = Node(Vector2D(i, j), input[i][j].toString())
            }
        }
        // construct neighbors
        for (i in 0 until nodeMap.rows) {
            for (j in 0 until nodeMap.columns) {
                nodeMap.values[i][j].neighbors = nodeMap.neighbors(Vector2D(i, j))
            }
        }
    }

    override fun calculateSolutionA(): String {
        return nodeMap
            .calculatePath()
            .let { it.size / 2 }
            .toString()
    }

    override fun calculateSolutionB(): String {
        val nodeMapNullable: Array<Array<Node?>> = Array(nodeMap.rows) {
            Array(nodeMap.columns) { null }
        }
        for (i in 0 until nodeMap.rows) {
            for (j in 0 until nodeMap.columns) {
                nodeMapNullable[i][j] = nodeMap.values[i][j]
            }
        }

        val path = nodeMap
            .calculatePath()

        path.forEach {
            nodeMapNullable[it.coordinate.x][it.coordinate.y] = null
        }

        val nodesNotInPath = mutableListOf<Node>()
        for (i in 0 until nodeMap.rows) {
            for (j in 0 until nodeMap.columns) {
                if (nodeMapNullable[i][j] != null) {
                    nodesNotInPath.add(nodeMapNullable[i][j]!!)
                }
            }
        }

        // TEMPORARY
        nodesNotInPath.forEach {
            nodeMap.values[it.coordinate.x][it.coordinate.y].value = "."
        }
        // redo neighbors for nodeMap
        for (i in 0 until nodeMap.rows) {
            for (j in 0 until nodeMap.columns) {
                nodeMap.values[i][j].neighbors = nodeMap.neighbors(Vector2D(i, j))
            }
        }

        for (i in 0 until shiftedNodeMap.rows) {
            for (j in 0 until shiftedNodeMap.columns) {
                shiftedNodeMap.values[i][j] = Node(
                    Vector2D(i, j),
                    "",
                    shiftedNodeMap.shiftedNeighbors(Vector2D(i, j), nodeMap),
                )
            }
        }
        print(nodeMap.toString())

        // for all items not in the path:
        //   - find 4 coordinates of shifted map and take 1
        //   - to a breadth first search
        //   - until an edge is found or search failed
        return nodesNotInPath.count {
            !checkIfPointReachesEdge(shiftedNodeMap, it.coordinate)
        }.toString()
    }

    private fun checkIfPointReachesEdge(shiftedNodeMap: Matrix, vector2D: Vector2D): Boolean {
        val queue = mutableListOf<Vector2D>()
        val visitedNodes = mutableListOf<Vector2D>()

        // add first node
        queue.add(vector2D)

        while (queue.isNotEmpty()) {
            val activeNode = queue.removeFirstOrNull()!!

            // detect if the point is on an edge
            if (activeNode.x == 0 || activeNode.x == shiftedNodeMap.rows - 1) {
                return true
            }
            if (activeNode.y == 0 || activeNode.y == shiftedNodeMap.columns - 1) {
                return true
            }

            // find neighbors
            val neighbors = shiftedNodeMap.values[activeNode.x][activeNode.y].neighbors
            neighbors.forEach { neighbor ->
                if (!visitedNodes.contains(neighbor) && !queue.contains(neighbor)) {
                    queue.add(neighbor)
                }
            }

            visitedNodes.add(activeNode)
        }
        return false
    }

    private fun Matrix.calculatePath(): MutableList<Node> {
        // find starting point
        val startingNode = find { it.value == "S" }

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
            val neighbors = currentNode.neighbors.map { values[it.x][it.y] }
            //  select neighbor[0] if neighbor[0] != previous node and set as current point
            neighbors
                .first { it != previousNode }
                .let { updatedNode ->
                    //  if neighbor[0] == starting node, finish
                    if (updatedNode == startingNode) {
                        return path
                    }
                    path.add(updatedNode)
                }
        }
    }

    private fun Matrix.shiftedNeighbors(vector2D: Vector2D, originalMatrix: Matrix): List<Vector2D> {
        // (0, 0) shifted
        // fetch relevant coordinates from normal matrix
        return listOf(
            vector2D.up() to listOf(vector2D.up().left(), vector2D.up()),
            vector2D.right() to listOf(vector2D.up(), vector2D),
            vector2D.down() to listOf(vector2D, vector2D.left()),
            vector2D.left() to listOf(vector2D.left(), vector2D.up().left()),
        ).filter {
            isWithinBounds(it.first)
        }.mapNotNull {
            val areas = it.second
            // check if the normal coordinates are in bounds
            // if count == 0, no connection is possible
            // if count == 1, guaranteed connection
            // if count == 2, determine possibility connection by types
            val legalCoordinates = listOf(areas[0], areas[1]).filter { originalMatrix.isWithinBounds(it) }
            when (legalCoordinates.count()) {
                0 -> null
                1 -> it
                // simply check if the two nodes are connected to each other in the normal map
                2 -> {
                    // check if a connection is established, if so, then don't allow as a neighbor
                    val neighbors = originalMatrix.values[areas[0].x][areas[0].y].neighbors
                    if (neighbors.contains(Vector2D(areas[1].x, areas[1].y))) {
                        null
                    } else {
                        it
                    }
                }
                else -> null
            }
        }.map { it.first }
    }

    private fun Matrix.neighbors(coordinate: Vector2D) =
        when (values[coordinate.x][coordinate.y].value) {
            "|" -> listOf(coordinate.up(), coordinate.down())
            "-" -> listOf(coordinate.left(), coordinate.right())
            "L" -> listOf(coordinate.up(), coordinate.right())
            "J" -> listOf(coordinate.up(), coordinate.left())
            "7" -> listOf(coordinate.left(), coordinate.down())
            "F" -> listOf(coordinate.right(), coordinate.down())
            "S" -> {
                listOf(
                    coordinate.right() to connectionsToWest,
                    coordinate.left() to connectionsToEast,
                    coordinate.up() to connectionsToSouth,
                    coordinate.down() to connectionsToNorth,
                )
                    .filter { isWithinBounds(it.first) }
                    .filter { it.second.contains(values[it.first.x][it.first.y].value) }
                    .map { it.first }
            }
            else -> emptyList()
        }

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
