package assignments

import models.assignment.Assignment
import kotlin.math.sign

class Assignment2 : Assignment() {
    private lateinit var reports: List<List<Int>>

    override fun getInput(): String {
        return "input_2"
    }

    override fun initialize(input: List<String>) {
        reports = input
            .map { it.split(' ') }
            .map { it.map { it.toInt() } }
    }

    override fun calculateSolutionA(): String {
        return reports
            .count { it.isSafe()  }
            .toString()
    }

    override fun calculateSolutionB(): String {
        return reports
            .count { it.isSafeWithTolerance()  }
            .toString()
    }

    private fun List<Int>.isSafe(): Boolean {
        return !zipWithNext().any { (a, b) -> isNotSafe(b - a) }
    }

    private fun List<Int>.isSafeWithTolerance(): Boolean {
        val index = zipWithNext().indexOfFirst { (a, b) -> isNotSafe(b - a) }
        if (index == -1) return true
        return listOf(
            toMutableList().apply { removeAt(index) },
            toMutableList().apply { removeAt(index + 1) },
        ).any { it.isSafe() }
    }

    private fun List<Int>.isNotSafe(iteration: Int): Boolean {
        // all increasing/decreasing
        if (iteration.sign != (last() - first()).sign) return true
        // iterations must be in between 1 and 3
        if (iteration * iteration.sign < 1 || iteration * iteration.sign > 3) return true
        return false
    }
}

