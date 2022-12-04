package assignments

class Assignment4 : Assignment() {

    data class Range(val min: Int, val max: Int) {
        fun contains(x: Int): Boolean {
            return x in min..max
        }
    }
    data class Pair(val rangeA: Range, val rangeB: Range)

    private lateinit var pairs: List<Pair>

    override fun getInput(): String {
        return "input_4"
    }

    private fun stringToRange(input: String): Range {
        val chunks = input.split('-')
        return Range(chunks[0].toInt(), chunks[1].toInt())
    }

    override fun initialize(input: List<String>) {
        pairs = input.map {
            val chunks = it.split(',')
            Pair(
                stringToRange(chunks[0]),
                stringToRange(chunks[1])
            )
        }
    }

    private fun rangesCompletelyOverlap(rangeA: Range, rangeB: Range): Boolean {
        return (rangeB.contains(rangeA.min) && rangeB.contains(rangeA.max)) ||
            (rangeA.contains(rangeB.min) && rangeA.contains(rangeB.max))
    }
    private fun rangesPartiallyOverlap(rangeA: Range, rangeB: Range): Boolean {
        return (rangeB.contains(rangeA.min) || rangeB.contains(rangeA.max)) ||
            (rangeA.contains(rangeB.min) || rangeA.contains(rangeB.max))
    }

    override fun calculateSolutionA(): String {
        return pairs.count {
            rangesCompletelyOverlap(it.rangeA, it.rangeB)
        }.toString()
    }

    override fun calculateSolutionB(): String {
        return pairs.count {
            rangesPartiallyOverlap(it.rangeA, it.rangeB)
        }.toString()
    }
}
