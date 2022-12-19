package assignments

class Assignment16 : Assignment() {

    data class Actor(var activeValveIndex: Int, var previousValveIndex: Int)

    data class Valve(val name: String, val flowRate: Int, val connections: List<Int>)
    data class State(
        var valveOpenedStatuses: MutableList<Boolean>,
        var totalPressure: Int = 0,
        var totalFlowRate: Int = 0,
        var actors: List<Actor>,
        var turn: Int = 0
    ) {
        fun copy(): State =
            State(
                valveOpenedStatuses.toMutableList(),
                totalPressure,
                totalFlowRate,
                actors.map { it.copy() },
                turn
            )

        fun toKeyInverse(): String {
            return testActor(
                turn,
                valveOpenedStatuses,
                totalPressure,
                totalFlowRate,
                actors[1].activeValveIndex,
                actors[0].activeValveIndex
            ).hashCode().toString()
        }

        fun toKey(): String {
            return testActor(
                turn,
                valveOpenedStatuses,
                totalPressure,
                totalFlowRate,
                actors[0].activeValveIndex,
                actors[1].activeValveIndex
            ).hashCode().toString()
        }
        data class testActor(val turn: Int, val statuses: List<Boolean>, val pressure: Int, val flow: Int, val ownActive: Int, val otherActive: Int)
        data class test(val turn: Int, val statuses: List<Boolean>, val pressure: Int, val flow: Int, val actors: List<Actor>)
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

    private var dp: MutableMap<String, Int> = mutableMapOf()

    interface Action {
        fun apply(state: State): Boolean
    }
    class OpenValveAction(val actorIndex: Int, private val valves: List<Valve>) : Action {
        override fun apply(state: State): Boolean {
            val valveIndexFromActiveActor = state.actors[actorIndex].activeValveIndex

            // label the active valve as 'open'
            state.valveOpenedStatuses[valveIndexFromActiveActor] = true
            state.totalFlowRate += valves[valveIndexFromActiveActor].flowRate

            // assign the previous valve index
            state.actors[actorIndex].previousValveIndex = valveIndexFromActiveActor

            return true
        }
    }
    class MoveAction(private val actorIndex: Int, private val targetIndex: Int) : Action {
        override fun apply(state: State): Boolean {
            // check if the valveIndex is not the same as the previous valve index (run in circles)
            if (targetIndex == state.actors[actorIndex].previousValveIndex) return false

            // assign the previous valve index
            state.actors[actorIndex].previousValveIndex = state.actors[actorIndex].activeValveIndex
            // assign the updated valve
            state.actors[actorIndex].activeValveIndex = targetIndex

            return true
        }
    }

    private fun simulateState(state: State, maxTurns: Int): Int {
        val key = state.toKey()
        val key2 = state.toKeyInverse()

        if (dp.size > 20000000) {
            dp.clear()
            println("Cleaning the DP map")
        }
        if (dp.containsKey(key)) return dp[key]!!
        if (dp.containsKey(key2)) return dp[key2]!!

        // -- Beginning of the turn --
        // 1.) process the pressure that is being released
        state.totalPressure += state.totalFlowRate

        state.turn++
        if (state.turn == maxTurns) return state.totalPressure

        // -- Determine all the options at this valve --
        var actionsToProcess = MutableList<MutableList<Action>>(state.actors.size) { mutableListOf() }

        // a.) open the valve (ONLY if unopened and the flowRate is bigger than 0)
        for (i in state.actors.indices) {
            val actorActiveValveIndex = state.actors[i].activeValveIndex
            if (!state.valveOpenedStatuses[actorActiveValveIndex] && valves[actorActiveValveIndex].flowRate > 0) {
                actionsToProcess[i].add(OpenValveAction(i, valves))
            }
        }

        // b.) move to an adjacent valve
        for (i in state.actors.indices) {
            val actorActiveValveIndex = state.actors[i].activeValveIndex
            for (valveIndex in valves[actorActiveValveIndex].connections) {
                actionsToProcess[i].add(MoveAction(i, valveIndex))
            }
        }

        var processedStates = mutableListOf<State>()
        for (action in actionsToProcess[0]) {
//            var updatedState = state.copy()
//            if (action.apply(updatedState)) {
//                processedStates.add(updatedState)
//            }
            for (action2 in actionsToProcess[1]) {
                if (action is OpenValveAction && action2 is OpenValveAction) {
                    if (state.actors[action.actorIndex].activeValveIndex ==
                        state.actors[action2.actorIndex].activeValveIndex
                    ) {
                        continue
                    }
                }

                var updatedState = state.copy()

                if (action.apply(updatedState) && action2.apply(updatedState)) {
                    processedStates.add(updatedState)
                }
            }
        }

        return if (processedStates.isEmpty()) {
            // continue with the current state, no branches are possible
            simulateState(state, maxTurns)
        } else {
            processedStates.map { simulateState(it, maxTurns) }.maxByOrNull { it }!!.also { state ->
                dp[key] = state

                if (state > highestPressure) {
                    highestPressure = state
                    println(highestPressure)
                }
            }
        }
    }

    private var highestPressure = 0

    override fun calculateSolutionA(): String {
//        val openedStatus = valves.map { false }.toMutableList()
//        val startIndex = valves.indexOfFirst { it.name == "AA" }
//        var state = State(openedStatus, actors = listOf(Actor(startIndex, startIndex)))
//        val result = simulateState(state, 30)
//
//        return result.totalPressure.toString()
        return ""
    }

    override fun calculateSolutionB(): String {
        val openedStatus = valves.map { false }.toMutableList()
        val startIndex = valves.indexOfFirst { it.name == "AA" }
        var state = State(openedStatus, actors = listOf(Actor(startIndex, startIndex), Actor(startIndex, startIndex)))
        val result = simulateState(state, 26)

        return result.toString()
        return ""
    }
}
