package assignments

class Assignment9 : Assignment() {

    override fun getInput(): String {
        return "input_9"
    }

    private lateinit var entries: List<List<Int>>

    override fun initialize(input: List<String>) {
        entries = input.map { line ->
            line.split(' ').map { it.toInt() }
        }
    }

    override fun calculateSolutionA() =
        entries
            .map { it.sumOfLastValues() }
            .sumOf { it }
            .toString()

    override fun calculateSolutionB() =
        entries
            .map {
                val deepestLevel = it.deepestLevel()
                val startNumbers = mutableListOf<Int>()
                it
                    .subList(0, deepestLevel + 1)
                    .startNumbers(startNumbers)

                var newNumber = 0
                startNumbers
                    .reversed()
                    .forEach {
                        newNumber = it - newNumber
                    }

                newNumber
            }
            .sumOf { it }
            .toString()

    private fun List<Int>.deepestLevel(): Int {
        var level = this
        var levelDepth = 0
        while (level.any { it != 0 }) {
            level = level.nextLayer()
            levelDepth++
        }
        return levelDepth
    }

    private fun List<Int>.startNumbers(numbers: MutableList<Int>) {
        if (any { it != 0 }) {
            numbers.add(first())
            nextLayer().startNumbers(numbers)
        }
    }

    private fun List<Int>.sumOfLastValues(): Int {
        var total = last()
        if (any { it != 0 }) {
            total += nextLayer().sumOfLastValues()
        }
        return total
    }

    private fun List<Int>.nextLayer() =
        IntRange(1, lastIndex).map {
            this[it] - this[it - 1]
        }
}
