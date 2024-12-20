package assignments

import models.assignment.Assignment

class Assignment6 : Assignment(6) {

    private lateinit var message: String

    override fun initialize(input: List<String>) {
        message = input.first()
    }

    private fun getIndexAfterDistinctChunkSize(input: String, chunkSize: Int): Int {
        for (i in 0 until input.length - chunkSize) {
            if (input.substring(i, i + chunkSize)
                .toSet()
                .size == chunkSize
            ) {
                return (i + chunkSize)
            }
        }
        return -1
    }

    override fun calculateSolutionA(): String {
        return getIndexAfterDistinctChunkSize(message, 4).toString()
    }

    override fun calculateSolutionB(): String {
        return getIndexAfterDistinctChunkSize(message, 14).toString()
    }
}
