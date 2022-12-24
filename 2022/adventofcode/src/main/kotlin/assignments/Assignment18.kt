package assignments

import toolkit.Vector3D
import kotlin.math.abs

class Assignment18 : Assignment() {

    override fun getInput(): String {
        return "input_18"
    }

    lateinit var cubes: List<Vector3D>

    override fun initialize(input: List<String>) {
        cubes = input.map { line ->
            line
                .split(',')
                .map { it.toInt() }
                .let { entry ->
                    Vector3D(entry[0], entry[1], entry[2])
                }
        }
    }

    private fun Vector3D.isNextTo(vector3D: Vector3D): Boolean {
        val diff = this - vector3D
        val distances = listOf(diff.x, diff.y, diff.z).map { abs(it) }
        return distances.count { it == 1 } == 1 &&
                distances.count { it == 0 } == 2
    }

    private fun Vector3D.getConnectedSidesWith(collection: List<Vector3D>): Int {
        return collection.count {
            this.isNextTo(it)
        }
    }

    override fun calculateSolutionA(): String {
        var total = 0
        for (cube in cubes) {
            var freeSides = 6 - cube.getConnectedSidesWith(cubes)
            total += freeSides
        }
        return total.toString()
    }

    override fun calculateSolutionB(): String {
        return ""
    }
}
