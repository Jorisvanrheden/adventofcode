package assignments

import toolkit.Vector2DLong
import kotlin.math.abs

class Assignment11 : Assignment() {
    override fun getInput(): String {
        return "input_11"
    }

    data class Galaxy(
        val originalCoordinate: Vector2DLong,
        var expandedCoordinate: Vector2DLong,
    )

    private lateinit var galaxies: MutableList<Galaxy>
    private lateinit var input: List<String>

    override fun initialize(input: List<String>) {
        this.input = input

        galaxies = mutableListOf()
        input.forEachIndexed { i, _ ->
            input[i].forEachIndexed { j, c ->
                if (c == '#') {
                    galaxies.add(
                        Galaxy(
                            Vector2DLong(i.toLong(), j.toLong()),
                            Vector2DLong(i.toLong(), j.toLong()),
                        ),
                    )
                }
            }
        }
    }

    override fun calculateSolutionA() =
        galaxies
            .expand(1, input)
            .sumOfDistances()
            .toString()

    override fun calculateSolutionB() =
        galaxies
            .expand(1000000, input)
            .sumOfDistances()
            .toString()

    private fun List<Galaxy>.sumOfDistances(): Long {
        var total = 0L
        for (i in indices) {
            for (j in (i + 1) until size) {
                val dx = abs(this[i].expandedCoordinate.x - this[j].expandedCoordinate.x)
                val dy = abs(this[i].expandedCoordinate.y - this[j].expandedCoordinate.y)
                total += dx + dy
            }
        }
        return total
    }

    private fun List<Galaxy>.expand(expansionRate: Int, input: List<String>): List<Galaxy> {
        // reset coordinates
        for (galaxy in galaxies) {
            galaxy.expandedCoordinate = galaxy.originalCoordinate.copy()
        }

        for (i in input.indices) {
            if (!input[i].contains('#')) {
                // all coordinates after this original X should be += expansionRate
                for (galaxy in galaxies) {
                    if (galaxy.originalCoordinate.x > i) {
                        galaxy.expandedCoordinate.x += expansionRate
                    }
                }
            }
        }

        for (j in input[0].indices) {
            val column = input.map { it[j] }
            if (!column.contains('#')) {
                for (galaxy in galaxies) {
                    if (galaxy.originalCoordinate.y > j) {
                        galaxy.expandedCoordinate.y += expansionRate
                    }
                }
            }
        }
        return this
    }
}
