package assignments

import models.assignment.Assignment

class Assignment2 : Assignment(2) {
    private lateinit var ranges: List<LongRange>

    override fun initialize(input: List<String>) {
        ranges = input.first().split(",").map {
            LongRange(
                it.split("-").first().toLong(),
                it.split("-").last().toLong()
            )
        }
    }

    override fun calculateSolutionA(): String {
        return ranges
            .flatMap { it }
            .filter { it.hasInvalidPattern(2) }
            .sum()
            .toString()
    }

    override fun calculateSolutionB(): String {
        return ranges
            .flatMap { it }
            .filter { it.hasInvalidPattern(it.toString().length) }
            .sum()
            .toString()
    }

    private fun Long.hasInvalidPattern(maxDivisors: Int) =
        (2..maxDivisors).any { hasRepeatingSequenceOfSize(it) }

    private fun Long.hasRepeatingSequenceOfSize(size: Int): Boolean {
        val id = toString()
        if (id.length % size != 0) return false
        return id.chunked(id.length / size).distinct().size == 1
    }
}
