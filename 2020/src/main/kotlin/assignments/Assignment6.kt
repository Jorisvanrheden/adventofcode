package assignments

import utilities.Utilities

class Assignment6 : Assignment() {
    private lateinit var groupAnswers: List<List<String>>

    override fun getInput(): String {
        return "input_6"
    }

    override fun initialize(input: List<String>) {
        groupAnswers = Utilities.packageByEmptyLine(input)
    }

    private fun allEntriesContain(chunk: List<String>, letter: Char): Boolean {
        for (x in chunk) {
            if (!x.contains(letter)) return false
        }
        return true
    }

    private fun getAnswerCountPerChunk(chunk: List<String>): Int {
        val alphabet = "abcdefghijklmnopqrstuvwxyz"

        return alphabet.count { allEntriesContain(chunk, it) }
    }

    override fun calculateSolutionA(): String {
        val answers: MutableList<String> = mutableListOf()
        for (x in groupAnswers) {
            answers.add(x.joinToString { it }.replace(", ", ""))
        }

        return answers.sumOf { it.toSet().size }.toString()
    }

    override fun calculateSolutionB(): String =
        groupAnswers.sumOf { getAnswerCountPerChunk(it) }.toString()
}
