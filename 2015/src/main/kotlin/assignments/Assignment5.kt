package assignments

class Assignment5 : Assignment() {

    override fun getInput(): String {
        return "input_5"
    }

    private lateinit var input: List<String>

    override fun initialize(input: List<String>) {
        this.input = input
    }

    private fun Boolean.toInt(): Int =
        if (this) 1
        else 0

    private fun String.containsVowels(): Boolean =
        sumOf { c ->
            listOf('a', 'e', 'i', 'o', 'u').contains(c).toInt()
        } >= 3

    private fun String.containsUnwantedParts(): Boolean =
        listOf("ab", "cd", "pq", "xy").any { this.contains(it) }

    private fun String.containsDuplicateLetter(): Boolean {
        for (i in 0 until length - 1) {
            if (this[i] == this[i + 1]) return true
        }
        return false
    }

    private fun String.containsLetterPairs(): Boolean {
        for (i in 0 until length - 1) {
            // pair
            val pairA = listOf(this[i], this[i + 1]).joinToString("")
            for (j in i + 2 until length - 1) {
                val pairB = listOf(this[j], this[j + 1]).joinToString("")

                if (pairA == pairB) return true
            }
        }
        return false
    }

    private fun String.containsDuplicateWithSpaceLetter(): Boolean {
        for (i in 0 until length - 2) {
            if (this[i] == this[i + 2]) return true
        }
        return false
    }

    private fun String.isNice(): Boolean =
        containsVowels() && !containsUnwantedParts() && containsDuplicateLetter()

    private fun String.isUpdatedNice(): Boolean =
        containsLetterPairs() && containsDuplicateWithSpaceLetter()

    override fun calculateSolutionA() =
        input.count { it.isNice() }
            .toString()

    override fun calculateSolutionB(): String =
        input.count { it.isUpdatedNice() }
            .toString()
}
