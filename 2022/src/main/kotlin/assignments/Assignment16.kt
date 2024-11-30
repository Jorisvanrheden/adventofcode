package assignments

import toolkit.Matrix

class Assignment16 : Assignment() {

    data class Valve(val name: String, val flowRate: Int, val connections: List<Int>)

    override fun getInput(): String {
        return "input_16"
    }

    private lateinit var valves: List<Valve>

    private fun parseInputToValve(input: String, names: List<String>): Valve {
        val chunks = input.split(';')

        val valveInfo = chunks[0]
            .split(' ')

        val tunnelInfo = chunks[1]
            .replace(",", "")
            .replace("tunnels lead to valves ", "")
            .replace("tunnels lead to valve ", "")
            .replace("tunnel leads to valves ", "")
            .replace("tunnel leads to valve ", "")
            .trim()
            .split(' ')
            .map { names.indexOf(it) }

        return Valve(
            valveInfo[1],
            valveInfo[4].split('=').let { it[1].toInt() },
            tunnelInfo
        )
    }

    override fun initialize(input: List<String>) {
        // Valve AA has flow rate=0; tunnels lead to valves DD, II, BB
        val names = input.map { it.split(' ')[1] }
        valves = input.map { parseInputToValve(it, names) }
    }

    private fun getFlowRate(valveStatuses: MutableSet<Int>): Int {
        var total = 0
        for (i in valveStatuses) {
            total += valves[i].flowRate
        }
        return total
    }

    private fun simulateState(position: Int, turn: Int, total: Int, openSet: MutableSet<Int>): Int {
        // -- Beginning of the turn --

        // 1.) process the pressure that is being released
        var maxPossiblePressure = total + getFlowRate(openSet) * (30 - turn)

        // Loop through all open valves and validate usage
        for (index in usefulValves) {
            // no need to process current valve
            if (position == index) continue

            if (openSet.contains(index)) continue

            // no need to process a valve that's too far away
            // also add 1 for the time it takes to open the valve
            val requiredTime = distanceMatrix.values[position][index] + 1
            if (turn + requiredTime >= 30) {
                continue
            }

            // increment total pressure and label the valve as opened
            val newTotal = total + requiredTime * getFlowRate(openSet)

            openSet.add(index)

            val outcome = simulateState(index, turn + requiredTime, newTotal, openSet)
            if (outcome > maxPossiblePressure) {
                maxPossiblePressure = outcome
            }
            openSet.remove(index)
        }

        return maxPossiblePressure
    }

    private fun simulateState2(start: Int, position: Int, turn: Int, total: Int, useful: List<Int>, openSet: MutableSet<Int>, elephant: Boolean): Int {
        // -- Beginning of the turn --

        // 1.) process the pressure that is being released
        var max = total + getFlowRate(openSet) * (26 - turn)

        if (!elephant) {
            // get all useful valves, but remove the open ones
            var candidates = useful.filter { !openSet.contains(it) }
            max += simulateState2(start, start, 0, 0, candidates, mutableSetOf(), true)
        }

        for (index in useful) {
            if (openSet.contains(index)) {
                continue
            }

            // no need to process a valve that's too far away
            // also add 1 for the time it takes to open the valve
            val requiredTime = distanceMatrix.values[position][index] + 1
            if (turn + requiredTime >= 26) {
                continue
            }

            // increment total pressure and label the valve as opened
            val newTotal = total + requiredTime * getFlowRate(openSet)
            openSet.add(index)

            val outcome = simulateState2(start, index, turn + requiredTime, newTotal, useful, openSet, elephant)

            if (outcome > max) {
                max = outcome
            }
            openSet.remove(index)
        }
        if (max > h) {
            h = max
            println(h)
        }
        return max
    }

    private var h = 0

    private lateinit var distanceMatrix: Matrix
    private lateinit var usefulValves: List<Int>

    private fun getUsefulValves(valves: List<Valve>): List<Int> {
        var v = mutableListOf<Int>()
        for ((index, s) in valves.withIndex()) {
            if (s.flowRate > 0)v.add(index)
        }
        return v
    }

    private fun getDistanceMatrix(valves: List<Valve>): Matrix {
        val matrix = Matrix(valves.size, valves.size)

        for (i in valves.indices) {
            for (j in valves.indices) {
                // identical nodes have a distance 0
                if (i == j) {
                    matrix.values[i][j] = 0
                } else {
                    // initialize with max distance
                    matrix.values[i][j] = valves.size
                }
            }
        }

        for (i in valves.indices) {
            for (j in valves[i].connections.indices) {
                val x = i
                val y = valves[i].connections[j]
                // set distance to all connected nodes
                matrix.values[x][y] = 1
            }
        }

        for (q in 0..valves.size) {
            for (i in valves.indices) {
                for (j in valves.indices) {
                    for (k in valves.indices) {
                        if (matrix.values[i][j] > matrix.values[i][k] + matrix.values[k][j]) {
                            matrix.values[i][j] = matrix.values[i][k] + matrix.values[k][j]
                        }
                    }
                }
            }
        }

        return matrix
    }

    override fun calculateSolutionA(): String {
        distanceMatrix = getDistanceMatrix(valves)
        usefulValves = getUsefulValves(valves)
        // moving to a valve takes 1 minute
        // opening a valve takes 1 minute
        // only the t(minuteOfOpeningValve) + 1 starts releasing pressure

        // so each action takes one minute
        val startIndex = valves.indexOfFirst { it.name == "AA" }
        var openSet = mutableSetOf<Int>()
        val result = simulateState(startIndex, 0, 0, openSet)

        return result.toString()
    }

    override fun calculateSolutionB(): String {
        // so each action takes one minute
        val startIndex = valves.indexOfFirst { it.name == "AA" }
        var openSet = mutableSetOf<Int>()
        val result = simulateState2(startIndex, startIndex, 0, 0, usefulValves.toMutableList(), openSet, false)

        return result.toString()
    }
}
