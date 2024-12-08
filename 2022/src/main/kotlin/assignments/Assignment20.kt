package assignments

import models.assignment.Assignment

class Assignment20 : Assignment(20) {
    data class Number(val name: String, var x: Long) {
        override fun equals(other: Any?): Boolean {
            return name == (other as Number)?.name
        }
    }

    private lateinit var numbers: List<Number>

    override fun initialize(input: List<String>) {
        numbers = input.mapIndexed { index, it ->
            Number(index.toString(), it.toLong())
        }
    }

    private fun List<Number>.mix(index: Int): List<Number> {
        var c = toMutableList()

        var valueAtIndex = this[index]

        var destinationIndex = index + valueAtIndex.x
        destinationIndex = destinationIndex.mod(size - 1).toLong()

        // see it like, a number is between index x1 and x2
        // example: size 7, index 1, value -2
        // start position is between indices 0 and 2
        // new indices are -2 and 0
        // 4, -2, 5, 6, 7, 8, 9
        // 4, 5, 6, 7, 8, -2, 9

        // if destination index is < 0
        // go the other way
        // destination index is size - destination index - 1

        //  4, 5, 6, 1, 7, 2, 9
        //                 |
        // [0][1][2][3][4][5][6]
        //  4, 2, 5, 6, 1, 7, 9

        // if destination index > size - 1
        // destination index = destination index % size

        // just use mod for both cases?

        c.removeAt(index)
        c.add(destinationIndex.toInt(), valueAtIndex)

        return c
    }

    private fun getGroveCoordinates(mixedList: List<Number>): Long {
        val zeroIndex = mixedList.indexOfFirst { it.x == 0L }!!

        return listOf(1000, 2000, 3000).map {
            val index = (zeroIndex + it) % mixedList.size
            mixedList[index]
        }
            .sumOf { it.x }
    }

    override fun calculateSolutionA(): String {
        var mixed = numbers.toMutableList()
        for (number in numbers) {
            // get index in updated list
            val numberIndex = mixed.indexOf(number)
            mixed = mixed.mix(numberIndex).toMutableList()
        }

        return getGroveCoordinates(mixed).toString()
    }

    override fun calculateSolutionB(): String {
        var mixed = numbers.toMutableList().onEach {
            it.x *= 811589153
        }

        for (i in 0 until 10) {
            for (number in numbers) {
                // get index in updated list
                val numberIndex = mixed.indexOf(number)
                mixed = mixed.mix(numberIndex).toMutableList()
            }
        }
        return getGroveCoordinates(mixed).toString()
    }
}
