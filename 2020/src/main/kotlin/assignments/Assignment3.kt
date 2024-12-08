package assignments

import models.assignment.Assignment
import models.matrix.IntMatrix
import models.vector.Vector2D

class Assignment3 : Assignment(3)
{
    private lateinit var matrix: IntMatrix

    override fun initialize(input: List<String>) {
        matrix = IntMatrix(input.size, input[0].count())

        input.forEachIndexed{ row, line ->
            for(column in 0 until line.count()){

                var value = 0

                when (line[column]){
                    '.' -> value = 0
                    '#' -> value = 1
                }

                matrix.values[row][column] = value
            }
        }
    }

    private fun getEncountersForSlope(slope: Vector2D):Int{
        var position = Vector2D(0,0)
        var encounters = 0

        while(position.y < matrix.rows){
            if(matrix.values[position.y][position.x] == 1) encounters++

            position += slope

            position.x %= matrix.columns
        }
        return encounters
    }

    override fun calculateSolutionA(): String {
        val encounters:Int = getEncountersForSlope(Vector2D(3, 1))

        return encounters.toString()
    }

    override fun calculateSolutionB(): String {
        val a:Int = getEncountersForSlope(Vector2D(1, 1))
        val b:Int = getEncountersForSlope(Vector2D(3, 1))
        val c:Int = getEncountersForSlope(Vector2D(5, 1))
        val d:Int = getEncountersForSlope(Vector2D(7, 1))
        val e:Int = getEncountersForSlope(Vector2D(1, 2))

        return (a*b*c*d*e).toString()
    }
}