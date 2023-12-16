package assignments

class Assignment15 : Assignment() {

    override fun getInput(): String {
        return "input_15"
    }

    private lateinit var sequence: List<String>

    override fun initialize(input: List<String>) {
        sequence = input.first().split(',')
    }

    override fun calculateSolutionA(): String {
        return sequence.sumOf {
            var score = 0
            it.toCharArray().forEach { c ->
                score = c.toScore(score)
            }
            score
        }.toString()
    }

    override fun calculateSolutionB(): String {
        return ""
    }

    private fun Char.toScore(startValue: Int): Int {
        var newValue = startValue + code
        newValue *= 17
        newValue %= 256
        return newValue
    }
}
