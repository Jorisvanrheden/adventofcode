package models

import toolkit.Vector2D

class MatrixChar(val rows: Int, val columns: Int) {
    var values: Array<Array<Char>> = Array(rows) {
        Array(columns) { '.'

        }
    }

    fun isWithinBounds(vector2D: Vector2D): Boolean {
        if (vector2D.x < 0 || vector2D.x >= rows) return false
        if (vector2D.y < 0 || vector2D.y >= columns) return false
        return true
    }

    fun forEach(predicate: (Int, Int) -> Unit) {
        for (i in 0 until rows) {
            for (j in 0 until columns) {
                predicate(i, j)
            }
        }
    }

    fun find(predicate: (Char) -> Boolean): Char {
        for (i in 0 until rows) {
            for (j in 0 until columns) {
                val value = values[i][j]
                if (value != null && predicate(value)) {
                    return value
                }
            }
        }
        throw Exception("Element with given predicate is not in the collection")
    }

    override fun toString(): String {
        var output = "\n"
        for (i in 0 until rows) {
            for (j in 0 until columns) {
                output += values[i][j]
            }
            output += "\n"
        }
        return output
    }
}
