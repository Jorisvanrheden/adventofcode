package assignments

class Assignment20 : Assignment() {

    override fun getInput(): String {
        return "input_20"
    }

    data class Number(val name: String, val x: Int)

    private lateinit var numbers: List<Number>

    override fun initialize(input: List<String>) {
        numbers = input.mapIndexed { index, it ->
            Number(index.toString(), it.toInt())
        }
    }

    private fun List<Number>.mix(index: Int): List<Number> {
        var c = toMutableList()

        var valueAtIndex = this[index]

        var destinationIndex = index + valueAtIndex.x
        destinationIndex = destinationIndex.mod(size - 1)

//        // wrap around the other side
//        if (destinationIndex == 0) destinationIndex = size - 1
//        else if (destinationIndex == size - 1) destinationIndex = 0
//
//        if (destinationIndex < 0 || destinationIndex > size - 1) {
//            destinationIndex = destinationIndex.mod(size - 1)
//        }

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

        c.removeAt(index)
        c.add(destinationIndex, valueAtIndex)

        return c
    }

    override fun calculateSolutionA(): String {
        var mixed = numbers.toMutableList()
        for (number in numbers) {
            // get index in updated list
            val numberIndex = mixed.indexOf(number)
            mixed = mixed.mix(numberIndex).toMutableList()
        }

        val zeroIndex = mixed.indexOfFirst { it.x == 0 }!!

        return listOf(1000, 2000, 3000).map {
            val index = (zeroIndex + it) % mixed.size
            mixed[index]
        }
            .sumOf { it.x }
            .toString()
    }

    override fun calculateSolutionB(): String {
        return ""
    }
}
