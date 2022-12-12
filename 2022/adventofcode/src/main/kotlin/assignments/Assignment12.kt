package assignments

import toolkit.Vector2D

class Assignment12 : Assignment() {

    data class Tile(
        val x: Int,
        val y: Int,
        val value: Int
    )

    class Grid(val rows: Int, val columns: Int) {
        var values: Array<Array<Tile>> = Array(rows) {
            Array(columns) {
                Tile(0, 0, 0)
            }
        }

        fun getNeighborsAt(coordinate: Vector2D): List<Tile> {
            // top down left and right
            return listOf(
                coordinate + Vector2D(1, 0),
                coordinate + Vector2D(-1, 0),
                coordinate + Vector2D(0, 1),
                coordinate + Vector2D(0, -1)
            )
                .filter {
                    it.x >= 0 && it.x < rows && it.y >= 0 && it.y < columns &&
                        values[it.x][it.y].value - values[coordinate.x][coordinate.y].value <= 1
                }
                .map { values[it.x][it.y] }
        }
    }

    private lateinit var grid: Grid
    private var start: Vector2D = Vector2D(0, 0)
    private var target: Vector2D = Vector2D(0, 0)

    override fun getInput(): String {
        return "input_12"
    }

    override fun initialize(input: List<String>) {
        grid = Grid(input.size, input[0].length)

        for (i in input.indices) {
            for (j in input[i].indices) {
                val c = input[i][j]
                if (c == 'S') {
                    start = Vector2D(i, j)
                    grid.values[i][j] = Tile(i, j, 'a'.toInt() - 'a'.toInt())
                }
                if (c == 'E') {
                    target = Vector2D(i, j)
                    grid.values[i][j] = Tile(i, j, 'z'.toInt() - 'a'.toInt())
                }
                if (c.isLowerCase()) {
                    grid.values[i][j] = Tile(i, j, c.toInt() - 'a'.toInt())
                }
            }
        }
    }

    private fun constructPath(start: Tile, linkMap: Map<Tile, Tile>): List<Tile> {
        var path = mutableListOf<Tile>()

        var activeNode = start

        while (activeNode != null) {
            activeNode = linkMap[activeNode] ?: break
            path.add(activeNode)
        }

        return path.reversed()
    }

    private fun findBestPath(grid: Grid, start: Tile, target: Tile): List<Tile> {
        // data structure to store the connected tiles
        var linkMap = mutableMapOf<Tile, Tile>()

        // queue for storing neighbors to process during the next iteration
        var queue = mutableListOf<Tile>()

        // keep track of nodes that are visited
        var visitedSet = mutableListOf<Tile>()

        visitedSet.add(start)
        queue.add(start)

        while (queue.isNotEmpty()) {
            // pop the first node from the queue
            val activeNode = queue.removeFirst()

            if (activeNode.x == target.x && activeNode.y == target.y) {
                return constructPath(activeNode, linkMap)
            }
            val neighbors = grid.getNeighborsAt(Vector2D(activeNode.x, activeNode.y))
            for (neighbor in neighbors) {
                if (!visitedSet.contains(neighbor)) {
                    visitedSet.add(neighbor)

                    queue.add(neighbor)

                    // store connection
                    linkMap[neighbor] = activeNode
                }
            }
        }

        return emptyList()
    }

    override fun calculateSolutionA(): String {
        // you need to store the costs and visitedNodes
        return findBestPath(grid, grid.values[start.x][start.y], grid.values[target.x][target.y])
            .count()
            .toString()
    }

    override fun calculateSolutionB(): String {
        var startingPositions = mutableListOf<Tile>()
        for (i in 0 until grid.rows) {
            for (j in 0 until grid.columns) {
                if (grid.values[i][j].value == 0) {
                    startingPositions.add(grid.values[i][j])
                }
            }
        }
        return startingPositions.map {
            findBestPath(grid, grid.values[it.x][it.y], grid.values[target.x][target.y])
        }
            .map { it.count() }
            .filter { it > 0 }
            .sorted()
            .first()
            .toString()
    }
}
