package assignments

import models.assignment.Assignment

class Assignment1 : Assignment() {
    private lateinit var lines: List<String>

    override fun getInput(): String {
        return "input_1"
    }

    override fun initialize(input: List<String>) {
        lines = input
    }

    override fun calculateSolutionA() =
        lines
            .sumOf { it.calibrationValue() }
            .toString()

    override fun calculateSolutionB() =
        lines
            .map { it.replaceNamesWithNumbers() }
            .sumOf { it.calibrationValue() }
            .toString()

    private fun String.replaceNamesWithNumbers(): String {
        var line = ""
        forEachIndexed { index, char ->
            if (char.isDigit()) {
                line += char
            } else {
                NAMES_TO_NUMBERS.forEach { (name, number) ->
                    if (substring(index).startsWith(name)) {
                        line += number
                    }
                }
            }
        }
        return line
    }

    private fun String.calibrationValue() =
        listOf(
            first { it.isDigit() },
            last { it.isDigit() },
        ).joinToString("")
            .toInt()

    private companion object {
        val NAMES_TO_NUMBERS = mapOf(
            "one" to '1',
            "two" to '2',
            "three" to '3',
            "four" to '4',
            "five" to '5',
            "six" to '6',
            "seven" to '7',
            "eight" to '8',
            "nine" to '9',
        )
    }
}
