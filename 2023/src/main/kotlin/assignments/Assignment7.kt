package assignments

import models.assignment.Assignment

class Assignment7 : Assignment() {

    private lateinit var hands: List<Hand>

    data class Hand(
        val input: String,
        var cards: List<Int>,
        val bid: Int,
    )

    override fun getInput(): String {
        return "input_7"
    }

    override fun initialize(input: List<String>) {
        hands = input.map {
            Hand(
                it.split(' ')[0],
                it.split(' ')[0].toCardValues(),
                it.split(' ')[1].toInt(),
            )
        }
    }

    private fun String.toCardValues() =
        map {
            if (it.isDigit()) {
                it.digitToInt()
            } else {
                LETTER_VALUES[it]!!
            }
        }

    override fun calculateSolutionA() =
        hands
            .sortedWith(comparator)
            .mapIndexed { index, hand -> hand.bid * (index + 1) }
            .sum()
            .toString()

    override fun calculateSolutionB() =
        hands
            .map { it.withJoker() }
            .sortedWith(comparator)
            .mapIndexed { index, hand -> hand.bid * (index + 1) }
            .sum()
            .toString()

    private val comparator = Comparator { handA: Hand, handB: Hand ->
        return@Comparator handA.compare(handB)
    }

    private fun Hand.compare(hand: Hand): Int {
        val diff = cards.score() - hand.cards.score()
        if (diff == 0) {
            for (i in cards.indices) {
                val compareResultPerCard = cards[i] - hand.cards[i]
                if (compareResultPerCard == 0) {
                    continue
                } else {
                    return compareResultPerCard
                }
            }
        } else {
            return diff
        }
        return 0
    }

    private fun List<Int>.replaceInts(toReplace: Int, replaceValue: Int) =
        map {
            if (it == toReplace) {
                replaceValue
            } else {
                it
            }
        }

    private fun List<Int>.calculateScore(): Int {
        // sort the original set by count
        val sortedByCount = sortedBy { item -> count { it == item } }
            .reversed()
            .let {
                val index = it.indexOfFirst { item -> count { it == item } == 1 }
                if (index == -1) {
                    it
                } else {
                    val a = it.subList(0, index)
                    val b = it.subList(index, lastIndex + 1).sorted()
                    a.plus(b)
                }
            }

        val uniqueValues = distinct()
            .sortedBy { sortedByCount.indexOf(it) }

        return sortedByCount.map { uniqueValues.indexOf(it) }
            .sorted()
            .let {
                when (it.joinToString("")) {
                    "00000" -> 7
                    "00001" -> 6
                    "00011" -> 5
                    "00012" -> 4
                    "00112" -> 3
                    "00123" -> 2
                    "01234" -> 1
                    else -> 0
                }
            }
    }

    private fun List<Int>.score() =
        if (contains(JOKER_VALUE)) {
            IntRange(1, 14)
                .map { replacement ->
                    replaceInts(JOKER_VALUE, replacement).calculateScore()
                }
                .maxOf { it }
        } else {
            calculateScore()
        }

    private fun Hand.withJoker(): Hand {
        cards = cards.replaceInts(PRE_JOKER_VALUE, JOKER_VALUE)
        return this
    }

    private companion object {
        val LETTER_VALUES = mapOf(
            'T' to 10,
            'J' to 11,
            'Q' to 12,
            'K' to 13,
            'A' to 14,
        )

        val PRE_JOKER_VALUE = LETTER_VALUES['J']!!
        val JOKER_VALUE = 0
    }
}
