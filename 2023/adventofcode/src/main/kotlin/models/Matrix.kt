package models

import toolkit.Vector2D

data class Node(var value: String, var neighbors: List<Vector2D> = emptyList())

class Matrix(val rows: Int, val columns: Int) {

    var values: Array<Array<Node>> = Array(rows) {
        Array(columns) {
            Node(".", listOf())
        }
    }

    fun isWithinBounds(vector2D: Vector2D): Boolean {
        if (vector2D.x < 0 || vector2D.x >= rows) return false
        if (vector2D.y < 0 || vector2D.y >= columns) return false
        return true
    }

    fun find(predicate: (Node) -> Boolean): Node {
        for (i in 0 until rows) {
            for (j in 0 until columns) {
                if (predicate(values[i][j])) {
                    return values[i][j]
                }
            }
        }
        throw Exception("Element with given predicate is not in the collection")
    }

    override fun toString(): String {
        var output = "\n"
        for (i in 0 until rows) {
            output += "|"
            for (j in 0 until columns) {
                output += values[i][j]
            }
            output += "|\n"
        }
        return output
    }

    fun toString(vector2D: Vector2D): String {
        var output = "\n"
        for (i in 0 until rows) {
            output += "|"
            for (j in 0 until columns) {
                if (vector2D.x == i && vector2D.y == j) {
                    output += "S"
                } else {
                    output += values[i][j]
                }
            }
            output += "|\n"
        }
        return output
    }
}
