package assignments

import models.assignment.Assignment
import utilities.Utilities

class Assignment11 : Assignment() {
    interface Operation {
        fun computeUpdatedValue(input: Long): Long
    }
    interface OperationValue {
        fun get(oldValue: Long): Long
    }

    class StaticOperationValue(private val newValue: Long) : OperationValue {
        override fun get(oldValue: Long): Long {
            return newValue
        }
    }
    class ReferenceOperationValue : OperationValue {
        override fun get(oldValue: Long): Long {
            return oldValue
        }
    }

    class MultiplyOperation(private val operationValue: OperationValue) : Operation {
        override fun computeUpdatedValue(input: Long): Long {
            return input * operationValue.get(input)
        }
    }

    class AddOperation(private val operationValue: OperationValue) : Operation {
        override fun computeUpdatedValue(input: Long): Long {
            return input + operationValue.get(input)
        }
    }

    data class Monkey(
        val id: Int,
        val items: MutableList<Long>,
        val operation: Operation,
        val divisibleByThreshold: Long,
        val monkeyIfTrue: Int,
        val monkeyIfFalse: Int,
        var inspections: Long = 0
    )

    override fun getInput(): String {
        return "input_11"
    }

    private fun getOperationValue(input: String): OperationValue {
        if (input == "old") return ReferenceOperationValue()
        else {
            return StaticOperationValue(input.toLong())
        }
    }

    private fun getOperationType(operationInput: List<String>): Operation {
        val operationValue = getOperationValue(operationInput[1])
        if (operationInput[0] == "+") return AddOperation(operationValue)
        if (operationInput[0] == "*") return MultiplyOperation(operationValue)
        return AddOperation(operationValue)
    }

    private fun parseInput(input: List<String>): Monkey {
        val id = input[0]
            .replace("Monkey", "")
            .replace(":", "")
            .trim()
            .toInt()

        val items = input[1]
            .split(':')[1]
            .split(',')
            .map { it.trim() }
            .map { it.toLong() }
            .toMutableList()

        val operationParts = input[2]
            .split(':')[1]
            .split(' ')
            .takeLast(2)

        var operation = getOperationType(operationParts)

        val divisibleByThreshold = input[3]
            .split(' ')
            .let { it.last() }
            .toLong()

        val monkeyIfTrue = input[4]
            .split(' ')
            .let { it.last() }
            .toInt()

        val monkeyIfFalse = input[5]
            .split(' ')
            .let { it.last() }
            .toInt()

        return Monkey(id, items, operation, divisibleByThreshold, monkeyIfTrue, monkeyIfFalse)
    }

    private var monkeysInput = listOf<List<String>>()

    override fun initialize(input: List<String>) {
        monkeysInput = Utilities.packageByEmptyLine(input)
    }

    private fun processTurn(monkey: Monkey, monkeys: List<Monkey>, clamp: (Long) -> Long) {
        for (item in monkey.items) {
            monkey.inspections++

            var updatedItem = clamp(monkey.operation.computeUpdatedValue(item))

            var monkeyIndex = monkey.monkeyIfFalse
            if (updatedItem % monkey.divisibleByThreshold == 0L) {
                monkeyIndex = monkey.monkeyIfTrue
            }
            monkeys[monkeyIndex].items.add(updatedItem)
        }
        monkey.items.clear()
    }

    private fun getCommonDenom(monkeys: List<Monkey>): Long {
        var total: Long = 1
        for (monkey in monkeys) {
            total *= monkey.divisibleByThreshold
        }
        return total
    }

    override fun calculateSolutionA(): String {
        val monkeys = monkeysInput.map {
            parseInput(it)
        }
        for (i in 0 until 20) {
            for (monkey in monkeys) {
                processTurn(monkey, monkeys) { x -> (x.toDouble() / 3).toLong() }
            }
        }

        return monkeys.map { it.inspections }
            .sorted()
            .let { it[it.size - 1] * it[it.size - 2] }.toLong()
            .toString()
    }

    override fun calculateSolutionB(): String {
        val monkeys = monkeysInput.map {
            parseInput(it)
        }
        val denom = getCommonDenom(monkeys)
        for (i in 0 until 10000) {
            for (monkey in monkeys) {
                processTurn(monkey, monkeys) { x -> x % denom }
            }
        }

        return monkeys.map { it.inspections }
            .sorted()
            .let { it[it.size - 1] * it[it.size - 2] }.toLong()
            .toString()
    }
}
