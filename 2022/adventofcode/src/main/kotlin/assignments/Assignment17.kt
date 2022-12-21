package assignments

import toolkit.Matrix
import toolkit.Vector2D

class Assignment17 : Assignment() {

    override fun getInput(): String {
        return "input_17"
    }

    private lateinit var directions: List<Int>

    data class Block(val matrix: Matrix, var location: Vector2D) {
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
        var matrix = Matrix(1, 4)
        for (i in 0 until matrix.columns) matrix.values[0][i] = 1
        return Block(matrix, Vector2D(0, 0))
    }

    private fun cross(): Block {
        var matrix = Matrix(3, 3)
        matrix.values[0][1] = 1
        matrix.values[1][0] = 1
        matrix.values[1][1] = 1
        matrix.values[1][2] = 1
        matrix.values[2][1] = 1
        return Block(matrix, Vector2D(0, 0))
    }

    private fun corner(): Block {
        var matrix = Matrix(3, 3)
        matrix.values[0][2] = 1
        matrix.values[1][2] = 1
        matrix.values[2][2] = 1
        matrix.values[2][1] = 1
        matrix.values[2][0] = 1
        return Block(matrix, Vector2D(0, 0))
    }

    private fun vertical(): Block {
        var matrix = Matrix(4, 1)
        for (i in 0 until matrix.rows) {
            matrix.values[i][0] = 1
        }
        return Block(matrix, Vector2D(0, 0))
    }

    private fun block(): Block {
        var matrix = Matrix(2, 2)
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

    private fun Matrix.moveBlock(block: Block, vector2D: Vector2D) {
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

    private fun Matrix.isOnFloor(block: Block): Boolean {
        // get lowest y coordinates
        val coordinates = block.getCoordinates()

        // check if matrix has value at coordinate + 1
        for (coordinate in coordinates) {
            if (coordinate.y >= rows - 1) return true
            if (values[coordinate.y + 1][coordinate.x] != 0) return true
        }

        return false
    }

    private fun Matrix.addBlock(block: Block) {
        for (i in 0 until block.matrix.rows) {
            for (j in 0 until block.matrix.columns) {
                val blockValue = block.matrix.values[i][j]
                if (blockValue != 0) {
                    values[i + block.location.y][j + block.location.x] = blockValue
                }
            }
        }
    }

    private fun Matrix.getBottomY(): Int {
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

    override fun calculateSolutionA(): String {
        var currentBlockIndex = 0
        var matrix = Matrix(3500, 7)

        var cachedShapes = mutableListOf<Block>()

        var activeBlock = getNextBlock(currentBlockIndex, matrix.getBottomY())

        var count = 1
        var dirIndex = 0
        while (true) {
            // process direction (only applicable to current block)
            matrix.moveBlock(activeBlock, Vector2D(directions[dirIndex], 0))
            dirIndex++
            dirIndex %= directions.size
            println(dirIndex)

            // if the current block is already on the floor, get the next one
            if (matrix.isOnFloor(activeBlock)) {
                // permanently etch the block in the matrix
                matrix.addBlock(activeBlock)

                if (count == 2022) {
//                    println(matrix.toString())
                    break
                }
                count++

                cachedShapes.add(activeBlock)

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
        return ""
    }
}
