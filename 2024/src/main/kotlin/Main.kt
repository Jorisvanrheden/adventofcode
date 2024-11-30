import assignments.* // ktlint-disable no-wildcard-imports
import models.assignment.Assignment
import utilities.Utilities

fun main(args: Array<String>) {
    val assignment: Assignment = Assignment1()

    val input: List<String> = Utilities.readFile("2024/src/main/kotlin/input/" + assignment.getInput())

    assignment.initialize(input)
    assignment.run()
}
