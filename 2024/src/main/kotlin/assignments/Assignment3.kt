package assignments

import models.assignment.Assignment
import models.indicesOf

class Assignment3 : Assignment() {

    private lateinit var memory: String

    override fun getInput(): String {
        return "input_3"
    }

    override fun initialize(input: List<String>) {
        memory = input.joinToString()
    }

    override fun calculateSolutionA(): String {
        return memory.pairs()
            .filter { memory.isMulChunk(it.first) }
            .mapNotNull { memory.substring(it.first, it.second + 1).mul() }
            .sum()
            .toString()
    }

    override fun calculateSolutionB(): String {
        val indicesDo = memory.indicesOf("do()")
        val indicesDont = memory.indicesOf("don't()")
        return memory.pairs()
            .filter { memory.isMulChunk(it.first) }
            .filter { canBeProcessed(it.first, indicesDo, indicesDont) }
            .mapNotNull { memory.substring(it.first, it.second + 1).mul() }
            .sum()
            .toString()
    }

    private fun canBeProcessed(index: Int, indicesDo: List<Int>, indicesDont: List<Int>): Boolean {
        val indexDo = indicesDo.lastOrNull { index > it }
        val indexDont = indicesDont.lastOrNull { index > it }

        return when {
            indexDo == null && indexDont == null -> true
            indexDo != null && indexDont == null -> true
            indexDo == null && indexDont != null -> false
            else -> indexDo!! > indexDont!!
        }
    }

    private fun String.isMulChunk(startIndex: Int): Boolean {
        return startIndex >= 3 && substring(startIndex - 3, startIndex) == "mul"
    }

    private fun String.pairs(): MutableList<Pair<Int, Int>> {
        val pairs = mutableListOf<Pair<Int, Int>>()
        val deque = ArrayDeque<Int>()
        for (i in indices) {
            when (this[i]) {
                '(' -> deque.addLast(i)
                ')' -> if (deque.isNotEmpty()) pairs.add(deque.removeLast() to i)
            }
        }
        return pairs
    }

    private fun String.mul(): Int? {
        try {
            val chunks = trim('(', ')').split(',')
            if (chunks.size > 2) return null
            return chunks[0].toInt() * chunks[1].toInt()
        } catch (e: Exception) {
            return null
        }
    }
}

