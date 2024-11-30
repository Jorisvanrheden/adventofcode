package assignments

class Assignment2 : Assignment() {
    data class Game(val id: Int, val cubes: List<GameCube>)
    data class GameCube(val count: Int, val color: String)

    private lateinit var games: List<Game>

    override fun getInput(): String {
        return "input_2"
    }

    override fun initialize(input: List<String>) {
        games = input.map { line ->
            line
                .split(':')
                .let {
                    Game(
                        it[0].split(' ')[1].toInt(),
                        it[1].toGameCubes(),
                    )
                }
        }
    }

    private fun String.toGameCubes() =
        // 3 blue, 4 red; 1 red, 2 green, 6 blue; 2 green
        split(';')
            .flatMap { section ->
                section
                    .split(',')
                    .map { it.toGameCube() }
            }

    private fun String.toGameCube() =
        // 3 blue
        trim()
            .split(' ')
            .let {
                GameCube(it[0].toInt(), it[1])
            }

    override fun calculateSolutionA() =
        games.filter {
            it.countMostCubesWithColor("red") <= 12 &&
            it.countMostCubesWithColor("green") <= 13 &&
            it.countMostCubesWithColor("blue") <= 14
        }.sumOf { it.id }.toString()

    override fun calculateSolutionB() =
        games.map {
            it.countMostCubesWithColor("red") *
            it.countMostCubesWithColor("green") *
            it.countMostCubesWithColor("blue")
        }.sumOf { it }.toString()

    private fun Game.countMostCubesWithColor(color: String) =
        cubes
            .filter { it.color == color }
            .maxOf { it.count }
}
