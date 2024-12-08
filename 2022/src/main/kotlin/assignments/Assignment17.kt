package assignments

import models.matrix.IntMatrix
import models.assignment.Assignment
import models.vector.Vector2D
import kotlin.math.abs

class Assignment17 : Assignment(17) {
    private lateinit var directions: List<Int>

    data class Block(val matrix: IntMatrix, var location: Vector2D) {
        fun getCoordinates(): List<Vector2D> {
            var coordinates = mutableListOf<Vector2D>()
            for (i in 0 until matrix.rows) {
                for (j in 0 until matrix.columns) {
                    // store all shape coordinates
                    if (matrix.values[i][j] != 0) {
                        coordinates.add(
                            Vector2D(
                                location.x + j,
                                location.y + i
                            )
                        )
                    }
                }
            }
            return coordinates
        }
    }

    private fun horizontal(): Block {
        var matrix = IntMatrix(1, 4)
        for (i in 0 until matrix.columns) matrix.values[0][i] = 1
        return Block(matrix, Vector2D(0, 0))
    }

    private fun cross(): Block {
        var matrix = IntMatrix(3, 3)
        matrix.values[0][1] = 1
        matrix.values[1][0] = 1
        matrix.values[1][1] = 1
        matrix.values[1][2] = 1
        matrix.values[2][1] = 1
        return Block(matrix, Vector2D(0, 0))
    }

    private fun corner(): Block {
        var matrix = IntMatrix(3, 3)
        matrix.values[0][2] = 1
        matrix.values[1][2] = 1
        matrix.values[2][2] = 1
        matrix.values[2][1] = 1
        matrix.values[2][0] = 1
        return Block(matrix, Vector2D(0, 0))
    }

    private fun vertical(): Block {
        var matrix = IntMatrix(4, 1)
        for (i in 0 until matrix.rows) {
            matrix.values[i][0] = 1
        }
        return Block(matrix, Vector2D(0, 0))
    }

    private fun block(): Block {
        var matrix = IntMatrix(2, 2)
        for (i in 0 until matrix.rows) {
            for (j in 0 until matrix.columns) {
                matrix.values[i][j] = 1
            }
        }
        return Block(matrix, Vector2D(0, 0))
    }

    private var shapes = listOf(
        horizontal(),
        cross(),
        corner(),
        vertical(),
        block()
    )

    override fun initialize(input: List<String>) {
        var dirs = mutableListOf<Int>()
        for (c in input[0]) {
            if (c == '<') dirs.add(-1)
            if (c == '>') dirs.add(1)
        }
        directions = dirs
    }

    private fun IntMatrix.moveBlock(block: Block, vector2D: Vector2D) {
        // validate if the block can move in its entirety
        val coordinates = block.getCoordinates().map { it + vector2D }

        // check if any of these coordinates are already occupied or out of bounds
        for (c in coordinates) {
            if (!isWithinBounds(c)) return
            if (values[c.y][c.x] != 0) return
        }

        // only after that we can update the location
        block.location += vector2D
    }

    private fun IntMatrix.isOnFloor(block: Block): Boolean {
        // get lowest y coordinates
        val coordinates = block.getCoordinates()

        // check if matrix has value at coordinate + 1
        for (coordinate in coordinates) {
            if (coordinate.y >= rows - 1) return true
            if (values[coordinate.y + 1][coordinate.x] != 0) return true
        }

        return false
    }

    private fun IntMatrix.addBlock(block: Block) {
        for (i in 0 until block.matrix.rows) {
            for (j in 0 until block.matrix.columns) {
                val blockValue = block.matrix.values[i][j]
                if (blockValue != 0) {
                    values[i + block.location.y][j + block.location.x] = blockValue
                }
            }
        }
    }

    private fun IntMatrix.getBottomY(): Int {
        for (i in rows - 1 downTo 0) {
            var isEmptyRow = true
            for (j in 0 until columns) {
                if (values[i][j] != 0) isEmptyRow = false
            }
            if (isEmptyRow) {
                return i + 1
            }
        }
        return -1
    }

    private fun getNextBlock(currentBlockIndex: Int, floor: Int): Block {
        var block = shapes[currentBlockIndex].copy()
        block.location.x = 2
        // force floor-amount of units from the bottom
        block.location.y = floor - block.matrix.rows - 3
        return block
    }

    private fun IntMatrix.rowsToString(rowCount: Int): String {
        val lowestY = getBottomY()
        if (abs(rows - lowestY) <= rowCount) return ""

        var output = ""
        for (i in 0..rowCount) {
            val rowIndex = i + lowestY
            for (j in 0 until columns) {
                var c = "."
                if (values[rowIndex][j] > 0) c = "#"
                output += c
            }
        }
        return output
    }

    private fun IntMatrix.copy(): IntMatrix {
        val copy = IntMatrix(rows, columns)
        for (i in 0 until rows) {
            for (j in 0 until columns) {
                copy.values[i][j] = values[i][j]
            }
        }
        return copy
    }

    override fun calculateSolutionA(): String {
        var currentBlockIndex = 0
        var matrix = IntMatrix(3500, 7)
        var activeBlock = getNextBlock(currentBlockIndex, matrix.getBottomY())

        var count = 1
        var dirIndex = 0
        while (true) {
            // process direction (only applicable to current block)
            matrix.moveBlock(activeBlock, Vector2D(directions[dirIndex], 0))
            dirIndex++
            dirIndex %= directions.size

            // if the current block is already on the floor, get the next one
            if (matrix.isOnFloor(activeBlock)) {
                // permanently etch the block in the matrix
                matrix.addBlock(activeBlock)

                if (count == 2022) {
                    break
                }
                count++

                currentBlockIndex++
                currentBlockIndex %= shapes.size
                activeBlock = getNextBlock(currentBlockIndex, matrix.getBottomY())
            } else {
                // drop block one unit down
                matrix.moveBlock(activeBlock, Vector2D(0, 1))
            }
        }

        return (matrix.rows - matrix.getBottomY()).toString()
    }

    override fun calculateSolutionB(): String {
        var map = HashMap<State, Answer>()

        var currentBlockIndex = 0
        var matrix = IntMatrix(3500, 7)
        var activeBlock = getNextBlock(currentBlockIndex, matrix.getBottomY())

        var rockDelta = 0

        while (true) {
            for ((dirIndex, direction) in directions.withIndex()) {
                // process direction (only applicable to current block)
                matrix.moveBlock(activeBlock, Vector2D(direction, 0))

                // if the current block is already on the floor, get the next one
                if (matrix.isOnFloor(activeBlock)) {
                    // permanently etch the block in the matrix
                    matrix.addBlock(activeBlock)

                    rockDelta++

                    currentBlockIndex++
                    currentBlockIndex %= shapes.size
                    activeBlock = getNextBlock(currentBlockIndex, matrix.getBottomY())
                } else {
                    // drop block one unit down
                    matrix.moveBlock(activeBlock, Vector2D(0, 1))
                }

                var mCopy = matrix.copy()
                mCopy.addBlock(activeBlock)
                var mKey = mCopy.rowsToString(20)
                var mBottom = mCopy.getBottomY()

                val key = State(dirIndex, currentBlockIndex, mKey)

                if (map.containsKey(key)) {
                    val x = map[key]!!

                    val requiredRocks = 1000000000000L
                    var preLoopRockCount = rockDelta
                    var preLoopHeight = mBottom

                    var diffRocksPerLoop = abs(rockDelta - x.rocks).toLong()
                    var diffHeightPerLoop = abs(mBottom - x.height).toLong()

                    // this value is rounded down, so there is going to be some remainder
                    var loopsNecessary = ((requiredRocks - preLoopRockCount) / diffRocksPerLoop)

                    var afterLoopRockCount = loopsNecessary * diffRocksPerLoop// + preLoopRockCount
                    var afterLoopHeight = loopsNecessary * diffHeightPerLoop// + preLoopHeight

                    var remainderRocks = requiredRocks - afterLoopRockCount

                    // find what percentage the remainder is of the diffRocksPerLoop
                    var percentageRocksRemainder = remainderRocks.toFloat() / diffRocksPerLoop.toFloat()
                    // then multiply that percentage with diffHeightPerLoop
                    var remainderHeight = (percentageRocksRemainder * diffHeightPerLoop).toInt()

                    // find how much height needs to be added
                    var totalHeight = afterLoopHeight + remainderHeight
                    // adding 3 for the distance from the highest shape to the top of the 'map'
                    totalHeight += 3

                    return totalHeight.toString()
                } else {
                    map[key] = Answer(rockDelta, mBottom, mKey)
                }
            }
        }

        return (matrix.rows - matrix.getBottomY()).toString()
    }
    data class Answer(val rocks: Int, val height: Int, val fields: String)
    data class State(val jet: Int, val block: Int, val fields: String)
}
