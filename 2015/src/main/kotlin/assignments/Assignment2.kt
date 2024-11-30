package assignments

import models.assignment.Assignment

class Assignment2 : Assignment() {

    override fun getInput(): String {
        return "input_2"
    }

    data class Size(val l: Int, val w: Int, val h: Int)

    private lateinit var sizes: List<Size>

    override fun initialize(input: List<String>) {
        sizes = input.map {
            it.split('x')
                .map { it.toInt() }
                .let { chunks ->
                    Size(chunks[0], chunks[1], chunks[2])
                }
        }
    }

    private fun Size.getSurfaceArea() =
        2 * l * w + 2 * w * h + 2 * h * l

    private fun Size.getSmallestSideArea() =
        listOf(l * w, w * h, h * l).minOf { it }

    private fun Size.getRibbonLengthPerimeter() =
        listOf(l, w, h)
            .sortedBy { it }
            .take(2)
            .sumOf { it + it }

    private fun Size.getRibbonLengthVolume() =
        l * w * h

    private fun Size.getRequiredRibbonLength() =
        getRibbonLengthVolume() + getRibbonLengthPerimeter()

    override fun calculateSolutionA(): String =
        sizes.sumOf {
            it.getSurfaceArea() + it.getSmallestSideArea()
        }
            .toString()

    override fun calculateSolutionB(): String =
        sizes.sumOf {
            it.getRequiredRibbonLength()
        }
            .toString()
}
