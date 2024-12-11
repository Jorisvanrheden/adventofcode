package assignments

import models.assignment.Assignment

class Assignment11 : Assignment(11) {
    private lateinit var stones: List<Long>
    private val map = mutableMapOf<Pair<Long, Int>, Long>()

    override fun initialize(input: List<String>) {
        stones = input.first().split(" ").map { it.toLong() }
    }

    override fun calculateSolutionA(): String {
        return stones
            .sumOf { findSplitCount(it, 25) }
            .toString()
    }

    override fun calculateSolutionB(): String {
        return stones
            .sumOf { findSplitCount(it, 75) }
            .toString()
    }

    private fun findSplitCount(input: Long, iterations: Int): Long {
        if (iterations == 0) return 1
        return map.getOrPut(Pair(input, iterations)) {
            when {
                input == 0L -> findSplitCount(1L, iterations - 1)
                input.toString().length % 2 == 0 -> {
                    input.toString()
                        .splitInHalf()
                        .sumOf { findSplitCount(it.toLong(), iterations - 1) }
                }
                else -> findSplitCount(input * 2024, iterations - 1)
            }
        }
    }

    private fun String.splitInHalf() =
        listOf(
            substring(0, length/2),
            substring(length/2, lastIndex + 1)
        )
}

