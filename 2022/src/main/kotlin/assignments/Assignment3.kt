package assignments

class Assignment3 : Assignment() {

    data class Rucksack(
        val originalEntry: String,
        val compartmentA: String,
        val compartmentB: String
    )

    private lateinit var rucksacks: List<Rucksack>

    override fun getInput(): String {
        return "input_3"
    }

    override fun initialize(input: List<String>) {
        rucksacks = input.map {
            // get the first half
            val compartmentA = it.substring(0, it.length / 2)
            // get the second half
            val compartmentB = it.substring(it.length / 2, it.length)

            Rucksack(it, compartmentA, compartmentB)
        }
    }

    private fun getMatchingTypes(a: String, b: String): List<Char> {
        return a.toSet()
            .intersect(b.toSet())
            .toList()
    }

    private fun getTypeScore(type: Char): Int {
        return if (type.isUpperCase()) {
            type - 'A' + 26 + 1
        } else {
            type - 'a' + 1
        }
    }

    private fun getGroupBadge(entries: List<String>): Char {
        var overlap = entries.first().toSet()
        for (entry in entries) {
            overlap = overlap.intersect(entry.toSet())
        }
        return overlap.first()
    }

    override fun calculateSolutionA(): String {
        // get all match types for each entry
        val matchingTypesPerEntry = rucksacks.map {
            getMatchingTypes(
                it.compartmentA,
                it.compartmentB
            )
        }

        return matchingTypesPerEntry.sumOf {
            it.sumOf { x -> getTypeScore(x) }
        }.toString()
    }

    override fun calculateSolutionB(): String {
        // Create groups of 3 rucksacks
        val groups = rucksacks.map { it.originalEntry }.chunked(3)

        return groups.sumOf {
            getTypeScore(
                getGroupBadge(it)
            )
        }.toString()
    }
}
