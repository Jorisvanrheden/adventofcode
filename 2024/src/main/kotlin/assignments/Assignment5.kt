package assignments

import models.assignment.Assignment
import utilities.Utilities
import kotlin.math.floor

class Assignment5 : Assignment(5) {
    private lateinit var rules: List<Pair<Int, Int>>
    private lateinit var updates: List<List<Int>>

    private val connectionBefore = HashMap<Int, MutableList<Int>>()
    private val connectionAfter = HashMap<Int, MutableList<Int>>()

    override fun initialize(input: List<String>) {
        Utilities.packageByEmptyLine(input).let { sections ->
            rules = sections[0]
                .map { it.split('|') }
                .map { Pair(it[0].toInt(), it[1].toInt()) }

            updates = sections[1]
                .map { line -> line.split(',').map { it.toInt() } }

        }

        // Create a before-map and after-map for each input in the rules
        rules.forEach { (first, second) ->
            connectionAfter.computeIfAbsent(first) { mutableListOf() }.add(second)
            connectionBefore.computeIfAbsent(second) { mutableListOf() }.add(first)
        }
    }

    override fun calculateSolutionA(): String {
        return updates
            .filter { it.findMistakeIndices(connectionAfter, connectionBefore).isEmpty() }
            .sumOf { it[floor((it.size / 2).toDouble()).toInt()] }
            .toString()
    }

    override fun calculateSolutionB(): String {
        return updates
            .filter { it.findMistakeIndices(connectionAfter, connectionBefore).isNotEmpty() }
            .map { it.fix(connectionAfter, connectionBefore) }
            .sumOf { it[floor((it.size / 2).toDouble()).toInt()] }
            .toString()
    }

    private fun List<Int>.fix(
        itemsAfter: MutableMap<Int, MutableList<Int>>,
        itemsBefore: MutableMap<Int, MutableList<Int>>,
    ): List<Int> {
        findMistakeIndices(itemsAfter, itemsBefore).forEach { mistake ->
            // Check if this mistake can be instead put at the beginning or end
            itemsBefore
                .connections(first())
                .takeIf { it.contains(mistake) }
                ?.let {
                    return removeAndInsertAt(this, mistake, 0).fix(itemsAfter, itemsBefore)
                }

            itemsAfter
                .connections(last())
                .takeIf { it.contains(mistake) }
                ?.let {
                    return removeAndInsertAt(this, mistake, lastIndex).fix(itemsAfter, itemsBefore)
                }

            // Mistake can be somewhere in the middle, check both sides
            zipWithNext().forEachIndexed { index, (a, b) ->
                if (itemsBefore.connections(b).contains(mistake) && itemsAfter.connections(a).contains(mistake)) {
                    return removeAndInsertAt(this, mistake, index).fix(itemsAfter, itemsBefore)
                }
            }
        }
        return this
    }

    private fun MutableMap<Int, MutableList<Int>>.connections(item: Int) = getOrDefault(item, null) ?: emptyList()

    private fun removeAndInsertAt(items: List<Int>, item: Int, insertIndex: Int): MutableList<Int> {
        val newList = items.toMutableList()
        newList.remove(item)
        newList.add(insertIndex, item)
        return newList
    }

    private fun List<Int>.findMistakeIndices(
        itemsAfter: MutableMap<Int, MutableList<Int>>,
        itemsBefore: MutableMap<Int, MutableList<Int>>,
    ): List<Int> {
        return zipWithNext()
            .mapNotNull { (a, b) ->
                when {
                    itemsAfter[a]?.contains(b) == false -> b
                    itemsBefore[b]?.contains(a) == false -> a
                    else -> null
                }
            }
    }
}

