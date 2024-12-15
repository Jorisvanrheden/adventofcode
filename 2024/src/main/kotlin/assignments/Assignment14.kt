package assignments

import models.assignment.Assignment
import models.vector.Vector2D
import java.awt.Color
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

class Assignment14 : Assignment(14) {
    private data class Robot(
        var position: Vector2D,
        val velocity: Vector2D,
    )
    private lateinit var robots: List<Robot>

    private fun String.toVector2D(): Vector2D {
        return split('=').last().split(',').let {
            Vector2D(it[0].toInt(), it[1].toInt())
        }
    }

    override fun initialize(input: List<String>) {
        robots = input.map {
            it.split(' ').let {
                Robot(
                    it[0].toVector2D(),
                    it[1].toVector2D(),
                )
            }
        }
    }

    override fun calculateSolutionA(): String {
        return simulate(robots, 100, WIDTH, HEIGHT)
            .filter { !it.isInMiddle(WIDTH, HEIGHT) }
            .map { it.toQuadrant(WIDTH, HEIGHT) }
            .groupingBy { it }
            .eachCount()
            .values
            .reduce { a, b -> a * b }
            .toString()
    }

    override fun calculateSolutionB(): String {
        simulate(robots, 6771, WIDTH, HEIGHT).exportAsImage(WIDTH, HEIGHT, 6771.toString())
        return "See the image 6771.png"
    }

    private fun simulate(robots: List<Robot>, seconds: Int, width: Int, height: Int): List<Robot> {
        val copiedRobots = robots.map { it.copy() }
        for (i in 0 until seconds) {
            for (robot in copiedRobots) {
                robot.position += robot.velocity
                robot.position.x = robot.position.x.mod(width)
                robot.position.y = robot.position.y.mod(height)
            }
        }
        return copiedRobots
    }

    private fun List<Robot>.exportAsImage(width: Int, height: Int, title: String) {
        val image = BufferedImage(width, height, BufferedImage.TYPE_INT_RGB)
        for (y in 0 until width) {
            for (x in 0 until width) {
                image.setRGB(x, y, Color.WHITE.rgb)
            }
        }
        forEach { image.setRGB(it.position.x, it.position.y, Color.BLACK.rgb) }

        val file = File("$title.png")
        ImageIO.write(image, "png", file)
    }

    private fun Robot.isInMiddle(w: Int, h: Int): Boolean {
        val mx = (w.toDouble() / 2).toInt()
        val my = (h.toDouble() / 2).toInt()
        return mx == position.x || my == position.y
    }

    private fun Robot.toQuadrant(w: Int, h: Int): Vector2D {
        val mx = (w.toDouble() / 2).toInt()
        val my = (h.toDouble() / 2).toInt()
        val x = if (position.x < mx) - 1 else 1
        val y = if (position.y < my) - 1 else 1
        return Vector2D(x, y)
    }

    private companion object {
        const val WIDTH = 101
        const val HEIGHT = 103
    }
}

