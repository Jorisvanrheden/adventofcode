package assignments

import toolkit.Matrix
import kotlin.math.floor

class Assignment10 : Assignment() {

    data class Cycle(var duration: Int, val addition: Int)
    private lateinit var cycles: List<Cycle>

    data class CycleProcessor(val cycles: MutableList<Cycle>) {
        var registerHistory = MutableList(1) { 1 }

        init {
            var registerValue = 1
            while (cycles.isNotEmpty()) {
                var cycle = cycles.first()
                registerValue += cycle.addition

                for (i in 0 until cycle.duration) {
                    registerHistory.add(registerValue)
                }
                cycles.remove(cycle)
            }
        }

        fun getValueAt(index: Int): Int {
            // get the history at index - 2 because:
            // - index is the 'x-th' cycle, but the array starts at 0
            // - the x-th index - 1 return the 'end' status, but we need to retrieve the 'during' status
            return index * registerHistory[index - 2]
        }
    }

    override fun getInput(): String {
        return "input_10"
    }

    override fun initialize(input: List<String>) {
        cycles = input.map {
            it.split(' ').let { parts ->
                if(parts.size == 1) Cycle(1, 0)
                else Cycle(2, parts[1].toInt())
            }
        }
    }

    private fun drawMatrixAt(matrix: Matrix, pixelX: Int, pixelY: Int, cycleX: Int) {
        // check if the pixel chunk (-1 .. 1) overlaps with the cycleX
        var pixelValue = 0
        if (cycleX >= pixelY - 1 && cycleX <= pixelY + 1) {
            pixelValue = 1
        }
        matrix.values[pixelX][pixelY] = pixelValue
    }

    override fun calculateSolutionA(): String {
        val cycleProcessor = CycleProcessor(cycles.toMutableList())

        return listOf(20, 60, 100, 140, 180, 220)
            .sumOf { cycleProcessor.getValueAt(it) }
            .toString()
    }

    override fun calculateSolutionB(): String {
        val cycleProcessor = CycleProcessor(cycles.toMutableList())
        var matrix = Matrix(6, 40)

        for (i in 1 until cycleProcessor.registerHistory.size - 1) {
            // checking how many times the current index fits in the amount of columns gets the row index
            val rowIndex = floor(i.toFloat() / matrix.columns).toInt()
            // constrain the column indices to the columns
            val columnIndex = i % matrix.columns
            val x = cycleProcessor.registerHistory[i - 1] % matrix.columns
            drawMatrixAt(matrix, rowIndex, columnIndex, x)
        }

        return matrix.toString()
    }
}
