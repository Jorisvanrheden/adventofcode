package assignments

class Assignment19 : Assignment() {
    override fun getInput(): String {
        return "input_19"
    }

    data class BluePrint(
        val oreRobotOreCost: Int,
        val clayRobotOreCost: Int,
        val obsidianRobotOreCost: Int,
        val obsidianRobotClayCost: Int,
        val geodeRobotOreCost: Int,
        val geodeRobotObsidianCost: Int
    )

    data class Inventory(
        var ore: Int,
        var clay: Int,
        var obsidian: Int,
        var geodes: Int,
        var oreRobots: Int,
        var clayRobots: Int,
        var obsidianRobots: Int,
        var geodeRobots: Int
    ) {
        fun processTurn() {
            ore += oreRobots
            clay += clayRobots
            obsidian += obsidianRobots
            geodes += geodeRobots
        }
    }

    private lateinit var bluePrints: List<BluePrint>

    private fun parseBluePrint(input: String): BluePrint {
        return input.split(' ')
            .let {
                BluePrint(
                    it[6].toInt(),
                    it[12].toInt(),
                    it[18].toInt(),
                    it[21].toInt(),
                    it[27].toInt(),
                    it[30].toInt()
                )
            }
    }

    override fun initialize(input: List<String>) {
        bluePrints = input.map { parseBluePrint(it) }
    }

    private fun canAffordOreRobot(inventory: Inventory, bluePrint: BluePrint) =
        inventory.ore >= bluePrint.oreRobotOreCost

    private fun canAffordClayRobot(inventory: Inventory, bluePrint: BluePrint) =
        inventory.ore >= bluePrint.clayRobotOreCost

    private fun canAffordObsidianRobot(inventory: Inventory, bluePrint: BluePrint) =
        inventory.ore >= bluePrint.obsidianRobotOreCost && inventory.clay >= bluePrint.obsidianRobotClayCost

    private fun canAffordGeodeRobot(inventory: Inventory, bluePrint: BluePrint) =
        inventory.ore >= bluePrint.geodeRobotOreCost && inventory.obsidian >= bluePrint.geodeRobotObsidianCost

    private fun toKey(inventory: Inventory, turn: Int): String {
        var key = ""
        key += inventory.ore.toString()
        key += "-"
        key += inventory.clay.toString()
        key += "-"
        key += inventory.obsidian.toString()
        key += "-"
        key += inventory.geodes.toString()
        key += "-"
        key += inventory.oreRobots.toString()
        key += "-"
        key += inventory.clayRobots.toString()
        key += "-"
        key += turn.toString()

        return key
    }

    private fun simulate(inventory: Inventory, bluePrint: BluePrint, turn: Int, map: MutableMap<String, Int>): Int {
        // terminate when the time limit is reached
        if (turn == 24) {
            return inventory.geodes
        }

        val key = toKey(inventory, turn)
        if (map.containsKey(key)) {
            return map[key]!!
        }

        // spending is done in the beginning
        // the bought robot can only start working in the next round

        // keep track of the best performing combination
        var purchasesPossible = MutableList(4) { false }
        // purchases are recorded as [ore, clay, obsidian]
        var purchases = MutableList(4) { listOf(0, 0, 0) }

        // find out what can be done in this round (spending)
        if (canAffordOreRobot(inventory, bluePrint)) {
            purchases[0] = listOf(bluePrint.oreRobotOreCost, 0, 0)
            purchasesPossible[0] = true
        }
        if (canAffordClayRobot(inventory, bluePrint)) {
            purchases[1] = listOf(bluePrint.clayRobotOreCost, 0, 0)
            purchasesPossible[1] = true
        }
        if (canAffordObsidianRobot(inventory, bluePrint)) {
            purchases[2] = listOf(bluePrint.obsidianRobotOreCost, bluePrint.obsidianRobotClayCost, 0)
            purchasesPossible[2] = true
        }
        if (canAffordGeodeRobot(inventory, bluePrint)) {
            purchases[3] = listOf(bluePrint.geodeRobotOreCost, 0, bluePrint.geodeRobotObsidianCost)
            purchasesPossible[3] = true
        }

        // possible purchases have been determined, now we can apply the robot effects
        inventory.processTurn()

        var outcomes = mutableListOf<Int>()

        // purchase mutations
        for (i in purchases.indices) {
            if (!purchasesPossible[i]) continue

            var copy = inventory.copy()

            copy.ore -= purchases[i][0]
            copy.clay -= purchases[i][1]
            copy.obsidian -= purchases[i][2]

            if (i == 0) copy.oreRobots++
            if (i == 1) copy.clayRobots++
            if (i == 2) copy.obsidianRobots++
            if (i == 3) copy.geodeRobots++

            outcomes.add(
                simulate(copy, bluePrint, turn + 1, map)
            )
        }

        // also provide the possibility to not buy anything
        outcomes.add(
            simulate(inventory, bluePrint, turn + 1, map)
        )

        val maxValue = outcomes.maxOrNull()!!
        map[key] = maxValue

        return maxValue
    }

    override fun calculateSolutionA(): String {
        val outcomes = bluePrints.mapIndexed { index, it ->
            println("$index/${bluePrints.size}")
            var inventory = Inventory(0, 0, 0, 0, 1, 0, 0, 0)
            simulate(inventory, it, 0, mutableMapOf())
        }

        return outcomes.mapIndexed { index, it ->
            (index + 1) * it
        }
            .sum()
            .toString()
    }

    override fun calculateSolutionB(): String {
        return ""
    }
}
