package assignments

import models.assignment.Assignment
import models.vector.Vector2DLong
import utilities.Utilities

class Assignment13 : Assignment(13) {
    private data class Entry(
        val buttonA: Vector2DLong,
        val buttonB: Vector2DLong,
        val prizeLocation: Vector2DLong,
    )
    private lateinit var entries: List<Entry>

    private fun String.toCoord() =
        split(':')[1].split(',').filter { it.isNotEmpty() }.let {
            Vector2DLong(
                it[0].split('+')[1].toLong(),
                it[1].split('+')[1].toLong(),
            )
        }

    override fun initialize(input: List<String>) {
        val chunks = Utilities.packageByEmptyLine(input)
        entries = chunks.map {
            Entry(
                it[0].toCoord(),
                it[1].toCoord(),
                it[2].split(':')[1].split(',').filter { it.isNotEmpty() }.let {
                    Vector2DLong(
                        it[0].split('=')[1].toLong(),
                        it[1].split('=')[1].toLong(),
                    )
                }
            )
        }
    }

    override fun calculateSolutionA(): String {
        return entries
            .sumOf { simulate(it) }
            .toString()
    }

    override fun calculateSolutionB(): String {
        return entries
            .sumOf {
                simulate(
                    it.copy(prizeLocation = it.prizeLocation + Vector2DLong(10000000000000, 10000000000000))
                )
            }
            .toString()
    }

    private fun simulate(entry: Entry): Long {
        val px = entry.prizeLocation.x
        val py = entry.prizeLocation.y

        val ax = entry.buttonA.x
        val ay = entry.buttonA.y

        val bx = entry.buttonB.x
        val by = entry.buttonB.y

        val A = (px * by - py * bx) / (ax * by - ay * bx)
        val B = (py - A * ay) / by

        if (A * ax + B * bx != px || A * ay + B * by != py) {
            return 0
        }

        return A * 3 + B * 1
    }
}

