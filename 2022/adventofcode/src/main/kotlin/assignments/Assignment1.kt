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

    private fun getTotalFromCollection(collection: List<Int>): Int {
        var total = 0
        for (value in collection) {
            total += value
        }
        return total
    }

    private fun findHeighestCaloryCount(elves: List<Elf>): Int {
        var highestValue = Int.MIN_VALUE
        for (i in elves.indices) {
            val total = getTotalFromCollection(elves[i].calories)
            if (total > highestValue) {
                highestValue = total
            }
        }
        return highestValue
    }

    override fun calculateSolutionA(): String {
        return findHeighestCaloryCount(elves).toString()
    }

    override fun calculateSolutionB(): String {
        val sortedElves = elves.sortedBy { getTotalFromCollection(it.calories) }

        var topTotal = 0
        for(i in sortedElves.size - 3 until sortedElves.size){
            topTotal += getTotalFromCollection(sortedElves[i].calories)
        }
        return topTotal.toString()
    }
}
