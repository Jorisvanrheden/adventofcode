package assignments

import kotlin.math.*

class Assignment4 : Assignment() {
    private lateinit var cards: List<Card>
    data class Card(val winningNumbers: List<Int>, val numbers: List<Int>)

    override fun getInput(): String {
        return "input_4"
    }

    override fun initialize(input: List<String>) {
        cards = input.map { line ->
            val chunks = line.split('|')

            val winningNumbers = chunks[0]
                .split(':')[1]
                .trim()
                .split(' ')
                .filter { it.isNotEmpty() }
                .map { it.toInt() }

            val numbers = chunks[1]
                .trim()
                .split(' ')
                .filter { it.isNotEmpty() }
                .map { it.toInt() }

            Card(winningNumbers, numbers)
        }
    }

    override fun calculateSolutionA() =
        cards.sumOf { card ->
            card.numbers
                .count { card.winningNumbers.contains(it) }
                .let { count ->
                    if (count == 0) {
                        0
                    } else {
                        2.toDouble().pow(count - 1).toInt()
                    }
                }
        }.toString()

    override fun calculateSolutionB(): String {
        val counts = mutableListOf<Int>()
        cards.forEachIndexed { index, card -> counts.add(1) }

        cards.forEachIndexed { index, card ->
            // get counts of winning numbers
            val count = card.numbers.count { card.winningNumbers.contains(it) }

            for (i in (index + 1)..(index + count)) {
                counts[i] += counts[index] * 1
            }
        }
        return counts.sum().toString()
    }
}
