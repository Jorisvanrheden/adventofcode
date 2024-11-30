package models

import toolkit.Vector2D

class NodeMatrix(rows: Int, columns: Int) : Matrix<Node>(rows, columns, Node(Vector2D(0, 0), ".", emptyList())) {
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

    fun copy(): NodeMatrix {
        val copy = NodeMatrix(rows, columns)
        for (i in 0 until rows) {
            for (j in 0 until columns) {
                copy.values[i][j] = values[i][j].copy()
            }
        }
        return copy
    }
}
