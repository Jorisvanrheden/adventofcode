package assignments

import models.assignment.Assignment
import models.vector.Vector3D
import kotlin.math.abs

class Assignment18 : Assignment() {

    data class Bounds(
        val xMin: Int,
        val xMax: Int,
        val yMin: Int,
        val yMax: Int,
        val zMin: Int,
        val zMax: Int
    )

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
        val distances = listOf(diff.x, diff.y, diff.z)
            .map { abs(it) }
        return distances.count { it == 1 } == 1 && distances.count { it == 0 } == 2
    }

    private fun Vector3D.getConnectedSidesWith(collection: List<Vector3D>): Int {
        return collection.count {
            this.isNextTo(it)
        }
    }

    private fun getNeighbors(currentNode: Vector3D, internalNodes: List<Vector3D>): List<Vector3D> {
        return internalNodes.filter {
            currentNode.isNextTo(it)
        }
    }

    data class Area(val area: Set<Vector3D>, val isInternal: Boolean)

    private fun isInInternalArea(currentNode: Vector3D, availableNodes: List<Vector3D>, bounds: Bounds, visitedNodes: MutableSet<Vector3D>): Area {
        // check if the current node is on the edge of a boundary dimension,
        // then the outside has been reached
        if (currentNode.x == bounds.xMin || currentNode.x == bounds.xMax) return Area(emptySet(), false)
        if (currentNode.y == bounds.yMin || currentNode.y == bounds.yMax) return Area(emptySet(), false)
        if (currentNode.z == bounds.zMin || currentNode.z == bounds.zMax) return Area(emptySet(), false)

        // add the current node to the visited nodes
        visitedNodes.add(currentNode)

        val neighbors = getNeighbors(currentNode, availableNodes).filter { !visitedNodes.contains(it) }
        for (neighbor in neighbors) {
            val isNeighborInternal = isInInternalArea(neighbor, availableNodes, bounds, visitedNodes)
            if (!isNeighborInternal.isInternal) return Area(emptySet(), false)
        }

        return Area(visitedNodes, true)
    }

    private fun getBounds(cubes: List<Vector3D>): Bounds {
        return Bounds(
            cubes.minOf { it.x },
            cubes.maxOf { it.x },
            cubes.minOf { it.y },
            cubes.maxOf { it.y },
            cubes.minOf { it.z },
            cubes.maxOf { it.z }
        )
    }

    private fun getTrappedCubes(bounds: Bounds, cubes: List<Vector3D>): List<Vector3D> {
        var internalCubes = mutableListOf<Vector3D>()
        for (i in bounds.xMin..bounds.xMax) {
            for (j in bounds.yMin..bounds.yMax) {
                for (k in bounds.zMin..bounds.zMax) {
                    val position = Vector3D(i, j, k)
                    if (!cubes.contains(position)) {
                        internalCubes.add(position)
                    }
                }
            }
        }

        // for each of the 'internal' cubes, check if they are connected to the outside layer
        // check if the current position is trapped, by continuously checking neighbors
        // until you find a neighbor that is exposed to the outside
        // if you can't find such a neighbor, the cube is trapped
        var areas = mutableListOf<Area>()
        var d = 0
        return internalCubes.filter {
            d++
            println("$d/${internalCubes.size}")

            for (area in areas) {
                if (area.area.contains(it)) {
                    area.isInternal
                }
            }

            // first check if any area already contains the cube
            val result = isInInternalArea(it, internalCubes, bounds, mutableSetOf())
            if (result.isInternal) areas.add(result)

            result.isInternal
        }
    }

    override fun calculateSolutionA(): String {
        return cubes
            .sumOf {
                val d = 6 - it.getConnectedSidesWith(cubes)
                d
            }
            .toString()
    }

    override fun calculateSolutionB(): String {
        // create bounds, and find all 'internal' trapped cubes
        val bounds = getBounds(cubes)

        // then find cubes that are 'trapped' within the other cubes
        val trappedCubes = getTrappedCubes(bounds, cubes)

        val combined = cubes.toMutableList().apply { addAll(trappedCubes) }

        // use the original answer
        return cubes
            .sumOf {
                6 - it.getConnectedSidesWith(combined)
            }.toString()
    }
}
