package assignments

import models.matrix.Node
import models.matrix.NodeMatrix
import models.assignment.Assignment
import models.vector.Vector2D

class Assignment10 : Assignment() {

    override fun getInput(): String {
        return "input_10"
    }

    private lateinit var matrix: NodeMatrix

    override fun initialize(input: List<String>) {
        matrix = NodeMatrix(input.count(), input[0].count())
        matrix.forEach { i, j ->
            matrix.values[i][j] = Node(Vector2D(i, j), input[i][j].toString())
        }
    }

    override fun calculateSolutionA(): String {
        matrix.calculateNeighbors()
        return matrix
            .calculatePath()
            .let { it.size / 2 }
            .toString()
    }

    override fun calculateSolutionB(): String {
        val path = matrix.calculatePath()

        val nodesNotInPath = matrix
            .copy()
            .flatten()
            .filter { node -> !path.any { a -> node.coordinate == a.coordinate } }

        // Set non-path tiles to '.' and recalculate the connecting neighbors
        nodesNotInPath.forEach { node ->
            matrix.values[node.coordinate.x][node.coordinate.y].value = "."
        }
        matrix.calculateNeighbors()

        // Create the shifted-node map based on the connection from the original map
        val shiftedNodeMap = NodeMatrix(matrix.rows + 1, matrix.columns + 1)
        shiftedNodeMap.forEach { i, j ->
            shiftedNodeMap.values[i][j] = Node(
                Vector2D(i, j),
                "",
                shiftedNodeMap.shiftedNeighbors(Vector2D(i, j), matrix),
            )
        }

        // for all items not in the path:
        //   - find 4 coordinates of shifted map and take 1
        //   - to a breadth first search
        //   - until an edge is found or search failed
        return nodesNotInPath.count {
            !checkIfPointReachesEdge(shiftedNodeMap, it.coordinate)
        }.toString()
    }

    private fun NodeMatrix.calculateNeighbors() {
        forEach { i, j ->
            values[i][j].neighbors = neighbors(Vector2D(i, j))
        }
    }

    private fun checkIfPointReachesEdge(shiftedNodeMap: NodeMatrix, vector2D: Vector2D): Boolean {
        val queue = mutableListOf<Vector2D>()
        val visitedNodes = mutableListOf<Vector2D>()

        // add first node
        queue.add(vector2D)

        while (queue.isNotEmpty()) {
            val activeNode = queue.removeFirstOrNull()!!

            // if the node reaches an edge, it is not locked in the path
            if (shiftedNodeMap.isEdgeCoordinate(activeNode)) {
                return true
            }

            // find neighbors
            val neighbors = shiftedNodeMap.values[activeNode.x][activeNode.y]?.neighbors
            neighbors!!.forEach { neighbor ->
                if (!visitedNodes.contains(neighbor) && !queue.contains(neighbor)) {
                    queue.add(neighbor)
                }
            }

            visitedNodes.add(activeNode)
        }
        return false
    }

    private fun NodeMatrix.isEdgeCoordinate(vector2D: Vector2D) =
        vector2D.x == 0 || vector2D.x == rows - 1 ||
            vector2D.y == 0 || vector2D.y == columns - 1

    private fun NodeMatrix.calculatePath(): MutableList<Node> {
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
            val neighbors = currentNode.neighbors.map { values[it.x][it.y]!! }
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

    private fun NodeMatrix.shiftedNeighbors(vector2D: Vector2D, originalMatrix: NodeMatrix): List<Vector2D> {
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
                    val neighbors = originalMatrix.values[areas[0].x][areas[0].y]!!.neighbors
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

    private fun NodeMatrix.neighbors(coordinate: Vector2D) =
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
                    .filter { it.second.contains(values[it.first.x][it.first.y]!!.value) }
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
