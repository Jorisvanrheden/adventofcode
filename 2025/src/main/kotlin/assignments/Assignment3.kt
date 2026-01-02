package assignments

import models.assignment.Assignment

class Assignment3 : Assignment(3) {
    data class BatteryBank(
        val batteries: List<Int>,
    )
    private lateinit var batteryBanks: List<BatteryBank>

    override fun initialize(input: List<String>) {
        batteryBanks = input.map { line ->
            BatteryBank(line.map { it.digitToInt() })
        }
    }

    override fun calculateSolutionA(): String {
        return batteryBanks
            .sumOf { it.batteries.toJoltage(2) }
            .toString()
    }

    override fun calculateSolutionB(): String {
        return batteryBanks
            .sumOf { it.batteries.toJoltage(12) }
            .toString()
    }

    private fun List<Int>.toJoltage(batteryCount: Int, result: String = ""): Long {
        if (batteryCount <= 0) {
            return result.toLong()
        }

        // Example, take 24859
        // For 1: highest is 9
        // For 2: highest is 89
        // For 3: highest is 855
        // For 4: highest is 4859
        // For 5: highest is 24859

        // For 1: Loop through and take the highest number
        // For 2: Find highest number where index is less than (itemsLength - index)
        //        Then create a subset from index -> end
        //        Repeat (even recursion possible?)
        // Only scan for items 0..lastIndex - batteryCount
        val subsetToProcess = subList(0, size - (batteryCount - 1))

        // This is the number that is guaranteed to be part of the set
        val subsetMax = subsetToProcess.max()
        val indexOfHighestNumber = subsetToProcess.indexOfFirst { it == subsetMax }

        // Remaining set to check on
        val remainingSet = subList(indexOfHighestNumber + 1, size)

        return remainingSet.toJoltage(batteryCount - 1, result + this[indexOfHighestNumber].toString())
    }
}
