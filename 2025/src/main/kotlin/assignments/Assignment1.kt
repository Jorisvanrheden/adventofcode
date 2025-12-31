package assignments

import models.assignment.Assignment

class Assignment1 : Assignment(1) {
    private lateinit var steps: List<Int>

    override fun initialize(input: List<String>) {
        steps = input
            .map {
                when (it[0]) {
                    'L' -> it.substring(1).toInt() * -1
                    'R' -> it.substring(1).toInt()
                    else -> 0
                }
            }
    }

    override fun calculateSolutionA(): String {
        var start = 50
        var countScoreZero = 0
        steps.forEach {
            start += it

            val score = start.mod(100)
            if (score == 0) countScoreZero++
        }
        return countScoreZero.toString()
    }

    override fun calculateSolutionB(): String {
        var counter = 50
        var countScoreZero = 0
        steps.forEach {
            val range =
                if (it < 0) {
                    IntRange(it, -1)
                } else {
                    IntRange(1, it)
                }
            for (increment in range) {
                val updatedCounter = counter + increment
                if (updatedCounter.mod(100) == 0) {
                    countScoreZero++
                }
            }
            counter += it
        }
        return countScoreZero.toString()
    }
}
