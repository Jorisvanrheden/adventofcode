package assignments

import toolkit.Matrix

class Assignment8 : Assignment() {

    private lateinit var trees: Matrix

    override fun getInput(): String {
        return "input_8"
    }

    override fun initialize(input: List<String>) {
        trees = Matrix(input[0].length, input.size)

        for (i in input[0].indices) {
            for (j in input[i].indices) {
                trees.values[i][j] = input[i][j].digitToInt()
            }
        }
    }

    private fun isVisibleFromTop(matrix: Matrix, x: Int, y: Int): Boolean {
        for (j in 0 until y) {
            if (matrix.values[x][j] >= matrix.values[x][y]) return false
        }
        return true
    }
    private fun isVisibleFromBottom(matrix: Matrix, x: Int, y: Int): Boolean {
        for (j in y + 1 until matrix.rows) {
            if (matrix.values[x][j] >= matrix.values[x][y]) return false
        }
        return true
    }
    private fun isVisibleFromLeft(matrix: Matrix, x: Int, y: Int): Boolean {
        for (i in 0 until x) {
            if (matrix.values[i][y] >= matrix.values[x][y]) return false
        }
        return true
    }
    private fun isVisibleFromRight(matrix: Matrix, x: Int, y: Int): Boolean {
        for (i in x + 1 until matrix.columns) {
            if (matrix.values[i][y] >= matrix.values[x][y]) return false
        }
        return true
    }

    private fun isVisibleFromOutside(matrix: Matrix, x: Int, y: Int): Boolean {
        if (isVisibleFromTop(matrix, x, y)) return true
        if (isVisibleFromBottom(matrix, x, y)) return true
        if (isVisibleFromLeft(matrix, x, y)) return true
        if (isVisibleFromRight(matrix, x, y)) return true

        return false
    }

    private fun getViewDistanceInDirection(matrix: Matrix, x: Int, y: Int, dirX: Int, dirY: Int): Int {
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

    private fun getScenicViewDistance(matrix: Matrix, x: Int, y: Int): Int {
        // look at each direction
        val left = getViewDistanceInDirection(matrix, x, y, -1, 0)
        val right = getViewDistanceInDirection(matrix, x, y, 1, 0)
        val top = getViewDistanceInDirection(matrix, x, y, 0, -1)
        val bottom = getViewDistanceInDirection(matrix, x, y, 0, 1)

        // multiply distances
        return left * right * top * bottom
    }

    override fun calculateSolutionA(): String {
        var total = 0
        for (i in 0 until trees.rows) {
            for (j in 0 until trees.columns) {
                if (isVisibleFromOutside(trees, i, j)) total += 1
            }
        }
        return total.toString()
    }

    override fun calculateSolutionB(): String {
        var maxDistance = 0
        for (i in 0 until trees.rows) {
            for (j in 0 until trees.columns) {
                val distance = getScenicViewDistance(trees, i, j)
                if (distance > maxDistance) {
                    maxDistance = distance
                }
            }
        }
        return maxDistance.toString()
    }
}
