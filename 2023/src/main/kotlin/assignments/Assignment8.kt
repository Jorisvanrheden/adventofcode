package assignments

import models.assignment.Assignment
import kotlin.math.max

class Assignment8 : Assignment(8) {
    private lateinit var instructions: String
    private lateinit var connections: List<Connection>

    data class Connection(
        val key: String,
        val left: Int,
        val right: Int,
    )

    override fun initialize(input: List<String>) {
        val names = input.subList(2, input.lastIndex + 1).map {
            it.split(" = ")[0]
        }
        instructions = input.first()

        connections = input.subList(2, input.lastIndex + 1).map {
            val chunks = it.split(" = ")
            val connections = chunks[1]
                .trim('(', ')')
                .split(", ")
            Connection(
                chunks[0],
                names.indexOf(connections[0]),
                names.indexOf(connections[1]),
            )
        }
    }

    override fun calculateSolutionA() =
        calculateStepsFromTo(connections.indexOfFirst { it.key == "AAA" }) {
            connections[it].key == "ZZZ"
        }.toString()

    override fun calculateSolutionB() =
        connections
            .filter { it.key.endsWith("A") }
            .map { connections.indexOf(it) }
            .map {
                calculateStepsFromTo(it) { index ->
                    connections[index].key.endsWith("Z")
                }
            }
            .lcm()
            .toString()

    private fun List<Long>.lcm(): Long {
        var result = first()
        for (i in 1 until size) {
            result = findLCM(result, this[i])
        }
        return result
    }

    private fun findLCM(a: Long, b: Long): Long {
        val largestInput = max(a, b)
        var lcm = largestInput
        while (true) {
            if (lcm % a == 0L && lcm % b == 0L) {
                return lcm
            }
            lcm += largestInput
        }
    }

    private fun calculateStepsFromTo(startIndex: Int, predicate: (index: Int) -> Boolean): Long {
        var steps = 0L
        var index = startIndex
        while (!predicate(index)) {
            instructions.instructionAtStep(steps).let {
                if (it == 'R') {
                    index = connections[index].right
                } else if (it == 'L') {
                    index = connections[index].left
                }
            }
            steps++
        }
        return steps
    }

    private fun String.instructionAtStep(step: Long) =
        this[step.mod(length)]
}
