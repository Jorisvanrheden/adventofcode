package assignments

import models.assignment.Assignment

class Assignment12 : Assignment(12) {
    data class Entry(
        val input: String,
        val summary: List<Int>,
    )

    private lateinit var entries: List<Entry>

    override fun initialize(input: List<String>) {
        entries = input.map { line ->
            val originalInput = line.split(' ')[0]
            val originalSummary = line.split(' ')[1].split(',').map { it.toInt() }
            val copyCount = 5
            Entry(
                originalInput.extend(copyCount),
                List(copyCount) { originalSummary.toList() }.flatten(),
            )
        }
    }

    private fun String.extend(times: Int): String {
        if (times == 1) return this

        return plus(
            IntRange(0, times - 2)
                .map { "?".plus(this) }
                .joinToString(""),
        )
    }

    override fun calculateSolutionA(): String {
        return ""
//        return entries
//            .map { (input, summary) ->
//                val combinations = mutableListOf<String>()
//
//                val indicesWithUnknowns = input.indicesOf { it == '?' }
//                val iterations = 2.toDouble().pow(indicesWithUnknowns.size).toInt()
//                for (i in 0 until iterations) {
//                    val binary = i.toBinary(indicesWithUnknowns.size)
//
//                    val resultingString = input.replaceWithBinary(binary, indicesWithUnknowns)
//                    val chunks = resultingString
//                        .split('.')
//                        .filter { it.isNotEmpty() }
//
//                    if (compareChunkWithInput(chunks, summary)) {
//                        combinations.add(resultingString)
//                    }
//                }
//                combinations.distinct().count()
//            }
//            .sumOf { it }
//            .toString()
    }

    override fun calculateSolutionB(): String {
//        var t = 0
//        return entries.map {
//            t++
//            println(t)
//            it.input.findPossibilities(numbers = it.summary)
//        }.sum().toString()

        return "?###????????".findPossibilities(numbers = listOf(3,2,1)).toString()
    }


    private fun String.findPossibilities(
        inputIndex: Int = 0,
        numberIndex: Int = 0,
        numbers: List<Int>,
    ): Int {
        var score = 0

        if (numberIndex >= numbers.size) return 0

        // if current is last number, and the number fits in the remaining space, return 1
        val charactersLeft = length - inputIndex
        val charactersNeeded = numbers.requiredCharactersFromIndex(numberIndex)
        if (charactersNeeded <= charactersLeft) {
            // we can process the number
            val number = numbers[numberIndex]
            val sectionToEvaluate = substring(inputIndex, inputIndex + number)
            if (sectionToEvaluate.contains('.')) {
                // start from current index + 1
                score += findPossibilities(inputIndex + 1, numberIndex, numbers)
            } else {
                // should there be some scoring here?
                if (numberIndex == numbers.lastIndex) {
                    score += 1
                }

                // start from current index + 1
                score += findPossibilities(inputIndex + 1, numberIndex, numbers)

                // increment active number
                val nextInputIndex = inputIndex + number
                if (nextInputIndex < length) {
                    // you can only evaluate the next section if nextInputIndex - 1 == '.' OR '?'
                    if (this[nextInputIndex] == '.' || this[nextInputIndex] == '?' ) {
                        score += findPossibilities(nextInputIndex + 1, numberIndex + 1, numbers)
                    }


                }
            }
        }
        return score
    }

    private fun List<Int>.requiredCharactersFromIndex(index: Int): Int {
        val section = subList(index, count())
        return section.sum() + (section.size - 1)
//        numbers.subList(numberIndex, numbers.count()).sum() + (numbers.size - 1)
    }

    private fun String.replaceWithBinary(binary: String, replacingIndices: List<Int>): String {
        val copy = toMutableList()
        for (i in binary.indices) {
            val originalStringIndex = replacingIndices[i]
            if (binary[i] == '1') {
                copy[originalStringIndex] = '#'
            } else {
                copy[originalStringIndex] = '.'
            }
        }
        return copy.joinToString("")
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
