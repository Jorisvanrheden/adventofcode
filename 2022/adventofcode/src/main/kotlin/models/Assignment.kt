package assignments

import kotlin.system.measureTimeMillis

abstract class Assignment {
    abstract fun getInput(): String
    abstract fun initialize(input: List<String>)

    fun run() {
        measureTimeMillis {
            val resultA: String = calculateSolutionA()
            print(String.format("%s: %s", "The answer to part 1 is", resultA))
        }.let { print(" (in $it ms) \n") }

        measureTimeMillis {
            val resultB: String = calculateSolutionB()
            print(String.format("%s: %s", "The answer to part 2 is", resultB))
        }.let { print(" (in $it ms) \n") }
    }

    abstract fun calculateSolutionA(): String
    abstract fun calculateSolutionB(): String
}
