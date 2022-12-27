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

    private fun MatrixGrid.getNeighbors(startPosition: Vector2D): List<MatrixGridTile?> {
        if (!isWalkableNode(startPosition)) return List<MatrixGridTile?>(4) { null }

        println("Processing $startPosition")

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

                println("Original $startPosition -- Opposing $opposingNodePosition")

                if (isWalkableNode(opposingNodePosition)) {
                    neighbors[index] = values[opposingNodePosition.x][opposingNodePosition.y]
                }
            }
        }
        return neighbors
    }

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

        for (i in 0 until matrix.rows) {
            for (j in 0 until matrix.columns) {
                matrix.values[i][j].neighbors = matrix.getNeighbors(Vector2D(i, j))
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

    private fun valueToCharacter(value: Int) =
        when (value) {
            10 -> '>'
            11 -> 'v'
            12 -> '<'
            13 -> '^'
            else -> 'x'
        }

    override fun calculateSolutionA(): String {
        // direction starts to the right
        var direction = Vector2D(0, 1)

        var currentPosition = field.getStartPosition()
        field.values[currentPosition.x][currentPosition.y].value = directions.indexOf(direction) + 10

        for (instruction in instructions) {
            if (instruction.rotation != 0) {
                direction = directions[getNextDirectionIndex(direction, instruction.rotation)]

                // update facing?
                field.values[currentPosition.x][currentPosition.y].value = directions.indexOf(direction) + 10
//                println(field.toStringCustom())
            }

            for (i in 0 until instruction.amount) {
                // find neighbors at position
                val activeTile = field.values[currentPosition.x][currentPosition.y]
                // get the index neighbor at the direction that we're looking for
                val neighborIndex = directions.indexOf(direction)
                // check if that neighbor exists
                val neighbor = activeTile.neighbors[neighborIndex]
                if (neighbor != null) {
                    // if so, move currentPosition to that neighbor
                    currentPosition = Vector2D(neighbor.x, neighbor.y)

                    field.values[currentPosition.x][currentPosition.y].value = directions.indexOf(direction) + 10
                }

//                println(field.toStringCustom())
            }
        }

        return ((currentPosition.x + 1) * 1000 + (currentPosition.y + 1) * 4 + directions.indexOf(direction)).toString()
    }

    override fun calculateSolutionB(): String {
        return ""
    }
}
