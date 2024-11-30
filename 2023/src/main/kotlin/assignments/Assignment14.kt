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

    override fun calculateSolutionA() =
        matrix
            .tilt(Vector2D(0,0).up())
            .calculateScore()
            .toString()

    override fun calculateSolutionB(): String {
        val scores = mutableListOf<Int>()
        var index = 0
        var startLoopIndex = 0
        var loop = mutableListOf<Int>()

        while (true) {
            matrix.tilt(Vector2D(0,0).up())
            matrix.tilt(Vector2D(0,0).left())
            matrix.tilt(Vector2D(0,0).down())
            matrix.tilt(Vector2D(0,0).right())

            val score = matrix.calculateScore()

            val compareChunkSize = 3
            for (i in 0 until scores.size - compareChunkSize) {
                // find first occurrence of duplicated entries
                val occurrenceIndices = scores.findCollectionOccurrenceIndices(
                    scores.subList(i, i + compareChunkSize)
                )
                if (occurrenceIndices.size > 1) {
                    // loop start has been found
                    startLoopIndex = i

                    // find loop
                    loop = scores.subList(occurrenceIndices[0], occurrenceIndices[1])
                    break
                }
            }

            if (loop.isNotEmpty()) {
                break
            }

            // store all scores
            scores.add(score)

            index++
        }

        return loop[(1000000000 - startLoopIndex - 1) % loop.size].toString()
    }

    private fun List<Int>.findCollectionOccurrenceIndices(collection: List<Int>): List<Int> {
        val indices = mutableListOf<Int>()
        for (i in 0 until size - collection.size) {
            if (subList(i, i + collection.size) == collection) {
                indices.add(i)
            }
        }
        return indices
    }

    private fun MatrixChar.calculateScore(): Int {
        var score = 0
        for (i in 0 until rows) {
            var count = 0
            for (j in 0 until columns) {
                if (values[i][j] == 'O') {
                    count++
                }
            }
            score += count * (rows - i)
        }
        return score
    }

    private fun MatrixChar.tilt(direction: Vector2D): MatrixChar {
        // sort
        if (direction.x == 1 ) {
            coordinates = coordinates.sortedByDescending { it.x }
        } else if (direction.x == -1 ) {
            coordinates = coordinates.sortedBy { it.x }
        } else if (direction.y == 1 ) {
            coordinates = coordinates.sortedByDescending { it.y }
        } else if (direction.y == -1 ) {
            coordinates = coordinates.sortedBy { it.y }
        }

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

            it.x = last.x
            it.y = last.y
        }
        return this
    }
}
