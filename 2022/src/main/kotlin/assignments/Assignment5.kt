package assignments

import models.assignment.Assignment
import utilities.Utilities

class Assignment5 : Assignment(5) {
    data class Instruction(val amount: Int, val from: Int, val to: Int)
    data class Stack(var items: MutableList<Char>) {
        fun copy(): Stack {
            return Stack(items.toMutableList())
        }
    }

    private lateinit var stacks: List<Stack>
    private lateinit var instructions: List<Instruction>

    private fun parseInstructions(input: List<String>): List<Instruction> {
        return input.map {
            it.split(' ')
                .let { x ->
                    Instruction(
                        x[1].toInt(),
                        x[3].toInt() - 1,
                        x[5].toInt() - 1
                    )
                }
        }
    }

    private fun parseStacks(input: List<String>): List<Stack> {
        val stackCount = input
            .last()
            .trim()
            .split("\\s+".toRegex())
            .size

        val stackLists = List<MutableList<Char>>(stackCount) { mutableListOf() }

        input.map {
            var index = it.indexOf('[')
            while (index != -1) {
                // get the stack index from all indices
                val stackIndex = input.last()[index + 1].digitToInt() - 1

                // parse the content and push it to the stack
                val content = it[index + 1]
                stackLists[stackIndex].add(
                    content
                )

                // iterate to next occurrence
                index = it.indexOf('[', index + 1)
            }
        }

        return stackLists.map { Stack(it) }
    }

    override fun initialize(input: List<String>) {
        Utilities.packageByEmptyLine(input).let {
            stacks = parseStacks(it[0])
            instructions = parseInstructions(it[1])
        }
    }

    private fun applyInstruction(stacks: MutableList<Stack>, instruction: Instruction) {
        // Iteratively adding items to other stacks
        for (i in 0 until instruction.amount) {
            stacks[instruction.to].items.add(
                0,
                stacks[instruction.from].items.removeFirst()
            )
        }
    }

    private fun applyNewInstruction(stacks: MutableList<Stack>, instruction: Instruction) {
        // Move items to other stacks by chunks
        stacks[instruction.to].items.addAll(
            0,
            stacks[instruction.from].items.take(instruction.amount)
        )

        for (i in 0 until instruction.amount) {
            stacks[instruction.from].items.removeFirst()
        }
    }

    override fun calculateSolutionA(): String {
        val stacksCopy = stacks.map { it.copy() }.toMutableList()
        for (instruction in instructions) {
            applyInstruction(stacksCopy, instruction)
        }
        return stacksCopy.map {
            it.items[0]
        }.joinToString("")
    }

    override fun calculateSolutionB(): String {
        val stacksCopy = stacks.map { it.copy() }.toMutableList()
        for (instruction in instructions) {
            applyNewInstruction(stacksCopy, instruction)
        }

        return stacksCopy.map {
            it.items[0]
        }.joinToString("")
    }
}
