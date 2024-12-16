package assignments

import models.assignment.Assignment
import models.matrix.CharMatrix
import models.vector.Vector2D
import utilities.Utilities

class Assignment15 : Assignment(15) {
    private lateinit var instructions: String
    private lateinit var matrix: CharMatrix

    override fun initialize(input: List<String>) {
        Utilities.packageByEmptyLine(input).let {
            val map = it[0]
            matrix = CharMatrix(map.size, map[0].length).apply {
                for (i in 0 until rows) {
                    for (j in 0 until columns) {
                        values[i][j] = map[i][j]
                    }
                }
            }
            instructions = it[1].reduce { a, b -> a + b }
        }
    }

    override fun calculateSolutionA(): String {
        return matrix
            .copy()
            .simulate(listOf('O'))
            .occurrencesOf('O')
            .sumOf { it.x * 100 + it.y }
            .toString()
    }

    override fun calculateSolutionB(): String {
        return matrix
            .expand()
            .simulate(listOf('[', ']'))
            .occurrencesOf('[')
            .sumOf { it.x * 100 + it.y }
            .toString()
    }

    private fun CharMatrix.simulate(wallCharacters: List<Char>): CharMatrix {
        var current = occurrencesOf('@').first()
        for (i in instructions.indices) {
            val direction = instructions[i].toDirection()
            val next = current + direction
            when {
                values[next.x][next.y] == '#' -> continue
                values[next.x][next.y] in wallCharacters -> {
                    val blocksToPush = calculateBlocksToPush(mutableSetOf(current), direction)
                    if (blocksToPush.isNotEmpty()) {
                        val original = blocksToPush.associateWith { values[it.x][it.y] }

                        blocksToPush.forEach {
                            values[it.x][it.y] = '.'
                        }

                        original.forEach { (block, value) ->
                            values[block.x + direction.x][block.y + direction.y] = value
                        }

                        values[current.x][current.y] = '.'
                        values[next.x][next.y] = '@'
                        current = next
                    }
                }
                values[next.x][next.y] == '.' -> {
                    values[current.x][current.y] = '.'
                    values[next.x][next.y] = '@'
                    current = next
                }
            }
        }
        return this
    }

    private fun CharMatrix.calculateBlocksToPush(blocks: Set<Vector2D>, direction: Vector2D, totalBlocks: Set<Vector2D> = mutableSetOf()): Set<Vector2D> {
        val blocksNextLayer = blocks.flatMap { getBlockForNextLayer(it, direction) }
        return when {
            blocksNextLayer.any { values[it.x][it.y] == '#' } -> return emptySet()
            blocksNextLayer.all { values[it.x][it.y] == '.' } -> return totalBlocks + blocks
            else -> {
                calculateBlocksToPush(
                    blocksNextLayer.filter { values[it.x][it.y] != '.' }.toMutableSet(),
                    direction,
                    totalBlocks + blocks
                )
            }
        }
    }

    private fun CharMatrix.getBlockForNextLayer(block: Vector2D, direction: Vector2D) =
        buildSet {
            val next = block + direction
            add(next)
            if (direction == Vector2D.UP || direction == Vector2D.DOWN) {
                when(values[next.x][next.y]) {
                    '[' -> add(next.right())
                    ']' -> add(next.left())
                }
            }
        }

    private fun CharMatrix.expand(): CharMatrix {
        val expandedMatrix = CharMatrix(rows, columns * 2)
        for (i in 0 until rows) {
            for (j in 0 until columns) {
                when (values[i][j]) {
                    '#' -> expandedMatrix.expandEntry(i, j, '#', '#')
                    '.' -> expandedMatrix.expandEntry(i, j, '.', '.')
                    '@' -> expandedMatrix.expandEntry(i, j, '@', '.')
                    'O' -> expandedMatrix.expandEntry(i, j, '[', ']')
                }
            }
        }
        return expandedMatrix
    }

    private fun CharMatrix.expandEntry(i: Int, j: Int, charLeft: Char, charRight: Char) {
        values[i][j * 2] = charLeft
        values[i][j * 2 + 1] = charRight
    }

    private fun Char.toDirection() =
        when (this) {
            '>' -> Vector2D.RIGHT
            '^' -> Vector2D.UP
            '<' -> Vector2D.LEFT
            'v' -> Vector2D.DOWN
            else -> throw Exception("Unknown direction: $this")
        }
}

