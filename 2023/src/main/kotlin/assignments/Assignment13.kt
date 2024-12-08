package assignments

import models.assignment.Assignment
import utilities.Utilities
import kotlin.math.min

class Assignment13 : Assignment(13) {
    private lateinit var chunks: List<List<String>>

    override fun initialize(input: List<String>) {
        chunks = Utilities.packageByEmptyLine(input)
    }

    override fun calculateSolutionA(): String {
        val rowScore = chunks
            .filter { it.findRowMirrorIndex() != -1 }
            .sumOf { it.findRowMirrorIndex() * 100 }

        val columnScore = chunks
            .filter { it.findColumnMirrorIndex() != -1 }
            .sumOf { it.findColumnMirrorIndex() }

        return (columnScore + rowScore).toString()
    }

    override fun calculateSolutionB(): String {
        return ""
    }

    private fun List<String>.findColumnMirrorIndex() =
        rotate90degrees().findRowMirrorIndex()

    private fun List<String>.rotate90degrees() =
        this[0]
            .indices
            .map {
                val column = mutableListOf<Char>()
                for (row in this) {
                    column += row[it]
                }
                column.joinToString("")
            }

    private fun List<String>.findRowMirrorIndex(): Int {
        for (i in indices) {
            val contentToMirror = subList(0, i + 1)
            val mirroredContent = createMirror(i + 1, contentToMirror.size)

            val mirrorSize = min(contentToMirror.size, mirroredContent.size)
            if (mirrorSize == 0) {
                continue
            }

            if (compare(
                contentToMirror.takeLast(mirrorSize).reversed(),
                mirroredContent.takeLast(mirrorSize)
            )) {
                return i + 1
            }
        }

        return -1
    }

    private fun compare(a: List<String>, b: List<String>): Boolean {
        val flatA = a.reduce { a, b -> a.plus(b) }
        val flatB = b.reduce { a, b -> a.plus(b) }

        // for solution A,
        // val requiredDifference = 0

        // for solution B
        val requiredDifference = 1
        return flatA
            .indices
            .count { flatA[it] != flatB[it] } == requiredDifference
    }

    private fun List<String>.createMirror(startIndex: Int, size: Int) =
        IntRange(0, size - 1)
            .map { startIndex + it }
            .filter { it < count() }
            .map { this[it] }
}
