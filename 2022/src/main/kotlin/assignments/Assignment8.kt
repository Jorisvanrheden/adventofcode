package assignments

import models.matrix.IntMatrix
import models.assignment.Assignment

class Assignment8 : Assignment() {

    private lateinit var trees: IntMatrix

    override fun getInput(): String {
        return "input_8"
    }

    override fun initialize(input: List<String>) {
        trees = IntMatrix(input[0].length, input.size)

        for (i in input[0].indices) {
            for (j in input[i].indices) {
                trees.values[i][j] = input[i][j].digitToInt()
            }
        }
    }

    private fun isVisibleFromTop(matrix: IntMatrix, x: Int, y: Int): Boolean {
        for (j in 0 until y) {
            if (matrix.values[x][j] >= matrix.values[x][y]) return false
        }
        return true
    }
    private fun isVisibleFromBottom(matrix: IntMatrix, x: Int, y: Int): Boolean {
        for (j in y + 1 until matrix.rows) {
            if (matrix.values[x][j] >= matrix.values[x][y]) return false
        }
        return true
    }
    private fun isVisibleFromLeft(matrix: IntMatrix, x: Int, y: Int): Boolean {
        for (i in 0 until x) {
            if (matrix.values[i][y] >= matrix.values[x][y]) return false
        }
        return true
    }
    private fun isVisibleFromRight(matrix: IntMatrix, x: Int, y: Int): Boolean {
        for (i in x + 1 until matrix.columns) {
            if (matrix.values[i][y] >= matrix.values[x][y]) return false
        }
        return true
    }

    private fun isVisibleFromOutside(matrix: IntMatrix, x: Int, y: Int): Boolean {
        if (isVisibleFromTop(matrix, x, y)) return true
        if (isVisibleFromBottom(matrix, x, y)) return true
        if (isVisibleFromLeft(matrix, x, y)) return true
        if (isVisibleFromRight(matrix, x, y)) return true

        return false
    }

    private fun getViewDistanceInDirection(matrix: IntMatrix, x: Int, y: Int, dirX: Int, dirY: Int): Int {
        var distance = 0
        var newX = x + dirX
        var newY = y + dirY

        while ((newX >= 0 && newX < matrix.columns) && (newY >= 0 && newY < matrix.rows)) {
            if (matrix.values[newX][newY] >= matrix.values[x][y]) {
                distance += 1
                break
            }

            newX += dirX
            newY += dirY
            distance += 1
        }
        return distance
    }

    private fun getScenicViewDistance(matrix: IntMatrix, x: Int, y: Int): Int {
        // look at each direction
        val left = getViewDistanceInDirection(matrix, x, y, -1, 0)
        val right = getViewDistanceInDirection(matrix, x, y, 1, 0)
        val top = getViewDistanceInDirection(matrix, x, y, 0, -1)
        val bottom = getViewDistanceInDirection(matrix, x, y, 0, 1)

        // multiply distances
        return left * right * top * bottom
    }

    override fun calculateSolutionA(): String {
        return trees.values.mapIndexed { i, row ->
            row.mapIndexed { j, _ ->
                if (isVisibleFromOutside(trees, i, j)) 1
                else 0
            }
        }
            .flatten()
            .sum()
            .toString()
    }

    override fun calculateSolutionB(): String {
        return trees.values.mapIndexed { i, row ->
            row.mapIndexed { j, _ ->
                getScenicViewDistance(trees, i, j)
            }
        }
            .flatten()
            .maxOrNull()
            .toString()
    }
}
