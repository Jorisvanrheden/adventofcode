package assignments

import models.assignment.Assignment
import kotlin.math.pow

class Assignment17 : Assignment(17) {
    data class Register(
        var a: Long = 0,
        var b: Long = 0,
        var c: Long = 0,
    ) {
        companion object {
            fun of(input: List<String>): Register =
                Register(
                    input[0].substringAfterLast(" ").toLong(),
                    input[1].substringAfterLast(" ").toLong(),
                    input[2].substringAfterLast(" ").toLong(),
                )
        }
    }
    private lateinit var register: Register
    private lateinit var program: List<Int>

    override fun initialize(input: List<String>) {
        register = Register.of(input)
        program = input[4].substringAfterLast(" ").split(",").map { it.toInt() }
    }

    override fun calculateSolutionA(): String {
        return register.copy()
            .runProgram(program)
            .joinToString (",")
    }

    override fun calculateSolutionB(): String {
        var candidates = listOf(0L)
        for (instruction in program.reversed()) {
            candidates = candidates.flatMap { candidate ->
                candidate
                    .toValuesToConsider()
                    .filter { Register(a = it).runProgram(program).first() == instruction }
            }
        }
        return candidates.first().toString()
    }

    private fun Long.toValuesToConsider(): LongRange {
        val shifted = this shl 3
        return LongRange(shifted, shifted + 8)
    }

    private fun Register.runProgram(program: List<Int>): MutableList<Int> {
        val output = mutableListOf<Int>()
        var pointer = 0
        while (pointer < program.lastIndex) {
            val instruction = program[pointer]
            val literalOperand = program[pointer + 1]
            val comboOperand = literalOperand.toComboOperand(this)

            when (instruction) {
                0 -> a = (a / 2.0.pow(comboOperand.toDouble())).toLong()
                1 -> b = b xor literalOperand.toLong()
                2 -> b = comboOperand.mod(8).toLong()
                3 -> {
                    if (a != 0L) {
                        pointer = literalOperand
                        continue
                    }
                }
                4 -> b = b xor c
                5 -> output.add(comboOperand.mod(8))
                6 -> b = (a / 2.0.pow(comboOperand.toDouble())).toLong()
                7 -> c = (a / 2.0.pow(comboOperand.toDouble())).toLong()
                else -> {}
            }
            pointer += 2
        }
        return output
    }

    private fun Int.toComboOperand(register: Register): Long =
        when (this) {
            0, 1, 2, 3 -> toLong()
            4 -> register.a
            5 -> register.b
            6 -> register.c
            else -> throw Exception("Invalid Input")
        }
}

