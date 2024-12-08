package assignments

import models.assignment.Assignment
import kotlin.math.*

class Assignment4 : Assignment(4) {
    private lateinit var cards: List<Card>
    data class Card(val winningNumbers: List<Int>, val numbers: List<Int>)

    override fun initialize(input: List<String>) {
        cards = input
            .map { it.split('|') }
            .map {
                Card(
                    it[0].split(':')[1].parseNumbers(),
                    it[1].parseNumbers(),
                )
            }
    }

    private fun String.parseNumbers() =
        split(' ')
            .filter { it.isNotEmpty() }
            .map { it.toInt() }

    override fun calculateSolutionA() =
        cards.sumOf { card ->
            card
                .countWinningNumbers()
                .let { count ->
                    if (count == 0) {
                        0
                    } else {
                        2.toDouble().pow(count - 1).toInt()
                    }
                }
        }.toString()

    override fun calculateSolutionB(): String {
        // track instances of each card
        val cardStash = cards.map { 1 }.toMutableList()

        cards.forEachIndexed { index, card ->
            // index + 1, because we don't want to add to the current card stash
            for (i in 0 until card.countWinningNumbers()) {
                cardStash[index + 1 + i] += cardStash[index]
            }
        }
        return cardStash.sum().toString()
    }

    private fun Card.countWinningNumbers() =
        numbers.count { winningNumbers.contains(it) }
}
