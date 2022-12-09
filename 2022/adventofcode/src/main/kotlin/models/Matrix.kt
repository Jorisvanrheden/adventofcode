package toolkit

class Matrix(val rows: Int, val columns: Int) {
    var values: Array<Array<Int>> = Array(rows) {
        Array(columns) {
            0
        }
    }

    fun foreach(delegate: (Matrix, Int, Int) -> Int): List<List<Int>> {
        return values.mapIndexed { i, row ->
            row.mapIndexed { j, _ ->
                delegate(this, i, j)
            }
        }
    }
}
