package assignments

import models.assignment.Assignment
import utilities.Utilities

class Assignment1 : Assignment() {

    data class Elf(val calories: List<Int>)

    private lateinit var elves: List<Elf>

    override fun getInput(): String {
        return "input_1"
    }

    override fun initialize(input: List<String>) {
        val chunks = Utilities.packageByEmptyLine(input)
        elves = chunks.map { calories -> Elf(calories.map { it.toInt() }) }
    }

    override fun calculateSolutionA(): String {
        val calorySumsSorted = elves.map { x ->
            x.calories.sumOf { it }
        }.sortedBy { it }

        // Find the elf that has the highest total calories
        return calorySumsSorted
            .last()
            .toString()
    }

    override fun calculateSolutionB(): String {
        val calorySumsSorted = elves.map { x ->
            x.calories.sumOf { it }
        }.sortedBy { it }

        // Return the sum of the top 3 elves' calories
        return calorySumsSorted
            .takeLast(3)
            .sum()
            .toString()
    }
}
