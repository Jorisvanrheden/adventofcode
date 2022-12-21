package toolkit

class Matrix(val rows: Int, val columns: Int) {

    var values: Array<Array<Int>> = Array(rows) {
        Array(columns) {
            0
        }
    }

    fun isWithinBounds(vector2D: Vector2D): Boolean {
        if (vector2D.x < 0 || vector2D.x >= columns) return false
        if (vector2D.y < 0 || vector2D.y >= rows) return false
        return true
    }

    override fun toString(): String {
        var output = "\n"
        for (i in 0 until rows) {
            output += "|"
            for (j in 0 until columns) {
                if (values[i][j] == 0) output += " "
                else output += "#"
            }
            output += "|\n"
        }
        return output
    }

    fun toString2(): String {
        var output = "\n"
        for (j in 0 until columns) {
            for (i in 0 until rows) {
                if (values[i][j] == 0) output += " "
                else output += "#"
            }
            output += "\n"
        }
        return output
    }
}
