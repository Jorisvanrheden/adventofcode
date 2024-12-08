package assignments

import models.assignment.Assignment
import kotlin.math.abs

class Assignment1 : Assignment(1) {
    private lateinit var leftList: List<Int>
    private lateinit var rightList: List<Int>

    override fun initialize(input: List<String>) {
        input
            .map { it.split(' ').filter { it.isNotEmpty() } }
            .let { lines ->
                leftList = lines.map { it[0].toInt() }
                rightList = lines.map { it[1].toInt() }
            }
    }

    override fun calculateSolutionA(): String {
        val sortedLeftList = leftList.sorted()
        val sortedRightList = rightList.sorted()
        return sortedLeftList
            .zip(sortedRightList)
            .sumOf { abs(it.first - it.second) }
            .toString()
    }

    override fun calculateSolutionB(): String {
        return leftList
            .sumOf { leftItem ->
                val occurrences = rightList.count { rightItem -> leftItem == rightItem }
                leftItem * occurrences
            }
            .toString()
    }
}
