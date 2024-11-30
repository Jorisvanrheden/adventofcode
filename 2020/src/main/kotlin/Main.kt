import assignments.Assignment
import assignments.Assignment7
import utilities.Utilities

fun main(args: Array<String>) {
    val assignment: Assignment = Assignment7()

    val input: List<String> = Utilities.readFile("2020/src/main/kotlin/input/" + assignment.getInput())

    assignment.initialize(input)
    assignment.run()
}
