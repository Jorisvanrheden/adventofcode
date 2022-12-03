import assignments.Assignment
import assignments.Assignment1
import assignments.Assignment2
import assignments.Assignment3
import utilities.Utilities

fun main(args: Array<String>) {
    val assignment: Assignment = Assignment3()

    val input: List<String> = Utilities.readFile("src/main/kotlin/input/" + assignment.getInput())

    assignment.initialize(input)
    assignment.run()
}
