package models.matrix

import models.vector.Vector2D

data class Node(
    var coordinate: Vector2D,
    var value: String,
    var neighbors: List<Vector2D> = emptyList(),
)

open class Matrix<T>(val rows: Int, val columns: Int, private val defaultValue: T) {
    var values: MutableList<MutableList<T>> = MutableList(rows) { _ ->
        MutableList(columns) { _ ->
            defaultValue
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

    fun filterNotNull(): List<T> {
        val items = mutableListOf<T>()
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

    fun find(predicate: (T) -> Boolean): T {
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

    fun flatten(): List<T> {
        val items = mutableListOf<T>()
        for (i in 0 until rows) {
            for (j in 0 until columns) {
                items.add(values[i][j])
            }
        }
        return items
    }

    fun flattenCoordinates(): List<Vector2D> {
        val items = mutableListOf<Vector2D>()
        for (i in 0 until rows) {
            for (j in 0 until columns) {
                items.add(Vector2D(i, j))
            }
        }
        return items
    }

    fun occurrencesOf(item: T): List<Vector2D> {
        val indicesThatMatchesPredicate = mutableListOf<Vector2D>()
        for (i in 0 until rows) {
            for (j in 0 until columns) {
                if (values[i][j] == item) {
                    indicesThatMatchesPredicate.add(Vector2D(i, j))
                }
            }
        }
        return indicesThatMatchesPredicate
    }

    fun neighbors(node: Vector2D) =
        listOf(
            node.up(),
            node.right(),
            node.down(),
            node.left(),
        ).filter { isWithinBounds(it) }
}
