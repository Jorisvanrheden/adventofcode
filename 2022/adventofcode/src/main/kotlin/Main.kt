import assignments.Assignment
import assignments.Assignment1
import assignments.Assignment2
import utilities.Utilities

fun main(args: Array<String>) {
    val assignment: Assignment = Assignment2()

    val input: List<String> = Utilities.readFile("src/main/kotlin/input/" + assignment.getInput())

    assignment.initialize(input)
    assignment.run()
}
