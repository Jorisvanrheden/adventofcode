package toolkit

class Matrix(val rows: Int, val columns: Int) {
    var values: Array<Array<Int>> = Array(rows) {
        Array(columns) {
            0
        }
    }
}
