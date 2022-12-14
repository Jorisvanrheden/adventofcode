package assignments

import toolkit.Matrix
import toolkit.Vector2D
import java.lang.Math.max
import java.lang.Math.min

class Assignment14 : Assignment() {

    data class Bounds(val xMin: Int, val xMax: Int, val yMin: Int, val yMax: Int)
    data class Line(val start: Vector2D, val end: Vector2D) {
        fun getCoordinatesInBetween(): List<Vector2D> {
            var coordinates = mutableListOf<Vector2D>()
            val xRange = end.x - start.x
            val yRange = end.y - start.y

            if (xRange != 0) {
                // iterate over x, make sure that the coordinate with lowest x is left
                val xMin = min(start.x, end.x)
                val xMax = max(start.x, end.x)
                for (i in xMin..xMax) {
                    coordinates.add(Vector2D(i, start.y))
                }
            } else if (yRange != 0) {
                // iterate over x, make sure that the coordinate with lowest x is left
                val yMin = min(start.y, end.y)
                val yMax = max(start.y, end.y)
                for (j in yMin..yMax) {
                    coordinates.add(Vector2D(start.x, j))
                }
            }
            return coordinates
        }
    }
    data class Section(val lines: List<Line>)

    override fun getInput(): String {
        return "input_14"
    }

    private lateinit var sections: List<Section>

    private fun String.toVector2D(): Vector2D {
        val parts = this.split(',')
        return Vector2D(parts[0].toInt(), parts[1].toInt())
    }

    override fun initialize(input: List<String>) {
        sections = input.map {
            val chunks = it.split(" -> ")
            var lines = mutableListOf<Line>()
            for (i in 0 until chunks.size - 1) {
                val vectorA = chunks[i].toVector2D()
                val vectorB = chunks[i + 1].toVector2D()
                lines.add(Line(vectorA, vectorB))
            }
            Section(lines)
        }
    }

    private fun getBounds(sections: List<Section>, addition: Vector2D): Bounds {
        // find boundaries
        var xValues = mutableListOf<Int>()
        xValues.addAll(sections.map { section -> section.lines.map { it.start.x } }.flatten())
        xValues.addAll(sections.map { section -> section.lines.map { it.end.x } }.flatten())
        xValues.add(addition.x)

        val yValues = mutableListOf<Int>()
        yValues.addAll(sections.map { section -> section.lines.map { it.start.y } }.flatten())
        yValues.addAll(sections.map { section -> section.lines.map { it.end.y } }.flatten())
        yValues.add(addition.y)

        return Bounds(
            xValues.minOf { it },
            xValues.maxOf { it },
            yValues.minOf { it },
            yValues.maxOf { it }
        )
    }

    private fun Matrix.processSection(bounds: Bounds, section: Section) {
        for (line in section.lines) {
            val coordinates = line.getCoordinatesInBetween()
            for (coordinate in coordinates) {
                values[coordinate.x - bounds.xMin][coordinate.y - bounds.yMin] = 1
            }
        }
    }

    private fun isInBounds(bounds: Bounds, vector2D: Vector2D): Boolean {
        if (vector2D.x < 0 || vector2D.x > bounds.xMax - bounds.xMin) return false
        if (vector2D.y < 0 || vector2D.y > bounds.yMax - bounds.yMin) return false
        return true
    }

    private fun findSandTarget(bounds: Bounds, matrix: Matrix, sandSource: Vector2D): Vector2D {
        var sandPosition = sandSource - Vector2D(bounds.xMin, bounds.yMin)
        while (true) {
            var nextPosition = sandPosition + Vector2D(0, 1)

            // boundary check
            if (!isInBounds(bounds, sandPosition)) break

            // check if it can move down
            if (matrix.values[nextPosition.x][nextPosition.y] == 0) {
                sandPosition = nextPosition
                continue
            }

            // else check if can move left down
            val leftPosition = nextPosition + Vector2D(-1, 0)
            if (!isInBounds(bounds, leftPosition)) break
            if (matrix.values[leftPosition.x][leftPosition.y] == 0) {
                sandPosition = leftPosition
                continue
            }

            // else check if can move right down
            val rightPosition = nextPosition + Vector2D(1, 0)
            if (!isInBounds(bounds, rightPosition)) break
            if (matrix.values[rightPosition.x][rightPosition.y] == 0) {
                sandPosition = rightPosition
                continue
            }

            // else return the last position
            return sandPosition
        }
        return Vector2D(-1, -1)
    }

    private fun applySandUntilStop(bounds: Bounds, matrix: Matrix, sandSource: Vector2D, stopCoordinate: Vector2D): Int {
        var rounds = 0
        while (true) {
            // find first y location below
            val sandDestination = findSandTarget(bounds, matrix, sandSource)
            if (sandDestination.x == stopCoordinate.x && sandDestination.y == stopCoordinate.y) {
                break
            }

            rounds++

            // apply the sand destination to the matrix
            matrix.values[sandDestination.x][sandDestination.y] = 2
        }
        return rounds
    }

    override fun calculateSolutionA(): String {
        val sandSource = Vector2D(500, 0)

        // parse all lines
        val bounds = getBounds(sections, sandSource)

        // create matrix
        var matrix = Matrix(
            bounds.xMax - bounds.xMin + 1,
            bounds.yMax - bounds.yMin + 1
        )

        // apply sections to matrix
        for (section in sections) {
            matrix.processSection(bounds, section)
        }

        // apply sand
        val rounds = applySandUntilStop(bounds, matrix, sandSource, Vector2D(-1, -1))

        return rounds.toString()
    }

    override fun calculateSolutionB(): String {
        val sandSource = Vector2D(500, 0)

        var sectionsA = sections.toMutableList()

        // parse all lines
        var bounds = getBounds(sectionsA, sandSource)

        sectionsA.add(
            Section(
                listOf(
                    Line(Vector2D(bounds.xMin - 1000, bounds.yMax + 2), Vector2D(bounds.xMax + 1000, bounds.yMax + 2))
                )
            )
        )

        bounds = getBounds(sectionsA, sandSource)

        // create matrix
        var matrix = Matrix(
            bounds.xMax - bounds.xMin + 1,
            bounds.yMax - bounds.yMin + 1
        )

        // apply sections to matrix
        for (section in sectionsA) {
            matrix.processSection(bounds, section)
        }

        // apply sand
        val rounds = applySandUntilStop(
            bounds,
            matrix,
            Vector2D(500, 0),
            Vector2D(sandSource.x - bounds.xMin, sandSource.y - bounds.yMin)
        )

        return rounds.toString()
    }
}
