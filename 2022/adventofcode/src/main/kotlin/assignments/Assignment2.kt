package assignments

class Assignment2 : Assignment() {
    data class Match(val playerValue: Int, val enemyValue: Int) {
        private fun getScore(valueA: Int, valueB: Int): Int {
            // Rock (0) -> 1 point
            // Paper (1) -> 2 points
            // Scissors (2) -> 3 points
            val choiceScore = valueA + 1

            // draw
            if (valueA == valueB) return choiceScore + 3

            // lost
            if (valueA == 0 && valueB == 1) return choiceScore + 0
            if (valueA == 1 && valueB == 2) return choiceScore + 0
            if (valueA == 2 && valueB == 0) return choiceScore + 0

            // win
            return choiceScore + 6
        }

        fun getDefaultScore(): Int {
            return getScore(playerValue, enemyValue)
        }

        fun getAlternativeScore(): Int {
            var alternativePlayerValue = playerValue

            // need a draw
            if (playerValue == 1) alternativePlayerValue = enemyValue

            // need a lose
            else if (playerValue == 0) {
                alternativePlayerValue = enemyValue - 1
                if (alternativePlayerValue == -1) alternativePlayerValue = 2
            }

            // need a win
            else if (playerValue == 2) {
                alternativePlayerValue = enemyValue + 1
                if (alternativePlayerValue == 3) alternativePlayerValue = 0
            }

            return getScore(alternativePlayerValue, enemyValue)
        }
    }

    private lateinit var matches: List<Match>

    override fun getInput(): String {
        return "input_2"
    }

    override fun initialize(input: List<String>) {
        val enemyValues = listOf("A", "B", "C")
        val ownValues = listOf("X", "Y", "Z")

        matches = input.map {
            val chunks = it.split(' ')
            Match(
                ownValues.indexOf(chunks[1]),
                enemyValues.indexOf(chunks[0])
            )
        }
    }

    override fun calculateSolutionA(): String {
        return matches.sumOf {
            it.getDefaultScore()
        }.toString()
    }

    override fun calculateSolutionB(): String {
        return matches.sumOf {
            it.getAlternativeScore()
        }.toString()
    }
}
