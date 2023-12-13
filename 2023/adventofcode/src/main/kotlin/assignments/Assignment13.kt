package assignments

import utilities.Utilities

class Assignment13 : Assignment() {

    override fun getInput(): String {
        return "input_13"
    }

    private lateinit var chunks: List<List<String>>

    override fun initialize(input: List<String>) {
        chunks = Utilities.packageByEmptyLine(input)
    }

    override fun calculateSolutionA(): String {
        // first check all row indices
        val chunksWithMirroredRow = chunks.filter { it.findRowMirrorIndex() != -1 }

        val rowScore = chunksWithMirroredRow
            .mapNotNull {
                val rowIndex = it.findRowMirrorIndex()
                if (rowIndex != -1 ) {
                    rowIndex
                } else {
                    null
                }
            }.sumOf { it * 100 }

        val columnScore = chunks
            .filter { !chunksWithMirroredRow.contains(it) }
            .mapNotNull {
                val columnIndex = it.findColumnMirrorIndex()
                if (columnIndex != -1 ) {
                    columnIndex
                } else {
                    null
                }
            }.sumOf { it }

        return (columnScore + rowScore).toString()
    }

    override fun calculateSolutionB(): String {
        return ""
    }

    private fun List<String>.findColumnMirrorIndex(): Int {
        // for each column add all items
        val columns = mutableListOf<String>()
        for (i in 0 until this[0].length) {
            val column = mutableListOf<Char>()
            for (row in this) {
                column += row[i]
            }
            columns.add(column.joinToString(""))
        }

        return columns.findRowMirrorIndex()
    }

    private fun List<String>.findRowMirrorIndex(): Int {
        val rowsToMirror = mutableListOf<String>()

        for (i in indices) {
            rowsToMirror.add(this[i])

            val mirror = createMirror(i + 1, rowsToMirror.size)

            val originalPartWithMirrorSize =
                rowsToMirror
                    .reversed()
                    .subList(0, mirror.size)

            if (mirror.isNotEmpty() && compare (originalPartWithMirrorSize, mirror)) {
                return i + 1
            }
        }

        return -1
    }

    private fun compare(a: List<String>, b: List<String>): Boolean {
        var diff = 0

        val flatA = a.reduce { a, b -> a.plus(b) }
        val flatB = b.reduce { a, b -> a.plus(b) }

        for (i in flatA.indices) {
            if (flatA[i] != flatB[i]) {
                diff++
            }
        }
        return diff == 1
    }

    private fun List<String>.createMirror(index: Int, size: Int): List<String> {
        val mirror = mutableListOf<String>()
        for (i in 0 until size) {
            val newIndex = index + i
            if (newIndex < count()) {
                mirror.add(this[newIndex])
            }
        }
        return mirror
    }
}
