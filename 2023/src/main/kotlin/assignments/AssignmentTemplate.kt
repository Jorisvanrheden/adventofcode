package assignments

import models.assignment.Assignment

class AssignmentTemplate : Assignment() {

    override fun getInput(): String {
        return "input_x"
    }

    override fun initialize(input: List<String>) {
    }

    override fun calculateSolutionA(): String {
        return ""
    }

    override fun calculateSolutionB(): String {
        return ""
    }
}
