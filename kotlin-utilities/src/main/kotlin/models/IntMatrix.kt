package models

class IntMatrix(rows: Int, columns: Int) : Matrix<Int>(rows, columns, 0) {
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
