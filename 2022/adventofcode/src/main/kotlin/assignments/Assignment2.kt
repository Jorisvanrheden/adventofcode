package assignments

class Assignment2 : Assignment() {

    data class Match(var a: Int, var b: Int)

    fun Match.getScore(own: Int, enemy: Int): Int {
        // Rock (1)
        // Paper (2)
        // Scissors (3)

        // draw
        if (own == enemy) return own + 3

        // lost
        if (own == 1 && enemy == 2) return own + 0
        if (own == 2 && enemy == 3) return own + 0
        if (own == 3 && enemy == 1) return own + 0

        return own + 6
    }

    fun Match.getDefaultScore(): Int {
        return getScore(a, b)
    }

    fun Match.getAlternativeScore(): Int {
        // need a draw
        if (a == 2) a = b
        // need a lose
        else if (a == 1) {
            a = b - 1
            if (a == 0) a = 3
        }
        // need a win
        else if (a == 3) {
            a = b + 1
            if (a == 4) a = 1
        }

        return getScore(a, b)
    }

    var matches = mutableListOf<Match>()

    override fun getInput(): String {
        return "input_2"
    }

    override fun initialize(input: List<String>) {
        val enemyValues = listOf("A", "B", "C")
        val ownValues = listOf("X", "Y", "Z")

        for (entry in input) {
            val chunks = entry.split(' ')

            val enemyValue = chunks[0]
            val ownValue = chunks[1]

            val ownIndex = ownValues.indexOf(ownValue) + 1
            val enemyIndex = enemyValues.indexOf(enemyValue) + 1
            matches.add(Match(ownIndex, enemyIndex))
        }
    }

    override fun calculateSolutionA(): String {
        return matches.sumOf { it.getDefaultScore() }.toString()
    }

    override fun calculateSolutionB(): String {
        return matches.sumOf { it.getAlternativeScore() }.toString()
    }
}
