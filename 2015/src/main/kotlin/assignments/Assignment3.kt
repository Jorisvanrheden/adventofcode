package assignments

import models.assignment.Assignment
import models.vector.Vector2D

class Assignment3 : Assignment(3) {
    private lateinit var input: String

    override fun initialize(input: List<String>) {
        this.input = input[0]
    }

    private fun Char.toVector2D() =
        when (this) {
            '>' -> Vector2D(1, 0)
            '<' -> Vector2D(-1, 0)
            '^' -> Vector2D(0, -1)
            'v' -> Vector2D(0, 1)
            else -> Vector2D(0, 0)
        }

    private fun MutableMap<Vector2D, Int>.appendPosition(position: Vector2D) {
        if (containsKey(position)) {
            this[position] = this[position]!! + 1
        } else {
            this[position] = 1
        }
    }

    override fun calculateSolutionA(): String {
        var visitedNodes = mutableMapOf<Vector2D, Int>()
        var position = Vector2D(0, 0)
        visitedNodes[position] = 1
        for (i in input.indices) {
            position += input[i].toVector2D()
            visitedNodes.appendPosition(position)
        }
        return visitedNodes.count { it.value >= 1 }.toString()
    }

    override fun calculateSolutionB(): String {
        var visitedNodes = mutableMapOf<Vector2D, Int>()
        var positionA = Vector2D(0, 0)
        var positionB = Vector2D(0, 0)
        visitedNodes[positionA] = 1
        for (i in input.indices) {
            val direction = input[i].toVector2D()

            if (i % 2 == 0) {
                positionA += direction
                visitedNodes.appendPosition(positionA)
            } else {
                positionB += direction
                visitedNodes.appendPosition(positionB)
            }
        }
        return visitedNodes.count { it.value >= 1 }.toString()
    }
}
