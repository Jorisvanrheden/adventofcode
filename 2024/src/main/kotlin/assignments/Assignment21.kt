package assignments

import models.assignment.Assignment
import models.vector.Vector2D

class Assignment21 : Assignment(21) {
    private lateinit var instructions: List<String>

    override fun initialize(input: List<String>) {
        instructions = input
    }

    override fun calculateSolutionA(): String {
        val pads = listOf(
            NumericalPad(),
            DirectionalPad(),
            DirectionalPad(),
        )
        return instructions
            .sumOf { it.removeSuffix("A").toInt() * getShortestButtonPresses(it, pads).count() }
            .toString()
    }

    override fun calculateSolutionB(): String {
        return ""
    }

    private fun getShortestButtonPresses(currentInput: String, pads: List<BasePad>): String {
        var inputs = listOf(currentInput)
        for (pad in pads) {
            inputs = inputs
                .map { it.map { pad.input(it) } }
                .map { getPermutations(it) }
                .flatten()
        }
        return inputs.minBy { it.length }
    }

    private fun getPermutations(lists: List<List<String>>): List<String> {
        val permutations: MutableList<String> = ArrayList()
        generatePermutations(lists, 0, "", permutations)
        return permutations
    }

    private fun generatePermutations(
        lists: List<List<String>>,
        depth: Int,
        current: String,
        permutations: MutableList<String>
    ) {
        if (depth == lists.size) {
            permutations.add(current)
            return
        }

        for (element in lists[depth]) {
            generatePermutations(lists, depth + 1, current + element, permutations)
        }
    }

    abstract class BasePad(
        private var currentCharacter: Char,
    ) {
        data class Instruction(
            val target: Char,
            val direction: Vector2D,
        )
        protected abstract val connections: Map<Char, List<Instruction>>

        fun input(characterToPress: Char): List<String> {
            // Numpad
            // +---+---+---+
            // | 7 | 8 | 9 |
            // +---+---+---+
            // | 4 | 5 | 6 |
            // +---+---+---+
            // | 1 | 2 | 3 |
            // +---+---+---+
            //     | 0 | A |
            //     +---+---+

            // Directional
            //     +---+---+
            //     | ^ | A |
            // +---+---+---+
            // | < | v | > |
            // +---+---+---+

            val paths = mutableListOf<List<Char>>()
            fun recurse(currentCharacter: Char, path: MutableList<Char> = mutableListOf()) {
                path.add(currentCharacter)

                if (currentCharacter == characterToPress) {
                    paths.add(path.toList())
                } else {
                    connections[currentCharacter]!!.forEach {
                        if (!path.contains(it.target)) {
                            recurse(it.target, path)
                        }
                    }
                }
                path.removeAt(path.size - 1)
            }
            recurse(currentCharacter)

            currentCharacter = characterToPress

            return paths
                .filterByMinSize()
                .map {
                    it
                        .zipWithNext { a, b ->  connections[a]!!.first { it.target == b }.direction }
                        .map { it.toChar() }
                        .toMutableList().apply { add('A') }
                        .joinToString(separator = "")
                }
        }

        private fun <T> List<List<T>>.filterByMinSize(): List<List<T>> {
            val minSize = this.minOfOrNull { it.size } ?: return emptyList() // Find the minimum size
            return this.filter { it.size == minSize } // Retain only the lists with the minimum size
        }

        private fun Vector2D.toChar(): Char =
            when (this) {
                Vector2D.UP -> '^'
                Vector2D.RIGHT -> '>'
                Vector2D.DOWN -> 'v'
                Vector2D.LEFT -> '<'
                else -> throw Exception("Invalid Vector2D $this")
            }
    }

    class NumericalPad : BasePad('A') {
        private val c = mapOf(
            '7' to listOf(
                Instruction('4', Vector2D.DOWN),
                Instruction('8', Vector2D.RIGHT),
            ),
            '8' to listOf(
                Instruction('7', Vector2D.LEFT),
                Instruction('5', Vector2D.DOWN),
                Instruction('9', Vector2D.RIGHT),
            ),
            '9' to listOf(
                Instruction('8', Vector2D.LEFT),
                Instruction('6', Vector2D.DOWN),
            ),
            '4' to listOf(
                Instruction('7', Vector2D.UP),
                Instruction('5', Vector2D.RIGHT),
                Instruction('1', Vector2D.DOWN),
            ),
            '5' to listOf(
                Instruction('4', Vector2D.LEFT),
                Instruction('8', Vector2D.UP),
                Instruction('6', Vector2D.RIGHT),
                Instruction('2', Vector2D.DOWN),
            ),
            '6' to listOf(
                Instruction('9', Vector2D.UP),
                Instruction('5', Vector2D.LEFT),
                Instruction('3', Vector2D.DOWN),
            ),
            '1' to listOf(
                Instruction('4', Vector2D.UP),
                Instruction('2', Vector2D.RIGHT),
            ),
            '2' to listOf(
                Instruction('1', Vector2D.LEFT),
                Instruction('5', Vector2D.UP),
                Instruction('3', Vector2D.RIGHT),
                Instruction('0', Vector2D.DOWN),
            ),
            '3' to listOf(
                Instruction('6', Vector2D.UP),
                Instruction('2', Vector2D.LEFT),
                Instruction('A', Vector2D.DOWN),
            ),
            '0' to listOf(
                Instruction('2', Vector2D.UP),
                Instruction('A', Vector2D.RIGHT),
            ),
            'A' to listOf(
                Instruction('3', Vector2D.UP),
                Instruction('0', Vector2D.LEFT),
            ),
        )

        override val connections: Map<Char, List<Instruction>>
            get() = c
    }

    class DirectionalPad : BasePad('A') {
        private val c = mapOf(
            '<' to listOf(
                Instruction('v', Vector2D.RIGHT)
            ),
            'v' to listOf(
                Instruction('<', Vector2D.LEFT),
                Instruction('^', Vector2D.UP),
                Instruction('>', Vector2D.RIGHT),
            ),
            '>' to listOf(
                Instruction('v', Vector2D.LEFT),
                Instruction('A', Vector2D.UP),
            ),
            '^' to listOf(
                Instruction('v', Vector2D.DOWN),
                Instruction('A', Vector2D.RIGHT),
            ),
            'A' to listOf(
                Instruction('^', Vector2D.LEFT),
                Instruction('>', Vector2D.DOWN),
            ),
        )

        override val connections: Map<Char, List<Instruction>>
            get() = c
    }
}

