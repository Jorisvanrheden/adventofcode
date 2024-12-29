package assignments

import models.assignment.Assignment
import kotlin.math.floor

class Assignment22 : Assignment(22) {
    private lateinit var secrets: List<Long>

    override fun initialize(input: List<String>) {
        secrets = input.map { it.toLong() }
    }

    override fun calculateSolutionA(): String {
        return secrets
            .sumOf { it.toSecretNumbers(2000).last() }
            .toString()
    }

    override fun calculateSolutionB(): String {
        val prices = secrets.map { it.toSecretNumbers(2000).map { it.toPrice() } }
        val sequencePriceMaps = prices.map { it.toSequencePriceMap() }

        return prices
            .toUniquePatterns()
            .maxOfOrNull { pattern -> sequencePriceMaps.sumOf { it.getOrDefault(pattern, 0) } }
            .toString()
    }

    private fun List<List<Int>>.toUniquePatterns() =
        flatMap { it.zipWithNext { a, b -> b - a } }
            .windowed(4)
            .toSet()

    private fun List<Int>.toSequencePriceMap(): MutableMap<List<Int>, Int> {
        val map = mutableMapOf<List<Int>, Int>()
        zipWithNext { a, b -> b - a }
            .windowed(4)
            .withIndex()
            .forEach { (index, sequence) ->
                if (!map.containsKey(sequence)) {
                    map[sequence] = this[index + sequence.size]
                }
            }
        return map
    }

    private fun Long.toSecretNumbers(iterations: Int) =
        mutableListOf(this).also {
            var secretNumber = this
            val sequence = IntRange(0, iterations - 1).map {
                secretNumber = processSecretNumber(secretNumber)
                secretNumber
            }
            it.addAll(sequence)
        }

    private fun processSecretNumber(secretNumber: Long) =
        listOf(
            { secret: Long -> secret.mix(secret * 64).prune() },
            { secret: Long -> secret.mix(floor((secret.toDouble() / 32.toDouble())).toLong()).prune() },
            { secret: Long -> secret.mix(secret * 2048).prune() }
        ).fold(secretNumber) { secret, operation ->
            operation(secret)
        }

    private fun Long.toPrice() = toString().last().digitToInt()
    private fun Long.mix(input: Long) = input xor this
    private fun Long.prune() = this.mod(16777216L)
}