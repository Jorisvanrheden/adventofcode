import assignments.* // ktlint-disable no-wildcard-imports
import models.assignment.Assignment
import utilities.Utilities

fun main(args: Array<String>) {
    val assignment: Assignment = Assignment10()

    val input: List<String> = Utilities.readFile("2023/src/main/kotlin/input/" + assignment.getInput())

    assignment.initialize(input)
    assignment.run()
}
