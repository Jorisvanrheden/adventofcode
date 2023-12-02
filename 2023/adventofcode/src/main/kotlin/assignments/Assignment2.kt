package assignments

class Assignment2 : Assignment() {
    data class Game(val id: Int, val gameSets: List<GameSet>)
    data class GameSet(val cubes: List<GameCube>)
    data class GameCube(val count: Int, val color: String)

    private lateinit var games: List<Game>

    override fun getInput(): String {
        return "input_2"
    }

    override fun initialize(input: List<String>) {
        games = input.map { line ->
            Game(
                line.split(':')[0].split(' ')[1].toInt(),
                line.split(':')[1].toGameSets(),
            )
        }
    }

    private fun String.toGameSets() =
        // 3 blue, 4 red; 1 red, 2 green, 6 blue; 2 green
        split(';').map { section ->
            GameSet(
                section.split(',').map {
                    it.toGameCube()
                },
            )
        }

    private fun String.toGameCube() =
        // 3 blue
        trim().split(' ').let {
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
        gameSets
            .flatMap { it.cubes }
            .filter { it.color == color }
            .maxOf { it.count }
}
