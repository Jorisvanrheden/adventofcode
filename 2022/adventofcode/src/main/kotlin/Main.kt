import assignments.Assignment
import assignments.Assignment10
import assignments.Assignment11
import assignments.Assignment12
import assignments.Assignment13
import assignments.Assignment14
import assignments.Assignment15
import assignments.Assignment16
import assignments.Assignment8
import assignments.Assignment9
import utilities.Utilities

fun main(args: Array<String>) {
    val assignment: Assignment = Assignment16()

    val input: List<String> = Utilities.readFile("src/main/kotlin/input/" + assignment.getInput())

    assignment.initialize(input)
    assignment.run()
}
