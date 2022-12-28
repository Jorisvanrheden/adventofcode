package assignments

import toolkit.Vector2D
import utilities.Utilities

class Assignment22 : Assignment() {

    override fun getInput(): String {
        return "input_22"
    }

    data class MatrixGridTile(val x: Int, val y: Int, var value: Int, var neighbors: List<MatrixGridTile?>)

    class MatrixGrid(val rows: Int, val columns: Int) {
        var values: Array<Array<MatrixGridTile>> = Array(rows) { row ->
            Array(columns) { column ->
                MatrixGridTile(row, column, -1, emptyList())
            }
        }

        fun isWalkableNode(vector2D: Vector2D): Boolean =
            values[vector2D.x][vector2D.y].value == 1

        fun isEmptyNode(vector2D: Vector2D): Boolean =
            values[vector2D.x][vector2D.y].value == -1

        fun isWithinBounds(vector2D: Vector2D): Boolean {
            if (vector2D.x < 0 || vector2D.x >= rows) return false
            if (vector2D.y < 0 || vector2D.y >= columns) return false
            return true
        }
    }

    data class Instruction(val amount: Int, val rotation: Int)

    private lateinit var field: MatrixGrid
    private lateinit var instructions: List<Instruction>
    private val directions = listOf(
        Vector2D(0, 1),
        Vector2D(1, 0),
        Vector2D(0, -1),
        Vector2D(-1, 0)
    )

    private fun parseField(input: List<String>): MatrixGrid {
        // column count doesn't have to come from the first row
        var matrix = MatrixGrid(input.size, input.maxOf { it.length })
        for (i in 0 until matrix.rows) {
            for (j in 0 until matrix.columns) {
                // -1 for empty space
                // 0 for blocked space
                // 1 for walkable space
                var nodeValue = -1
                if (j < input[i].length) {
                    if (input[i][j].isWhitespace()) nodeValue = -1
                    else if (input[i][j] == '#') nodeValue = 0
                    else if (input[i][j] == '.') nodeValue = 1
                }

                matrix.values[i][j] = MatrixGridTile(i, j, nodeValue, emptyList())
            }
        }
        return matrix
    }
    private fun parseInstructions(input: String): List<Instruction> {
        var pointerIndex = 0
        var buffer = mutableListOf<Char>()
        var instructions = mutableListOf<Instruction>()
        while (pointerIndex < input.length) {
            // instruction is complete when a non-digit is found
            if (!input[pointerIndex].isDigit()) {
                if (buffer.size > 0) {
                    instructions.add(
                        Instruction(
                            buffer.joinToString("").toInt(),
                            0
                        )
                    )
                    buffer.clear()
                }

                var rotation = 1
                if (input[pointerIndex] == 'L') rotation = -1
                instructions.add(Instruction(0, rotation))
            } else {
                buffer.add(input[pointerIndex])
            }

            pointerIndex++
        }
        if (buffer.size > 0) {
            instructions.add(
                Instruction(
                    buffer.joinToString("").toInt(),
                    0
                )
            )
        }
        return instructions
    }

    override fun initialize(input: List<String>) {
        val chunks = Utilities.packageByEmptyLine(input)

        field = parseField(chunks[0])
        instructions = parseInstructions(chunks[1][0])
    }

    private fun MatrixGrid.toStringCustom(): String {
        var output = "\n"
        for (i in 0 until rows) {
            output += "|"
            for (j in 0 until columns) {
                if (values[i][j].value == -1) output += " "
                else if (values[i][j].value == 0) output += "#"
                else if (values[i][j].value == 1) output += "."
                else output += valueToCharacter(values[i][j].value)
            }
            output += "|\n"
        }
        return output
    }

    private fun valueToCharacter(value: Int) =
        when (value) {
            10 -> '>'
            11 -> 'v'
            12 -> '<'
            13 -> '^'
            else -> 'x'
        }

    private fun MatrixGrid.getFurthersOpposingPosition(vector2D: Vector2D, direction: Vector2D): Vector2D {
        // if no direct neighbor is found, then check the furthers node on the opposite side
        // we are looking for (support circular notion of the grid
        val oppositeDirection = Vector2D(direction.x * -1, direction.y * -1)
        var oppositePosition = vector2D
        var lastValidPosition = vector2D

        // while you can move further in the opposite direction, process that node
        while (isWithinBounds(oppositePosition) && !isEmptyNode(oppositePosition)) {
            lastValidPosition = oppositePosition
            oppositePosition += oppositeDirection
        }

        return lastValidPosition
    }

    private fun MatrixGrid.getDirectAndOpposingNeighbors(startPosition: Vector2D): List<MatrixGridTile?> {
        if (!isWalkableNode(startPosition)) return List<MatrixGridTile?>(4) { null }

        var neighbors = MutableList<MatrixGridTile?>(4) { null }
        for ((index, direction) in directions.withIndex()) {
            val neighborNode = startPosition + direction

            if (isWithinBounds(neighborNode) && isWalkableNode(neighborNode)) {
                neighbors[index] = (values[neighborNode.x][neighborNode.y])
            } else if (!isWithinBounds(neighborNode) || isEmptyNode(neighborNode)) {
                // if no direct neighbor is found, then check the furthers node on the opposite side
                // we are looking for (support circular notion of the grid
                val opposingNodePosition = getFurthersOpposingPosition(startPosition, direction)
                if (opposingNodePosition == startPosition) continue

                if (isWalkableNode(opposingNodePosition)) {
                    neighbors[index] = values[opposingNodePosition.x][opposingNodePosition.y]
                }
            }
        }
        return neighbors
    }

    private fun MatrixGrid.getStartPosition(): Vector2D {
        // find the first top left tile that is walkable
        for (i in 0 until rows) {
            for (j in 0 until columns) {
                if (isWalkableNode(Vector2D(i, j))) {
                    return Vector2D(i, j)
                }
            }
        }
        return Vector2D(0, 0)
    }

    private fun getNextDirectionIndex(direction: Vector2D, rotation: Int) =
        (directions.indexOf(direction) + rotation).mod(directions.size)

    private fun MatrixGrid.initializeDirectAndOpposingNeighbors() {
        for (i in 0 until rows) {
            for (j in 0 until columns) {
                values[i][j].neighbors = getDirectAndOpposingNeighbors(Vector2D(i, j))
            }
        }
    }

    private fun MatrixGrid.getAreas(): List<Area> {
        // example input
        val areaSize = 50

        var areas = mutableListOf<Area>()

        for (i in 0 until rows / areaSize) {
            for (j in 0 until columns / areaSize) {
                val x = i * areaSize
                val y = j * areaSize
                if (!isEmptyNode(Vector2D(x, y))) {
                    var areaMatrix = MatrixGrid(areaSize, areaSize)
                    for (coordX in 0 until areaSize) {
                        for (coordY in 0 until areaSize) {
                            areaMatrix.values[coordX][coordY] = MatrixGridTile(
                                coordX,
                                coordY,
                                values[x + coordX][y + coordY].value,
                                emptyList()
                            )
                        }
                    }
                    areas.add(Area(Vector2D(x, y), i, j, mutableListOf(), areaMatrix))
                }
            }
        }

        // add connections in order of direction

        // --- EXAMPLE INPUT MAPPING ---
//        // 1 -> 2, 3, 4, 6
//        areas[0].connections.add(AreaConnection(areas[5], { c -> Vector2D(areaSize - 1 - c.x, areaSize - 1) }, Vector2D(0, -1)))
//        areas[0].connections.add(AreaConnection(areas[3], { c -> Vector2D(0, c.y) }, Vector2D(1, 0)))
//        areas[0].connections.add(AreaConnection(areas[2], { c -> Vector2D(0, areaSize - 1 - c.x) }, Vector2D(1, 0)))
//        areas[0].connections.add(AreaConnection(areas[1], { c -> Vector2D(0, areaSize - 1 - c.x) }, Vector2D(1, 0)))
//
//        // 2 -> 1, 3, 5, 6
//        areas[1].connections.add(AreaConnection(areas[2], { c -> Vector2D(c.x, 0) }, Vector2D(0, 1)))
//        areas[1].connections.add(AreaConnection(areas[4], { c -> Vector2D(areaSize - 1, areaSize - 1 - c.y) }, Vector2D(-1, 0)))
//        areas[1].connections.add(AreaConnection(areas[5], { c -> Vector2D(areaSize - 1, areaSize - 1 - c.x) }, Vector2D(-1, 0)))
//        areas[1].connections.add(AreaConnection(areas[0], { c -> Vector2D(0, areaSize - 1 - c.x) }, Vector2D(1, 0)))
//
//        // 3 -> 1, 2, 4, 5
//        areas[2].connections.add(AreaConnection(areas[3], { c -> Vector2D(c.x, 0) }, Vector2D(0, 1)))
//        areas[2].connections.add(AreaConnection(areas[4], { c -> Vector2D(areaSize - 1 - c.y, 0) }, Vector2D(0, 1)))
//        areas[2].connections.add(AreaConnection(areas[1], { c -> Vector2D(c.x, areaSize - 1) }, Vector2D(0, -1)))
//        areas[2].connections.add(AreaConnection(areas[0], { c -> Vector2D(0, areaSize - 1 - c.x) }, Vector2D(0, 1)))
//
//        // 4 -> 1, 3, 5, 6
//        areas[3].connections.add(AreaConnection(areas[5], { c -> Vector2D(0, areaSize - 1 - c.x) }, Vector2D(1, 0)))
//        areas[3].connections.add(AreaConnection(areas[4], { c -> Vector2D(0, c.y) }, Vector2D(1, 0)))
//        areas[3].connections.add(AreaConnection(areas[2], { c -> Vector2D(c.x, areaSize - 1) }, Vector2D(0, -1)))
//        areas[3].connections.add(AreaConnection(areas[0], { c -> Vector2D(areaSize - 1, c.y) }, Vector2D(-1, 0)))
//
//        // 5 -> 2, 3, 4, 6
//        areas[4].connections.add(AreaConnection(areas[5], { c -> Vector2D(c.x, 0) }, Vector2D(0, 1)))
//        areas[4].connections.add(AreaConnection(areas[1], { c -> Vector2D(areaSize - 1, areaSize - 1 - c.y) }, Vector2D(-1, 0)))
//        areas[4].connections.add(AreaConnection(areas[2], { c -> Vector2D(areaSize - 1, areaSize - 1 - c.x) }, Vector2D(-1, 0)))
//        areas[4].connections.add(AreaConnection(areas[3], { c -> Vector2D(areaSize - 1, c.y) }, Vector2D(-1, 0)))
//
//        // 6 -> 1, 2, 4, 5
//        areas[5].connections.add(AreaConnection(areas[0], { c -> Vector2D(areaSize - 1 - c.x, 0) }, Vector2D(0, -1)))
//        areas[5].connections.add(AreaConnection(areas[1], { c -> Vector2D(areaSize - 1 - c.y, 0) }, Vector2D(0, 1)))
//        areas[5].connections.add(AreaConnection(areas[4], { c -> Vector2D(c.x, areaSize - 1) }, Vector2D(0, -1)))
//        areas[5].connections.add(AreaConnection(areas[3], { c -> Vector2D(areaSize - 1 - c.y, areaSize - 1) }, Vector2D(0, -1)))

        // --- REAL INPUT MAPPING ---
        // 1 -> 2, 3, 5, 6
        areas[0].connections.add(AreaConnection(areas[1], { c -> Vector2D(c.x, 0) }, Vector2D(0, 1)))
        areas[0].connections.add(AreaConnection(areas[2], { c -> Vector2D(0, c.y) }, Vector2D(1, 0)))
        areas[0].connections.add(AreaConnection(areas[4], { c -> Vector2D(areaSize - 1 - c.x, 0) }, Vector2D(0, 1)))
        areas[0].connections.add(AreaConnection(areas[5], { c -> Vector2D(c.y, 0) }, Vector2D(0, 1)))

        // 2 -> 1, 3, 4, 6
        areas[1].connections.add(AreaConnection(areas[3], { c -> Vector2D(areaSize - 1 - c.x, areaSize - 1) }, Vector2D(0, -1)))
        areas[1].connections.add(AreaConnection(areas[2], { c -> Vector2D(c.y, areaSize - 1) }, Vector2D(0, -1)))
        areas[1].connections.add(AreaConnection(areas[0], { c -> Vector2D(c.x, areaSize - 1) }, Vector2D(0, -1)))
        areas[1].connections.add(AreaConnection(areas[5], { c -> Vector2D(areaSize - 1, c.y) }, Vector2D(-1, 0)))

        // 3 -> 1, 2, 4, 5
        areas[2].connections.add(AreaConnection(areas[1], { c -> Vector2D(areaSize - 1, c.x) }, Vector2D(-1, 0)))
        areas[2].connections.add(AreaConnection(areas[3], { c -> Vector2D(0, c.y) }, Vector2D(1, 0)))
        areas[2].connections.add(AreaConnection(areas[4], { c -> Vector2D(0, c.x) }, Vector2D(1, 0)))
        areas[2].connections.add(AreaConnection(areas[0], { c -> Vector2D(areaSize - 1, c.y) }, Vector2D(-1, 0)))

        // 4 -> 2, 3, 5, 6
        areas[3].connections.add(AreaConnection(areas[1], { c -> Vector2D(areaSize - 1 - c.x, areaSize - 1) }, Vector2D(0, -1)))
        areas[3].connections.add(AreaConnection(areas[5], { c -> Vector2D(c.y, areaSize - 1) }, Vector2D(0, -1)))
        areas[3].connections.add(AreaConnection(areas[4], { c -> Vector2D(c.x, areaSize - 1) }, Vector2D(0, -1)))
        areas[3].connections.add(AreaConnection(areas[2], { c -> Vector2D(areaSize - 1, c.y) }, Vector2D(-1, 0)))

        // 5 -> 1, 3, 4, 6
        areas[4].connections.add(AreaConnection(areas[3], { c -> Vector2D(c.x, 0) }, Vector2D(0, 1)))
        areas[4].connections.add(AreaConnection(areas[5], { c -> Vector2D(0, c.y) }, Vector2D(1, 0)))
        areas[4].connections.add(AreaConnection(areas[0], { c -> Vector2D(areaSize - 1 - c.x, 0) }, Vector2D(0, 1)))
        areas[4].connections.add(AreaConnection(areas[2], { c -> Vector2D(c.y, 0) }, Vector2D(0, 1)))

        // 6 -> 1, 2, 4, 5
        areas[5].connections.add(AreaConnection(areas[3], { c -> Vector2D(areaSize - 1, c.x) }, Vector2D(-1, 0)))
        areas[5].connections.add(AreaConnection(areas[1], { c -> Vector2D(0, c.y) }, Vector2D(1, 0)))
        areas[5].connections.add(AreaConnection(areas[0], { c -> Vector2D(0, c.x) }, Vector2D(1, 0)))
        areas[5].connections.add(AreaConnection(areas[4], { c -> Vector2D(areaSize - 1, c.y) }, Vector2D(-1, 0)))

        return areas
    }

    private fun getUpdatedPosition(currentPosition: Vector2D, direction: Vector2D, field: MatrixGrid): Vector2D {
        // find neighbors at position
        val activeTile = field.values[currentPosition.x][currentPosition.y]
        // get the index neighbor at the direction that we're looking for
        val neighborIndex = directions.indexOf(direction)
        // check if that neighbor exists
        val neighbor = activeTile.neighbors[neighborIndex]
        if (neighbor != null) {
            // if so, move currentPosition to that neighbor
            return Vector2D(neighbor.x, neighbor.y)
        }
        return currentPosition
    }

    override fun calculateSolutionA(): String {
        field.initializeDirectAndOpposingNeighbors()

        // direction starts to the right
        var direction = Vector2D(0, 1)
        var currentPosition = field.getStartPosition()

        instructions.forEach {
            direction = directions[getNextDirectionIndex(direction, it.rotation)]
            for (i in 0 until it.amount) {
                currentPosition = getUpdatedPosition(currentPosition, direction, field)
            }
        }

        return ((currentPosition.x + 1) * 1000 + (currentPosition.y + 1) * 4 + directions.indexOf(direction)).toString()
    }

    override fun calculateSolutionB(): String {
        val areas = field.getAreas()

        var activeArea = areas[0]

        // direction starts to the right
        var localDirection = Vector2D(0, 1)
        var localPosition = activeArea.getLocalPosition(field.getStartPosition())

        // find in which area the current position is in
        // transform the 'real' direction to the local direction
        // check if the next position will be out of bounds of the area
        // if that's the case, then check which area is linked to that side
        // check if you can access the first node of the neighboring area
        // move position, and set the active area to the neighbor

        for (instruction in instructions) {
            localDirection = directions[getNextDirectionIndex(localDirection, instruction.rotation)]
            val g = activeArea.getGlobalPosition(localPosition)
//            field.values[g.x][g.y].value = 10 + getNextDirectionIndex(localDirection, 0)
//            println(field.toStringCustom())

            // get current area
            for (i in 0 until instruction.amount) {
                val g = activeArea.getGlobalPosition(localPosition)
//                field.values[g.x][g.y].value = 10 + getNextDirectionIndex(localDirection, 0)
//                println(field.toStringCustom())

                val nextPosition = localPosition + localDirection

                if (activeArea.matrix.isWithinBounds(nextPosition)) {
                    if (activeArea.matrix.isWalkableNode(nextPosition)) {
                        // stay in the active area
                        localPosition = nextPosition
                    } else {
                        continue
                    }
                } else {
                    val connection = activeArea.connections[getNextDirectionIndex(localDirection, 0)]

                    // get the transformed position
                    val transformedPosition = connection.transformPosition(localPosition)
                    val transformedDirection = connection.transformedDirection

                    // don't process simple blocking nodes
                    if (!connection.area.matrix.isWalkableNode(transformedPosition)) continue

                    // set active area and update the local position and direction
                    localPosition = transformedPosition
                    localDirection = transformedDirection
                    activeArea = connection.area
                }
            }
        }
        println(field.toStringCustom())
        val globalPosition = activeArea.getGlobalPosition(localPosition)
        return ((globalPosition.x + 1) * 1000 + (globalPosition.y + 1) * 4 + directions.indexOf(localDirection)).toString()
    }

    data class Area(val originalPosition: Vector2D, val x: Int, val y: Int, var connections: MutableList<AreaConnection>, val matrix: MatrixGrid) {
        fun getGlobalPosition(localPosition: Vector2D): Vector2D =
            originalPosition + localPosition

        fun getLocalPosition(globalPosition: Vector2D): Vector2D =
            globalPosition - originalPosition
    }
    data class AreaConnection(
        val area: Area,
        var transformPosition: (coordinate: Vector2D) -> Vector2D,
        var transformedDirection: Vector2D
    )
}
