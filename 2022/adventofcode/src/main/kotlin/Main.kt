import assignments.Assignment
import assignments.Assignment1
import assignments.Assignment2
import assignments.Assignment3
import assignments.Assignment4
import assignments.Assignment5
import assignments.Assignment6
import utilities.Utilities

fun main(args: Array<String>) {
    val assignment: Assignment = Assignment6()

    val input: List<String> = Utilities.readFile("src/main/kotlin/input/" + assignment.getInput())

    assignment.initialize(input)
    assignment.run()
}
