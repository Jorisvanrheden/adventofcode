package assignments

class Assignment16 : Assignment() {

    data class Valve(val name: String, val flowRate: Int, val valves: List<String>)
    data class State(var totalPressure: Int, var activeValve: Valve, var turn: Int, var openedValves: MutableSet<Valve>) {
        fun copy(): State =
            State(
                totalPressure,
                activeValve.copy(),
                turn,
                openedValves.toMutableSet()
            )
    }

    override fun getInput(): String {
        return "input_16"
    }

    private lateinit var valves: List<Valve>

    private fun parseInputToValve(input: String): Valve {
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

        return Valve(
            valveInfo[1],
            valveInfo[4].split('=').let { it[1].toInt() },
            tunnelInfo
        )
    }

    override fun initialize(input: List<String>) {
        valves = input.map { parseInputToValve(it) }
    }

    private fun List<Valve>.getValveByName(name: String): Valve =
        first { it.name == name }

    private fun simulateState(allValves: List<Valve>, state: State): State {
        // determine all the options at this valve
        // - move to an adjacent valve
        // - open the valve (ONLY if unopened)

        // Beginning of the turn:
        // - process the pressure that is being released
//        println("${state.totalFlowRate} <--> ${state.openedValves.sumOf { it.flowRate }}")
        state.totalPressure += state.openedValves.sumOf { it.flowRate }

        if (state.turn == 30) {
            return state
        }

        val possibleStates = mutableListOf<State>()
        // check the total scores of all options
        if (!state.openedValves.contains(state.activeValve)) {
            val updatedState = state.copy()
            // label the active valve as 'open'
            updatedState.openedValves.add(updatedState.activeValve)

            updatedState.turn++

            // recursively iterate while increasing the turn
            val resultState = simulateState(allValves, updatedState)
            possibleStates.add(resultState)
        }

        // store other states given different selected valves
        for (valveName in state.activeValve.valves) {
            val updatedState = state.copy()
            // assign the updated valve
            updatedState.activeValve = allValves.getValveByName(valveName)

            updatedState.turn++

            // recursively iterate while increasing the turn
            val resultState = simulateState(allValves, updatedState)
            possibleStates.add(resultState)
        }

        // only return the one with the highest value
        val r = possibleStates.maxByOrNull { it.totalPressure }!!
        if (r.totalPressure > highestTotalPressure) {
            highestTotalPressure = r.totalPressure
            println(highestTotalPressure)
        }
        return r
    }

    private var highestTotalPressure = 0

    override fun calculateSolutionA(): String {
        // moving to a valve takes 1 minute
        // opening a valve takes 1 minute
        // only the t(minuteOfOpeningValve) + 1 starts releasing pressure

        // so each action takes one minute
        var state = State(0, valves[0], 0, mutableSetOf())

        state = simulateState(valves, state)

        return state.totalPressure.toString()
    }

    override fun calculateSolutionB(): String {
        return ""
    }
}
