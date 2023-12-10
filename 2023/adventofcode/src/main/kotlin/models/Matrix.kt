package models

import toolkit.Vector2D

data class Node(
    var coordinate: Vector2D,
    var value: String,
    var neighbors: List<Vector2D> = emptyList(),
)

class Matrix(val rows: Int, val columns: Int) {
    var values: Array<Array<Node?>> = Array(rows) {
        Array(columns) {
            Node(Vector2D(0, 0), ".", listOf())
        }
    }

    fun copy(): Matrix {
        val copy = Matrix(rows, columns)
        for (i in 0 until rows) {
            for (j in 0 until columns) {
                copy.values[i][j] = values[i][j]?.copy()
            }
        }
        return copy
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

    fun filterNotNull(): List<Node> {
        val items = mutableListOf<Node>()
        for (i in 0 until rows) {
            for (j in 0 until columns) {
                val value = values[i][j]
                if (value != null) {
                    items.add(value)
                }
            }
        }
        return items.toList()
    }

    fun find(predicate: (Node) -> Boolean): Node {
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
                output += values[i][j]?.value
            }
            output += "\n"
        }
        return output
    }
}
