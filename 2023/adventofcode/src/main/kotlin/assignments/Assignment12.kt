package assignments

import kotlin.math.pow

class Assignment12 : Assignment() {

    data class Entry(
        val input: String,
        val summary: List<Int>,
    )

    private lateinit var entries: List<Entry>

    override fun getInput(): String {
        return "input_12"
    }

    override fun initialize(input: List<String>) {
        entries = input.map { line ->
            Entry(
                line.split(' ')[0],
                line.split(' ')[1].split(',').map { it.toInt() },
            )
        }
    }

    override fun calculateSolutionA(): String {
        val totalIterations = entries
            .map {
                val combinations = mutableListOf<String>()

                val indicesWithUnknowns = it.input.indicesOf { it == '?' }
                val iterations = 2.toDouble().pow(indicesWithUnknowns.size).toInt()
                for (i in 0 until iterations) {
                    val binary = i.toBinary(indicesWithUnknowns.size)
                    val copy = it.input.toMutableList()
                    for (cIndex in binary.indices) {
                        if (cIndex < binary.length) {
                            val originalStringIndex = indicesWithUnknowns[cIndex]
                            if (binary[cIndex] == '1') {
                                // set the ? to #
                                copy[originalStringIndex] = '#'
                            } else {
                                // set the ? to .
                                copy[originalStringIndex] = '.'
                            }
                        }
                    }

                    val resultingString = copy.joinToString("")

                    val chunks = resultingString
                        .split('.')
                        .filter { it.isNotEmpty() }

                    if (compareChunkWithInput(chunks, it.summary)) {
                        combinations.add(resultingString)
                    }
                }
                combinations.distinct().count()
            }.sumOf { it }

        return totalIterations.toString()
    }

    override fun calculateSolutionB(): String {
        return ""
    }

    private fun String.indicesOf(predicate: (Char) -> Boolean): List<Int> {
        val indicesThatMatchesPredicate = mutableListOf<Int>()
        for (i in indices) {
            if (predicate(this[i])) {
                indicesThatMatchesPredicate.add(i)
            }
        }
        return indicesThatMatchesPredicate
    }

    private fun Int.toBinary(maxValues: Int) =
        String.format(
            "%${maxValues}s",
            toString(2),
        )
            .replace(' ', '0')

    private fun compareChunkWithInput(chunks: List<String>, summary: List<Int>): Boolean {
        if (chunks.size != summary.size) return false
        for (c in chunks.indices) {
            if (chunks[c].count() != summary[c]) return false
        }
        return true
    }
}
