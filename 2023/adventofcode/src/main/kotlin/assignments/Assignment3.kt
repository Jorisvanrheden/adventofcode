package assignments

import kotlin.math.abs

class Assignment3 : Assignment() {
    private lateinit var numbers: List<GridNumber>
    private lateinit var symbols: List<Symbol>

    data class GridNumber(val index: Int, val value: Int, val range: IntRange)
    data class Symbol(val index: Int, val value: Char, val column: Int)

    override fun getInput(): String {
        return "input_3"
    }

    override fun initialize(input: List<String>) {
        numbers = input.flatMapIndexed { index, it ->
            it.extractNumbers(index)
        }
        symbols = input.flatMapIndexed { index, it ->
            it.extractSymbols(index)
        }
    }

    private fun String.extractSymbols(index: Int) =
        mapIndexedNotNull { column, c ->
            column.takeIf { !c.isDigit() && c != '.' }
        }.map {
            Symbol(
                index,
                this[it],
                it,
            )
        }

    private fun String.extractNumbers(index: Int): MutableList<GridNumber> {
        val numbers = mutableListOf<GridNumber>()
        var offset = 0
        var strippedString = this

        while (strippedString.firstNumberStartIndex() != -1) {
            val startIndex = strippedString.firstNumberStartIndex()
            val endIndex = strippedString.firstNumberEndIndex(startIndex)
            numbers.add(
                GridNumber(
                    index,
                    strippedString.substring(startIndex, endIndex).toInt(),
                    IntRange(startIndex + offset - 1, endIndex + offset),
                ),
            )
            strippedString = strippedString.substring(endIndex)
            offset += endIndex
        }
        return numbers
    }

    private fun String.firstNumberStartIndex() =
        indexOfFirst { it.isDigit() }

    private fun String.firstNumberEndIndex(startIndex: Int): Int {
        var endIndex = startIndex
        while (endIndex <= lastIndex && this[endIndex].isDigit()) {
            endIndex++
        }
        return endIndex
    }

    override fun calculateSolutionA() =
        numbers
            .filter { it.hasAdjacentSymbol(symbols) }
            .sumOf { it.value }
            .toString()

    override fun calculateSolutionB() =
        symbols
            .filter { it.value == '*' }
            .sumOf { it.calculateGearRatio(numbers) }
            .toString()

    private fun Symbol.calculateGearRatio(numbers: List<GridNumber>) =
        numbers
            .filter { it.hasAdjacentSymbol(listOf(this)) }
            .map { it.value }
            .let {
                if (it.count() == 2) {
                    it[0] * it[1]
                } else {
                    0
                }
            }

    private fun GridNumber.hasAdjacentSymbol(symbols: List<Symbol>) =
        // find overlap: deltaX <= 1 && deltaY <= 1
        symbols.any {
            abs(index - it.index) <= 1 && range.contains(it.column)
        }
}
