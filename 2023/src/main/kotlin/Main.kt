import assignments.* // ktlint-disable no-wildcard-imports
import utilities.Utilities

fun main(args: Array<String>) {
    val assignment: Assignment = Assignment16()

    val input: List<String> = Utilities.readFile("2023/src/main/kotlin/input/" + assignment.getInput())

    assignment.initialize(input)
    assignment.run()
}
