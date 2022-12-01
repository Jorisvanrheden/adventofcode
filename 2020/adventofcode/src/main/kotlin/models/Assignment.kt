package assignments

abstract class Assignment {
    abstract fun getInput(): String
    abstract fun initialize(input: List<String>)

    fun run() {
        val resultA: String = calculateSolutionA()
        printResult(String.format("%s: %s", "The answer to part 1 is", resultA))

        val resultB: String = calculateSolutionB()
        printResult(String.format("%s: %s", "The answer to part 2 is", resultB))
    }

    private fun printResult(result: String) {
        println(result)
    }

    abstract fun calculateSolutionA(): String
    abstract fun calculateSolutionB(): String
}
