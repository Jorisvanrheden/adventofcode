package assignments

import toolkit.Matrix
import toolkit.Vector2D

class Assignment23 : Assignment() {

    override fun getInput(): String {
        return "input_23"
    }

    data class Elf(
        var position: Vector2D,
        var directions: MutableList<Vector2D> = mutableListOf(
            Vector2D(-1, 0),
            Vector2D(1, 0),
            Vector2D(0, -1),
            Vector2D(0, 1)
        )
    ) {
        override fun equals(other: Any?): Boolean {
            return position == (other as Elf).position
        }
    }

    private var elves: MutableSet<Elf> = mutableSetOf()

    override fun initialize(input: List<String>) {
        for (i in input.indices) {
            for (j in input[i].indices) {
                if (input[i][j] == '#') {
                    elves.add(Elf(Vector2D(i, j)))
                }
            }
        }
    }

    private fun MutableSet<Elf>.toCustomString(): String {
        val xMin = minOf { it.position.x }
        val xMax = maxOf { it.position.x }
        val yMin = minOf { it.position.y }
        val yMax = maxOf { it.position.y }

        var matrix = Matrix(xMax - xMin + 1, yMax - yMin + 1)
        forEachIndexed { index, it ->
            var value = 1
            if (index == 5) value = 99
            matrix.values[it.position.x - xMin][it.position.y - yMin] = value
        }
        return matrix.toString()
    }

    private fun MutableSet<Elf>.openPositionCount(): Int {
        val xRange = maxOf { it.position.x } - minOf { it.position.x } + 1
        val yRange = maxOf { it.position.y } - minOf { it.position.y } + 1

        return xRange * yRange - size
    }

    private fun hasSurroundingElves(elf: Elf, elves: Set<Elf>): Boolean {
        // build a cube and check taken positions in that cube
        for (i in 0 until 3) {
            for (j in 0 until 3) {
                val position = Vector2D(i - 1, j - 1) + elf.position
                if (position == elf.position) continue
                if (elves.any { it.position == position }) return true
            }
        }
        return false
    }

    private fun getDirectionValidationSet(direction: Vector2D) =
        when (direction) {
            Vector2D(0, 1) -> listOf(Vector2D(-1, 1), Vector2D(0, 1), Vector2D(1, 1))
            Vector2D(0, -1) -> listOf(Vector2D(-1, -1), Vector2D(0, -1), Vector2D(1, -1))
            Vector2D(1, 0) -> listOf(Vector2D(1, -1), Vector2D(1, 0), Vector2D(1, 1))
            Vector2D(-1, 0) -> listOf(Vector2D(-1, -1), Vector2D(-1, 0), Vector2D(-1, 1))
            else -> emptyList()
        }

    private fun isOnPosition(requiredOpenPositions: List<Vector2D>, elves: Set<Elf>): Boolean {
        for (position in requiredOpenPositions) {
            if (elves.any { it.position == position }) return true
        }
        return false
    }

    private fun findProposedDirection(elf: Elf, elves: Set<Elf>): Vector2D {
        for (direction in elf.directions) {
            val requiredOpenPositions = getDirectionValidationSet(direction).map { it + elf.position }
            if (isOnPosition(requiredOpenPositions, elves)) continue
            return direction
        }
        return Vector2D(0, 0)
    }

    override fun calculateSolutionA(): String {
        return ""
        for (i in 0 until 10) {
            // only process elves that have surrounding elves
            val elvesToProcess = elves.filter { hasSurroundingElves(it, elves) }

            // store proposed positions per elf
            var proposedPositions = elvesToProcess.map {
                it.position + findProposedDirection(it, elves)
            }
            var proposedDirections = elvesToProcess.map {
                findProposedDirection(it, elves)
            }

            elvesToProcess.forEachIndexed { index, it ->
                val proposedDirection = proposedDirections[index]
                // only process the move if only one elf goes to the position
                if (proposedPositions.count { pos ->
                    pos == (proposedDirection + it.position)
                } == 1
                ) {
                    // move elf to desired position
                    it.position += proposedDirection
                }
            }

            elves.forEach {
                // remove the first direction and place it at the end
                it.directions.add(
                    it.directions.removeAt(0)
                )
            }
        }

        return elves.openPositionCount().toString()
    }

    override fun calculateSolutionB(): String {
        var turns = 0
        while(elves.count { hasSurroundingElves(it, elves) } > 0) {
            println(turns)
            turns++
            // only process elves that have surrounding elves
            val elvesToProcess = elves.filter { hasSurroundingElves(it, elves) }

            // store proposed positions per elf
            var proposedPositions = elvesToProcess.map {
                it.position + findProposedDirection(it, elves)
            }
            var proposedDirections = elvesToProcess.map {
                findProposedDirection(it, elves)
            }

            elvesToProcess.forEachIndexed { index, it ->
                val proposedDirection = proposedDirections[index]
                // only process the move if only one elf goes to the position
                if (proposedPositions.count { pos ->
                        pos == (proposedDirection + it.position)
                    } == 1
                ) {
                    // move elf to desired position
                    it.position += proposedDirection
                }
            }

            elves.forEach {
                // remove the first direction and place it at the end
                it.directions.add(
                    it.directions.removeAt(0)
                )
            }
        }
        return turns.toString()
    }
}
