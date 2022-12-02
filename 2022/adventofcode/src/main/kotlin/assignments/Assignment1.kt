package assignments

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
        // Find the elf that has the highest total calories
        return elves.maxOf {
            it.calories.sumOf { x -> x }
        }.toString()
    }

    override fun calculateSolutionB(): String {
        // Sort the elves based on calories, and then take the top 3 values
        val topElvesSortedByCalories = elves.sortedBy { x ->
            x.calories.sumOf { it }
        }.let { it.subList(it.size - 3, it.size) }

        // Return the sum of the top 3 elves' calories
        return topElvesSortedByCalories.sumOf { x ->
            x.calories.sumOf { it }
        }.toString()
    }
}
