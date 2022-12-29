package assignments

import toolkit.Matrix
import toolkit.Vector2D

class Assignment24 : Assignment() {
    override fun getInput(): String {
        return "input_24"
    }

    data class Blizzard(val startingPosition: Vector2D, val direction: Vector2D)

    private lateinit var blizzards: List<Blizzard>
    private lateinit var grid: Matrix

    override fun initialize(input: List<String>) {
        var blizzards = mutableListOf<Blizzard>()
        grid = Matrix(input.size, input[0].length)

        for (i in input.indices) {
            for (j in input[i].indices) {
                // initialize walkable grid
                if (input[i][j] != '#') {
                    grid.values[i][j] = 1
                }

                // initialize blizzards
                var direction = when (input[i][j]) {
                    '>' -> Vector2D(0, 1)
                    '<' -> Vector2D(0, -1)
                    '^' -> Vector2D(-1, 0)
                    'v' -> Vector2D(1, 0)
                    else -> continue
                }
                blizzards.add(Blizzard(Vector2D(i, j), direction))
            }
        }
        this.blizzards = blizzards
    }

    private fun initializeBlizzardsForTurns(grid: Matrix, blizzards: List<Blizzard>): List<Matrix> {
        var blizzardGrids = mutableListOf<Matrix>()

        val blizzardXMin = 1
        val blizzardXMax = grid.rows - 1
        val blizzardXRange = blizzardXMax - blizzardXMin
        val blizzardYMin = 1
        val blizzardYMax = grid.columns - 1
        val blizzardYRange = blizzardYMax - blizzardYMin

        val turns = (grid.rows - 2) * (grid.columns - 2)

        for (i in 0 until turns) {
            // initialize a new grid for each turn
            var updatedGrid = Matrix(grid.rows, grid.columns)
            // initialize with walkable positions
            for (r in 0 until updatedGrid.rows) {
                for (c in 0 until updatedGrid.columns) {
                    updatedGrid.values[r][c] = 1
                }
            }

            // apply the blizzard locations to that turns
            for (blizzard in blizzards) {
                // calculate the location for this blizzard at t(i)
                var blizzardPosition = blizzard.startingPosition + Vector2D(
                    blizzard.direction.x * i,
                    blizzard.direction.y * i
                )

                // constrain the position to be within the boundaries of the grid
                // mod to move the blizzard to the other side of the map
                blizzardPosition.x = (blizzardPosition.x - blizzardXMin).mod(blizzardXRange) + blizzardXMin
                blizzardPosition.y = (blizzardPosition.y - blizzardYMin).mod(blizzardYRange) + blizzardYMin

                updatedGrid.values[blizzardPosition.x][blizzardPosition.y] = 0
            }
            // store the blizzard grid for t(i)
            blizzardGrids.add(updatedGrid)
        }
        return blizzardGrids
    }

    private fun getNeighbors(currentPosition: Vector2D, grid: Matrix): List<Vector2D> {
        val neighborDirections = listOf(
            Vector2D(1, 0),
            Vector2D(-1, 0),
            Vector2D(0, 1),
            Vector2D(0, -1),
            // also store the current position, for staying idle
            Vector2D(0, 0)
        )

        return neighborDirections.map { currentPosition + it }.filter {
            grid.isWithinBounds(it) && grid.values[it.x][it.y] == 1
        }
    }

    data class TimeTile(val t: Int, val position: Vector2D)

    private fun findBestPath(grid: Matrix, start: TimeTile, target: Vector2D, blizzardsPerTurn: List<Matrix>): Int {
        // queue for storing neighbors to process during the next iteration
        var queue = mutableListOf<TimeTile>()

        // keep track of nodes that are visited
        var visitedSet = mutableSetOf<TimeTile>()

        visitedSet.add(start)
        queue.add(start)

        while (queue.isNotEmpty()) {
            // pop the first node from the queue
            val activeNode = queue.removeFirst()

            if (activeNode.position == target) {
                return activeNode.t
                println("Path found, implementation now missing..")
            }

            val nextTimeStep = activeNode.t + 1
            val blizzardIndex = nextTimeStep.mod(blizzardsPerTurn.size)
            val neighbors = getNeighbors(activeNode.position, grid).filter {
                blizzardsPerTurn[blizzardIndex].values[it.x][it.y] == 1
            } // transform to time tiles, as we need the time property
                .map { TimeTile(nextTimeStep, it) }

            for (neighbor in neighbors) {
                if (!visitedSet.contains(neighbor)) {
                    visitedSet.add(neighbor)

                    queue.add(neighbor)
                }
            }
        }

        return -1
    }

    override fun calculateSolutionA(): String {
        // 1.) Pre-initialize a collection with the locations of the blizzards over 18 turns
        val blizzardsPerTurn = initializeBlizzardsForTurns(grid, blizzards)

        // 2.) Run simulation for t(turns)
        val result = findBestPath(
            grid,
            TimeTile(0, Vector2D(0, 1)),
            Vector2D(grid.rows - 1, grid.columns - 2),
            blizzardsPerTurn
        )

        // to find out which tile you can move to, you need to know all the tiles that will NOT be
        // occupied the next turn
        //
        return result.toString()
    }

    override fun calculateSolutionB(): String {
        // 1.) Pre-initialize a collection with the locations of the blizzards over 18 turns
        val blizzardsPerTurn = initializeBlizzardsForTurns(grid, blizzards)

        val start = Vector2D(0, 1)
        val target = Vector2D(grid.rows - 1, grid.columns - 2)

        // store result and put turn in the next iteration
        val turnsToTarget = findBestPath(
            grid,
            TimeTile(0, start),
            target,
            blizzardsPerTurn
        )

        val turnsToStart = findBestPath(
            grid,
            TimeTile(turnsToTarget, target),
            start,
            blizzardsPerTurn
        )

        val turnsToTarget2 = findBestPath(
            grid,
            TimeTile(turnsToStart, start),
            target,
            blizzardsPerTurn
        )

        return turnsToTarget2.toString()
    }
}
