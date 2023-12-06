package assignments

import utilities.Utilities

class Assignment5 : Assignment() {
    data class AlamanacMap(val entries: List<AlamanacMapEntry>)
    data class AlamanacMapEntry(val source: Long, val destination: Long, val range: Long)

    private lateinit var seeds: List<Long>
    private lateinit var maps: List<AlamanacMap>

    override fun getInput(): String {
        return "input_5"
    }

    override fun initialize(input: List<String>) {
        seeds = input
            .first()
            .split(':')[1]
            .split(' ')
            .filter { it.isNotEmpty() }
            .map { it.toLong() }

        maps = Utilities.packageByEmptyLine(input.subList(1, input.lastIndex + 1))
            // skip the title
            .map { it.subList(1, it.lastIndex + 1).toAlmanacMap() }
    }

    private fun List<String>.toAlmanacMap() =
        map { line ->
            line
                .split(' ')
                .map { it.toLong() }
                .let { AlamanacMapEntry(it[1], it[0], it[2]) }
        }.let { AlamanacMap(it) }

    override fun calculateSolutionA() =
        seeds
            .map { it.traverse(maps) }
            .minOf { it }
            .toString()

    override fun calculateSolutionB() =
        seeds.toRanges()
            .map { it.binarySearch(maps) }
            .let {
                minimumLocationScore.toString()
            }

    private var minimumLocationScore = Long.MAX_VALUE

    private fun LongRange.binarySearch(maps: List<AlamanacMap>) {
        val range = last - first

        // then create a new range for each of those
        // break it down in x amount of ranges
        val sections = 10000

        IntRange(0, sections)
            .mapIndexed { index, _ ->
                val pStart = index.toDouble() / sections
                val pEnd = (index + 1).toDouble() / sections
                LongRange(
                    first + (pStart * range).toLong(),
                    first + (pEnd * range).toLong(),
                )
            }.forEach {
                val middleSeed = (it.first + it.last) / 2
                middleSeed
                    .traverse(maps)
                    .let { score ->
                        if (score < minimumLocationScore) {
                            minimumLocationScore = score
                            it.binarySearch(maps)
                        }
                    }
            }
    }

    private fun List<Long>.toRanges(): List<LongRange> {
        require(count().mod(2) == 0)
        val mutableList = mutableListOf<LongRange>()
        for (i in 0 until count() / 2) {
            val index = i * 2
            mutableList.add(LongRange(this[index], this[index] + this[index + 1] - 1))
        }
        return mutableList.toList()
    }

    private fun Long.traverse(maps: List<AlamanacMap>, startIndex: Int = 0): Long {
        return maps[startIndex]
            .transformInput(this)
            .let {
                if (startIndex >= maps.count() - 1) {
                    it
                } else {
                    it.traverse(maps, startIndex + 1)
                }
            }
    }

    private fun AlamanacMap.transformInput(input: Long): Long {
        entries.forEach {
            if (input >= it.source && input < it.source + it.range) {
                return it.destination + (input - it.source)
            }
        }
        return input
    }
}
