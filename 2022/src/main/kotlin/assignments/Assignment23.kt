package assignments

import models.assignment.Assignment
import models.vector.Vector2D

class Assignment23 : Assignment(23) {
    data class Elf(
        var position: Vector2D,
        var directionIndex: Int = 0
    ) {
        override fun equals(other: Any?): Boolean {
            return position == (other as Elf).position
        }

        fun copy() = Elf(Vector2D(position.x, position.y))
    }

    private val directions = listOf(
        Vector2D(-1, 0),
        Vector2D(1, 0),
        Vector2D(0, -1),
        Vector2D(0, 1)
    )

    private val directionValidationPositions = mapOf(
        Vector2D(0, 1) to listOf(Vector2D(-1, 1), Vector2D(0, 1), Vector2D(1, 1)),
        Vector2D(0, -1) to listOf(Vector2D(-1, -1), Vector2D(0, -1), Vector2D(1, -1)),
        Vector2D(1, 0) to listOf(Vector2D(1, -1), Vector2D(1, 0), Vector2D(1, 1)),
        Vector2D(-1, 0) to listOf(Vector2D(-1, -1), Vector2D(-1, 0), Vector2D(-1, 1))
    )

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

    private fun Set<Elf>.openPositionCount(): Int {
        val xRange = maxOf { it.position.x } - minOf { it.position.x } + 1
        val yRange = maxOf { it.position.y } - minOf { it.position.y } + 1

        return xRange * yRange - size
    }

    private fun hasSurroundingElves(elf: Elf, elves: Set<Elf>): Boolean {
        // build a square and check taken positions in that square
        for (i in 0 until 3) {
            for (j in 0 until 3) {
                val position = Vector2D(i - 1, j - 1) + elf.position
                if (position == elf.position) continue
                if (elves.any { it.position == position }) return true
            }
        }
        return false
    }

    private fun isOnPosition(requiredOpenPositions: List<Vector2D>, elves: Set<Elf>): Boolean {
        for (position in requiredOpenPositions) {
            if (elves.any { it.position == position }) return true
        }
        return false
    }

    private fun findProposedDirection(elf: Elf, elves: Set<Elf>): Vector2D {
        for (i in directions.indices) {
            val index = (i + elf.directionIndex).mod(directions.size)
            val requiredOpenPositions = directionValidationPositions[directions[index]]!!.map { it + elf.position }
            if (isOnPosition(requiredOpenPositions, elves)) continue
            return directions[index]
        }
        return Vector2D(0, 0)
    }

    private fun List<Elf>.updatePositions(proposedDirections: List<Vector2D>, proposedPositions: List<Vector2D>) {
        forEachIndexed { index, it ->
            val proposedDirection = proposedDirections[index]
            if (proposedDirection != Vector2D(0, 0)) {
                // only process the move if only one elf goes to the position
                if (proposedPositions.count { pos ->
                    pos == (proposedDirection + it.position)
                } == 1
                ) {
                    // move elf to desired position
                    it.position += proposedDirection
                }
            }
        }
    }

    private fun simulateRound(elvesToProcess: List<Elf>, elves: Set<Elf>) {
        var proposedDirections = elvesToProcess.map {
            findProposedDirection(it, elves)
        }

        // store proposed positions per elf
        var proposedPositions = elvesToProcess.mapIndexed { index, it ->
            it.position + proposedDirections[index]
        }

        elvesToProcess.updatePositions(proposedDirections, proposedPositions)

        elves.forEach {
            // increase the direction index (same as moving the first item to the back, but faster)
            it.directionIndex++
        }
    }

    override fun calculateSolutionA(): String {
        var elvesCopy = elves.map { it.copy() }.toSet()

        for (i in 0 until 10) {
            // only process elves that have surrounding elves
            val elvesToProcess = elvesCopy.filter { hasSurroundingElves(it, elvesCopy) }
            simulateRound(elvesToProcess, elvesCopy)
        }
        return elvesCopy.openPositionCount().toString()
    }

    override fun calculateSolutionB(): String {
        var elvesCopy = elves.map { it.copy() }.toSet()

        var turns = 1
        var elvesToProcess = elvesCopy.filter { hasSurroundingElves(it, elvesCopy) }
        while (elvesToProcess.isNotEmpty()) {
            turns++
            simulateRound(elvesToProcess, elvesCopy)
            elvesToProcess = elvesCopy.filter { hasSurroundingElves(it, elvesCopy) }
            println(turns)
        }
        return turns.toString()
    }
}
