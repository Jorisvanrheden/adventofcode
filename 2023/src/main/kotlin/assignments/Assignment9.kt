package assignments

import models.assignment.Assignment

class Assignment9 : Assignment(9) {
    private lateinit var entries: List<List<Int>>

    override fun initialize(input: List<String>) {
        entries = input.map { line ->
            line.split(' ').map { it.toInt() }
        }
    }

    override fun calculateSolutionA() =
        entries
            .map { entry ->
                entry
                    .fetchNumbersFromAllLayers { it.last() }
                    .sum()
            }
            .sumOf { it }
            .toString()

    override fun calculateSolutionB() =
        entries
            .map { entry ->
                var total = 0
                entry
                    .fetchNumbersFromAllLayers { it.first() }
                    .forEach { total = it - total }
                total
            }
            .sumOf { it }
            .toString()

    private fun List<Int>.fetchNumbersFromAllLayers(predicate: (List<Int>) -> Int): List<Int> {
        var level = this
        var numbers = mutableListOf<Int>()
        while (level.any { it != 0 }) {
            numbers.add(predicate(level))
            level = level.nextLayer()
        }
        return numbers.reversed()
    }

    private fun List<Int>.nextLayer() =
        IntRange(1, lastIndex).map {
            this[it] - this[it - 1]
        }
}
