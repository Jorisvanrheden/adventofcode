package assignments

import models.assignment.Assignment
import kotlin.math.pow

class Assignment25 : Assignment() {

    override fun getInput(): String {
        return "input_25"
    }

    private lateinit var snafuNumbers: List<String>

    private fun String.toDecimal(): Long {
        val reversed = this.reversed()
        var total = 0L
        for (i in reversed.indices) {
            // base is pow(i + 1,5)
            val base = (5.toDouble().pow(i)).toLong()

            var multiplier = 1
            when (reversed[i]) {
                '2' -> multiplier = 2
                '1' -> multiplier = 1
                '0' -> multiplier = 0
                '-' -> multiplier = -1
                '=' -> multiplier = -2
            }
            total += base * multiplier
        }
        return total
    }

    private fun Long.toSnafu(): String {
        // start with the total and slowly find the highest base that can fit
        // then subtract with that base
        var total = this

        var snafu = ""

        var baseIndex = 0

        while (total != 0L) {
            // calculate the remainder after modding with the next base index
            val currentBaseDecimal = (5.toDouble().pow(baseIndex)).toLong()
            val nextBaseDecimal = (5.toDouble().pow(baseIndex + 1)).toLong()

            // mod using the next base decimal
            val remainder = total.mod(nextBaseDecimal)

            // check if the remainder is within the range of 0, 1 or 2
            if (remainder <= 2 * currentBaseDecimal) {
                val s = remainder / currentBaseDecimal

                // update the total: since we could fit the remainder in the possible
                // positive snafu options, we subtract the result of the total
                total -= s * currentBaseDecimal
                snafu += s.toString()
            } else {
                val s = (remainder - nextBaseDecimal) / currentBaseDecimal

                // update the total: since we could not fit the remainder in the possible
                // positive snafu options, we get a minus snafu number
                total -= s * currentBaseDecimal

                if (s == -2L)snafu += "="
                else if (s == -1L)snafu += '-'
                else {
                    println("Should not happen")
                }
            }

            baseIndex++
        }

        // 12111      906
        // 1.)
        // 906 mod 5 = 1
        // 1 does fit with 1s, so:
        // 1 / 1 = 1 --> answer is '1' --> -1 for next check

        // 2.)
        // 906 mod 25 - 1 = 5
        // 5 does fit with 5s, so:
        // 5 / 5 = 1 --> answer is '1' --> -5 for next check

        // 3.)
        // 906 mod 125 - 1 - 5 = 25
        // 25 does fit with 25s, so:
        // 25 / 25 = 1 --> answer is '1' --> -25 for next check

        // 4.)
        // 906 mod 625 - 1 - 5 - 25 = 250
        // 250 does fit with 125s, so:
        // 250 / 125 = 2 --> answer is '2' --> -250 for next check

        // etc

        // --------------

        // 2=0=      198
        // 1.)
        // 198 mod 5 = 3
        // 3 doesnt fit with 1s, so:
        // 5 - (2*1) = 3 --> answer is '=' --> +2 for next check

        // 2.)
        // 198 mod 25 + 2 = 25
        // 25 doesn't fit with 5s, so:
        // 25 - (0*5) = 25 --> answer is '0' --> no adding for next check

        // 3.)
        // 198 mod 125 + 2 = 75
        // 75 doesn't fit with 25s, so:
        // 125 - (2*25) = 75 --> answer is '=' --> +50 for next check

        // 4.)
        // 198 mod 625 + 2 + 50 = 250
        // 250 does fit with 125s, so:
        // 250 / 125 = 2 --> answer is '2' --> -250 for next check (if existed)

        // 2=01      201
        // 201 mod 5 = 1
        // answer is 1 -> -1 for next
        // 201 mod 25 - 1 = 0
        // answer is 0 -> +-0 for next
        // 201 mod 125 - 1 = 75
        // answer is '=' -> +50 for next
        // 201 mod 625 - 1 + 50 = 250
        // answer is '2' -> -250 for next

        return snafu.reversed()
    }

    override fun initialize(input: List<String>) {
        snafuNumbers = input
    }

    override fun calculateSolutionA(): String {
        val decimalSum = snafuNumbers.sumOf { it.toDecimal() }
        return decimalSum.toSnafu()
    }

    override fun calculateSolutionB(): String {
        return ""
    }
}
