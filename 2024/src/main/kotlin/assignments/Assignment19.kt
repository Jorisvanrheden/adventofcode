package assignments

import models.assignment.Assignment
import utilities.Utilities

class Assignment19 : Assignment(19) {
    private lateinit var patterns: List<String>
    private lateinit var designs: List<String>

    override fun initialize(input: List<String>) {
        patterns = input[0].split(',').map { it.trim() }
        designs = Utilities.packageByEmptyLine(input)[1]
    }

    override fun calculateSolutionA(): String {
        return designs.count {
            canDesignBeMadeWithPatterns(it, patterns) > 0
        }.toString()
    }

    override fun calculateSolutionB(): String {
        return designs.sumOf {
            canDesignBeMadeWithPatterns(it, patterns)
        }.toString()
    }

    private fun canDesignBeMadeWithPatterns(
        design: String,
        patterns: List<String>,
        map: MutableMap<String, Long> = mutableMapOf(),
    ): Long {
        if (map.containsKey(design)) return map[design]!!
        if (design.isEmpty()) return 1

       return patterns
           .filter { design.startsWith(it) }
           .sumOf { canDesignBeMadeWithPatterns(design.substring(it.length), patterns, map) }
           .also { map[design] = it }
    }
}