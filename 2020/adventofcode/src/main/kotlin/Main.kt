import assignments.*
import utilities.Utilities

fun main(args: Array<String>) {

    val assignment:Assignment = Assignment3()

    val input:List<String> = Utilities.readFile("src/main/resources/" + assignment.getInput())

    assignment.initialize(input)
    assignment.run()
}