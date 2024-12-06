package models.matrix

class CharMatrix(rows: Int, columns: Int) : Matrix<Char>(rows, columns, '.') {
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

    fun copy(): CharMatrix {
        val copy = CharMatrix(rows, columns)
        for (i in 0 until rows) {
            for (j in 0 until columns) {
                copy.values[i][j] = values[i][j]
            }
        }
        return copy
    }
}
