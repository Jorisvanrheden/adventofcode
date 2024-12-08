package assignments

import models.assignment.Assignment

class Assignment19 : Assignment(19) {
    data class BluePrint(
        val oreRobotOreCost: Int,
        val clayRobotOreCost: Int,
        val obsidianRobotOreCost: Int,
        val obsidianRobotClayCost: Int,
        val geodeRobotOreCost: Int,
        val geodeRobotObsidianCost: Int
    ) {
        fun maxOreCost(): Int {
            var cost = oreRobotOreCost
            if (clayRobotOreCost > cost)cost = clayRobotOreCost
            if (obsidianRobotOreCost > cost)cost = obsidianRobotOreCost
            if (geodeRobotOreCost > cost)cost = geodeRobotOreCost
            return cost
        }
        fun maxClayCost(): Int {
            return obsidianRobotClayCost
        }
        fun maxObsidianCost(): Int {
            return geodeRobotObsidianCost
        }
    }

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
    private var globalMax = 0

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

    private fun Inventory.requiresMoreOreRobots(bluePrint: BluePrint) =
        oreRobots < bluePrint.maxOreCost()

    private fun Inventory.requiresMoreClayRobots(bluePrint: BluePrint) =
        clayRobots < bluePrint.maxClayCost()

    private fun Inventory.requiresMoreObsidianRobots(bluePrint: BluePrint) =
        obsidianRobots < bluePrint.maxObsidianCost()

    private fun Inventory.canAffordOreRobot(bluePrint: BluePrint) =
        ore >= bluePrint.oreRobotOreCost

    private fun Inventory.canAffordClayRobot(bluePrint: BluePrint) =
        ore >= bluePrint.clayRobotOreCost

    private fun Inventory.canAffordObsidianRobot(bluePrint: BluePrint) =
        ore >= bluePrint.obsidianRobotOreCost && clay >= bluePrint.obsidianRobotClayCost

    private fun Inventory.canAffordGeodeRobot(bluePrint: BluePrint) =
        ore >= bluePrint.geodeRobotOreCost && obsidian >= bluePrint.geodeRobotObsidianCost

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

    private fun maxGeodesToGet(inventory: Inventory, bluePrint: BluePrint, turn: Int, maxTurns: Int): Int {
        val turnsRemaining = maxTurns - turn + 2

        // this is honestly a pretty questionable extra filter, and could be a lot more refined
        // however, it does filter out quite some extra options, so probably not that bad after all
        var geodes = inventory.geodes
        var geodeRobots = inventory.geodeRobots

        for (i in 0..turnsRemaining) {
            geodes += geodeRobots
            geodeRobots++
        }
        return geodes
    }

    private fun simulate(inventory: Inventory, bluePrint: BluePrint, turn: Int, map: MutableMap<String, Int>, maxTurns: Int): Int {
        // terminate when the time limit is reached
        if (turn == maxTurns) {
            return inventory.geodes
        }

        if (map.size > 10000000) {
            map.clear()
        }

        val key = toKey(inventory, turn)
        if (map.containsKey(key)) {
            return map[key]!!
        }

        // check if it is even possible for this permutation to still achieve a higher max
        if (inventory.geodes + maxGeodesToGet(inventory, bluePrint, turn, maxTurns) < globalMax) {
            return inventory.geodes
        }

        // keep track of the best performing combination
        var purchasesPossible = MutableList(4) { false }
        // purchases are recorded as [ore, clay, obsidian]
        var purchases = MutableList(4) { listOf(0, 0, 0) }

        // find out what can be done in this round (spending)
        if (inventory.requiresMoreOreRobots(bluePrint) && inventory.canAffordOreRobot(bluePrint)) {
            purchases[0] = listOf(bluePrint.oreRobotOreCost, 0, 0)
            purchasesPossible[0] = true
        }
        if (inventory.requiresMoreClayRobots(bluePrint) && inventory.canAffordClayRobot(bluePrint)) {
            purchases[1] = listOf(bluePrint.clayRobotOreCost, 0, 0)
            purchasesPossible[1] = true
        }
        if (inventory.requiresMoreObsidianRobots(bluePrint) && inventory.canAffordObsidianRobot(bluePrint)) {
            purchases[2] = listOf(bluePrint.obsidianRobotOreCost, bluePrint.obsidianRobotClayCost, 0)
            purchasesPossible[2] = true
        }
        if (inventory.canAffordGeodeRobot(bluePrint)) {
            purchases[3] = listOf(bluePrint.geodeRobotOreCost, 0, bluePrint.geodeRobotObsidianCost)
            purchasesPossible[3] = true
        }

        // possible purchases have been determined, now we can apply the robot effects
        inventory.processTurn()

        var outcomes = mutableListOf<Int>()

        // purchase mutations
        for (i in purchases.indices) {
            if (!purchasesPossible[i]) continue

            var updatedInventory = inventory.copy()

            updatedInventory.ore -= purchases[i][0]
            updatedInventory.clay -= purchases[i][1]
            updatedInventory.obsidian -= purchases[i][2]

            if (i == 0) updatedInventory.oreRobots++
            if (i == 1) updatedInventory.clayRobots++
            if (i == 2) updatedInventory.obsidianRobots++
            if (i == 3) updatedInventory.geodeRobots++

            outcomes.add(
                simulate(updatedInventory, bluePrint, turn + 1, map, maxTurns)
            )
        }

        // also provide the possibility to not buy anything
        outcomes.add(
            simulate(inventory, bluePrint, turn + 1, map, maxTurns)
        )

        return outcomes.maxOrNull()!!.also {
            map[key] = it
        }
    }

    override fun calculateSolutionA(): String {
        val outcomes = bluePrints.mapIndexed { index, it ->
            println("$index/${bluePrints.size}")
            var inventory = Inventory(0, 0, 0, 0, 1, 0, 0, 0)
            simulate(inventory, it, 0, mutableMapOf(), 24)
        }

        return outcomes.mapIndexed { index, it ->
            (index + 1) * it
        }
            .sum()
            .toString()
    }

    override fun calculateSolutionB(): String {
        val outcomes = bluePrints
            .take(3)
            .mapIndexed { index, it ->
                globalMax = 0
                println("$index/3")
                var inventory = Inventory(0, 0, 0, 0, 1, 0, 0, 0)
                simulate(inventory, it, 0, mutableMapOf(), 32)
            }

        return outcomes.reduce { a, b ->
            a * b
        }
            .toString()
    }
}
