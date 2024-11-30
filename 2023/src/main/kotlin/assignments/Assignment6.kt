package assignments

import models.assignment.Assignment

class Assignment6 : Assignment() {
    private lateinit var times: List<Int>
    private lateinit var distances: List<Int>

    override fun getInput(): String {
        return "input_6"
    }

    override fun initialize(input: List<String>) {
        times = input.first().toNumbers()
        distances = input.last().toNumbers()
    }

    private fun String.toNumbers() =
        split(':')[1]
            .split(' ')
            .filter { it.isNotEmpty() }
            .map { it.toInt() }

    override fun calculateSolutionA() =
        times
            .mapIndexed { index, totalTime ->
                IntRange(0, totalTime).count {
                    val distance = (it * (totalTime - it))
                    distance > distances[index]
                }
            }
            .reduce { a, b -> a * b }
            .toString()

    override fun calculateSolutionB(): String {
        val time = times.toSingularNumber()
        val highestDistance = distances.toSingularNumber()

        return LongRange(0, time).count {
            val distance = (it * (time - it))
            distance > highestDistance
        }.toString()
    }

    private fun List<Int>.toSingularNumber() =
        joinToString("") { it.toString() }.toLong()
}
