package assignments

import models.MatrixChar
import toolkit.Vector2D

class Assignment14 : Assignment() {

    override fun getInput(): String {
        return "input_14"
    }

    private lateinit var matrix: MatrixChar

    private lateinit var coordinates: List<Vector2D>

    override fun initialize(input: List<String>) {
        matrix = MatrixChar(input.size, input[0].length)

        for (i in input.indices) {
            for (j in input[0].indices) {
                matrix.values[i][j] = input[i][j]
            }
        }

        val coords = mutableListOf<Vector2D>()
        matrix.forEach { i, j ->
            if (matrix.values[i][j] == 'O') {
                coords.add(Vector2D(i, j))
            }
        }
        coordinates = coords.toList()
    }

    override fun calculateSolutionA(): String {
        matrix.tilt(Vector2D(0,0).up())

        var score = 0
        for (i in 0 until matrix.rows) {
            var count = 0
            for (j in 0 until matrix.columns) {
                if (matrix.values[i][j] == 'O') {
                    count++
                }
            }
            score += count * (matrix.rows - i)
        }

        return score.toString()
    }

    override fun calculateSolutionB(): String {
        return ""
    }

    private fun MatrixChar.tilt(direction: Vector2D) {
        // sort vertically
        coordinates.sortedBy { it.x }

        coordinates.forEach {
            // remove
            values[it.x][it.y] = '.'

            // find obstacle
            var last = it
            var next = it + direction
            while (isWithinBounds(next) && values[next.x][next.y] == '.') {
                last = next
                next += direction
            }
            // unset active val
            values[last.x][last.y] = 'O'
        }

//        for (j in 0 until columns) {
//            for (i in 0 until rows) {
//                if (values[i][j] == 'O') {
//                    var current = Vector2D(i, j)
//                    var goal = Vector2D(i, j) + direction
//                    while (isWithinBounds(goal) && values[goal.x][goal.y] == '.'){
//                        values[current.x][current.y] = '.'
//                        values[goal.x][goal.y] = 'O'
//                        current = goal
//                        goal = goal.up()
//                    }
//                }
//            }
//        }
    }
}
