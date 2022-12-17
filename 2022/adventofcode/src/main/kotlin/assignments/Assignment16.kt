package assignments

class Assignment16 : Assignment() {

    data class Valve(val name: String, val flowRate: Int, val connections: List<Int>)
    data class State(
        var valveOpenedStatuses: MutableList<Boolean>,
        var visitedValves: MutableList<Int> = mutableListOf(),
        var totalPressure: Int = 0,
        var totalFlowRate: Int = 0,
        var activeValveIndex: Int,
        var previousValveIndex: Int = 0,
        var turn: Int = 0
    ) {
        fun copy(): State =
            State(
                valveOpenedStatuses.toMutableList(),
                visitedValves.toMutableList(),
                totalPressure,
                totalFlowRate,
                activeValveIndex,
                previousValveIndex,
                turn
            )

        fun toKey(): String {
            return test(turn, valveOpenedStatuses, totalPressure, totalFlowRate, activeValveIndex).hashCode().toString()
        }

        data class test(val turn: Int, val statuses: List<Boolean>, val pressure: Int, val flow: Int, val activeIndex: Int)
    }

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

    private var dp: MutableMap<String, State> = mutableMapOf()

    private fun simulateState(state: State): State {
        val key = state.toKey()
        if (dp.size > 1000000) dp.clear()
        if (dp.containsKey(key)) return dp[key]!!

        // -- Beginning of the turn --
        // 1.) process the pressure that is being released
        state.totalPressure += state.totalFlowRate

        // add visited valve
        state.visitedValves.add(state.activeValveIndex)

        state.turn++
        if (state.turn == 30) return state

        // -- Determine all the options at this valve --
        // a.) open the valve (ONLY if unopened and the flowRate is bigger than 0)
        val possibleStates = mutableListOf<State>()
        // check the total scores of all options
        if (!state.valveOpenedStatuses[state.activeValveIndex] && valves[state.activeValveIndex].flowRate > 0) {
            // don't process if the flowRate is 0, no point in opening

            val updatedState = state.copy()
            // label the active valve as 'open'
            updatedState.valveOpenedStatuses[updatedState.activeValveIndex] = true
            updatedState.totalFlowRate += valves[updatedState.activeValveIndex].flowRate

            // assign the previous valve index
            updatedState.previousValveIndex = state.activeValveIndex

            // recursively iterate while increasing the turn
            val resultState = simulateState(updatedState)
            possibleStates.add(resultState)
        }

        // b.) move to an adjacent valve
        for (valveIndex in valves[state.activeValveIndex].connections) {
            // check if the valveIndex is not the same as the previous valve index (run in circles)
            if (valveIndex == state.previousValveIndex) continue

            val updatedState = state.copy()
            // assign the updated valve
            updatedState.activeValveIndex = valveIndex
            // assign the previous valve index
            updatedState.previousValveIndex = state.activeValveIndex

            // recursively iterate while increasing the turn
            val resultState = simulateState(updatedState)
            possibleStates.add(resultState)
        }

        if (possibleStates.isEmpty()) {
            return simulateState(state)
        }

        // only return the one with the highest value
        val r = possibleStates.maxByOrNull { it.totalPressure }!!
        if (r.totalPressure > highestTotalPressure) {
            highestTotalPressure = r.totalPressure
            println(highestTotalPressure)
        }

        dp[key] = r
        return r
    }

    private var highestTotalPressure = 0

    override fun calculateSolutionA(): String {
        // moving to a valve takes 1 minute
        // opening a valve takes 1 minute
        // only the t(minuteOfOpeningValve) + 1 starts releasing pressure

        // so each action takes one minute
        val openedStatus = valves.map { false }.toMutableList()
        val startIndex = valves.indexOfFirst { it.name == "AA" }
        var state = State(openedStatus, activeValveIndex = startIndex)
        val result = simulateState(state)

        val text = result.visitedValves.map { valves[it].name }
        println(text)

        return result.totalPressure.toString()
    }

    override fun calculateSolutionB(): String {
        return ""
    }
}
