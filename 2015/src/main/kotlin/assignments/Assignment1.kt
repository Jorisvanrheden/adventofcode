package assignments

import models.assignment.Assignment

class Assignment1 : Assignment() {
    override fun getInput(): String {
        return "input_1"
    }

    private lateinit var input: String

    override fun initialize(input: List<String>) {
        this.input = input[0]
    }

    override fun calculateSolutionA(): String {
        return (input.count { it == '(' } - input.count { it == ')' }).toString()
    }

    override fun calculateSolutionB(): String {
        var floor = 0
        for (i in input.indices) {
            if (input[i] == '(') floor++
            else if (input[i] == ')') floor--
            if (floor == -1) return (i + 1).toString()
        }
        return "Answer not found"
    }
}
