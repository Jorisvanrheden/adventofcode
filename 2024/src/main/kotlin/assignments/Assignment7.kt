package assignments

import models.assignment.Assignment

class Assignment7 : Assignment() {

    private data class Entry(
        val total: Long,
        val numbers: List<Long>,
    )

    private lateinit var entries: List<Entry>

    override fun getInput(): String {
        return "input_7"
    }

    override fun initialize(input: List<String>) {
        entries = input.map {
            val chunk = it.split(':')
            val total = chunk[0].toLong()
            val numbers = chunk[1].split(' ').filter { it.isNotEmpty() }.map { it.toLong() }
            Entry(total, numbers)
        }
    }

    private fun add(a: Long, b: Long): Long = a + b
    private fun multiply(a: Long, b: Long): Long = a * b
    private fun concatenate(a: Long, b: Long): Long = "$a$b".toLong()

    override fun calculateSolutionA(): String {
        val operators = listOf(::add, ::multiply)
        return entries
            .filter { containsSolution(it.total, it.numbers.first(), it.numbers.subList(1, it.numbers.lastIndex + 1), operators) }
            .sumOf { it.total }
            .toString()
    }

    override fun calculateSolutionB(): String {
        val operators = listOf(::add, ::multiply, ::concatenate)
        return entries
            .filter { containsSolution(it.total, it.numbers.first(), it.numbers.subList(1, it.numbers.lastIndex + 1), operators) }
            .sumOf { it.total }
            .toString()
    }

    private fun containsSolution(total: Long, current: Long, numbers: List<Long>, operators: List<(Long, Long) -> Long>): Boolean {
        if (current > total) return false
        return if (numbers.isEmpty()) {
            total == current
        } else {
            operators.any {
                containsSolution(total, it(current, numbers[0]), numbers.subList(1, numbers.lastIndex + 1), operators)
            }
        }
    }
}

