package assignments

class Assignment1 : Assignment() {
    private lateinit var expenseReport: List<Int>

    override fun getInput(): String {
        return "input_1"
    }

    override fun initialize(input: List<String>) {
        expenseReport = input.map { it.toInt() }
    }

    override fun calculateSolutionA(): String {
        for (x in expenseReport) {
            for (y in expenseReport) {
                if (x + y == 2020) {
                    return (x * y).toString()
                }
            }
        }
        return "answer not found"
    }

    override fun calculateSolutionB(): String {
        for (x in expenseReport) {
            for (y in expenseReport) {
                for (z in expenseReport) {
                    if (x + y + z == 2020) {
                        return (x * y * z).toString()
                    }
                }
            }
        }
        return "answer not found"
    }
}
