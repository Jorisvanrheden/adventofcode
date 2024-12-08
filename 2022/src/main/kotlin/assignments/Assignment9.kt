package assignments

import models.assignment.Assignment
import models.vector.Vector2D
import kotlin.math.abs

class Assignment9 : Assignment(9) {

    data class Instruction(val direction: Vector2D, val steps: Int)

    private lateinit var instructions: List<Instruction>

    private fun Int.clamp(): Int {
        if (this > 0) return 1
        if (this < 0) return -1
        return this
    }

    private fun parseDirection(input: String): Vector2D =
        when (input) {
            "R" -> Vector2D(1, 0)
            "L" -> Vector2D(-1, 0)
            "U" -> Vector2D(0, 1)
            "D" -> Vector2D(0, -1)
            else -> Vector2D(0, 0)
        }

    override fun initialize(input: List<String>) {
        instructions = input.map {
            val parts = it.split(' ')
            Instruction(
                parseDirection(parts[0]),
                parts[1].toInt()
            )
        }
    }

    private fun Vector2D.isConnectedTo(vector2D: Vector2D): Boolean {
        return abs(vector2D.x - x) <= 1 && abs(vector2D.y - y) <= 1
    }

    private fun resolveNode(guider: Vector2D, follower: Vector2D): Vector2D {
        // if neighboring (adjacent or diagonal), return original node
        if (follower.isConnectedTo(guider)) return follower

        // limit step size to 1
        var xAddition = (guider.x - follower.x).clamp()
        var yAddition = (guider.y - follower.y).clamp()

        return follower + Vector2D(xAddition, yAddition)
    }

    private fun processStep(knots: MutableList<Vector2D>, direction: Vector2D, visitedNodes: MutableList<Vector2D>) {
        // apply the direction to the head node
        knots[0] += direction

        // let each knot be influenced by its upper-adjacent node
        for (i in 1 until knots.size) {
            knots[i] = resolveNode(knots[i - 1], knots[i])
        }

        // store the tail node positions
        if (!visitedNodes.contains(knots[knots.size - 1])) {
            visitedNodes.add(knots[knots.size - 1])
        }
    }

    override fun calculateSolutionA(): String {
        val nodes = MutableList(2) { Vector2D(0, 0) }
        var visitedNodes = mutableListOf<Vector2D>()
        for (instruction in instructions) {
            for (i in 0 until instruction.steps) {
                processStep(nodes, instruction.direction, visitedNodes)
            }
        }
        return visitedNodes.size.toString()
    }

    override fun calculateSolutionB(): String {
        val nodes = MutableList(10) { Vector2D(0, 0) }
        var visitedNodes = mutableListOf<Vector2D>()
        for (instruction in instructions) {
            for (i in 0 until instruction.steps) {
                processStep(nodes, instruction.direction, visitedNodes)
            }
        }
        return visitedNodes.size.toString()
    }
}
