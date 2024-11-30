import assignments.Assignment
import assignments.Assignment2
import assignments.Assignment3
import assignments.Assignment4
import assignments.Assignment5
import utilities.Utilities

fun main(args: Array<String>) {
    val assignment: Assignment = Assignment5()

    val input: List<String> = Utilities.readFile("2015/src/main/kotlin/input/" + assignment.getInput())

    assignment.initialize(input)
    assignment.run()
}
