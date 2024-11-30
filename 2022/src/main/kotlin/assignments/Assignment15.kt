package assignments

import toolkit.Vector2DLong
import java.lang.Long.max
import kotlin.math.abs
import kotlin.math.min

class Assignment15 : Assignment() {

    data class Bounds(val xMin: Long, val xMax: Long, val yMin: Long, val yMax: Long)
    data class Sensor(val position: Vector2DLong, val closestBeacon: Vector2DLong) {
        fun getManhattanDistanceToBeacon(): Long =
            abs(closestBeacon.x - position.x) + abs(closestBeacon.y - position.y)

        fun getManhattanDistanceTo(Vector2DLong: Vector2DLong): Long =
            abs(Vector2DLong.x - position.x) + abs(Vector2DLong.y - position.y)

        fun getYRangeAt(x: Long): Vector2DLong {
            var rangeAtOrigin = Vector2DLong(
                position.y - getManhattanDistanceToBeacon(),
                position.y + getManhattanDistanceToBeacon()
            )

            val distanceToMove = abs(position.x - x)
            rangeAtOrigin.x += distanceToMove
            rangeAtOrigin.y -= distanceToMove
            if (rangeAtOrigin.x > rangeAtOrigin.y) return Vector2DLong(0, 0)
            return rangeAtOrigin
        }
    }

    override fun getInput(): String {
        return "input_15"
    }

    private lateinit var sensors: List<Sensor>

    private fun String.parseCoordinate(): Long {
        return split('=').let {
            it[1].toLong()
        }
    }
    private fun parseChunkToVector(input: String): Vector2DLong {
        val parts = input
            .replace(",", "")
            .split(' ')

        val x = parts[parts.size - 2].parseCoordinate()
        val y = parts[parts.size - 1].parseCoordinate()
        return Vector2DLong(x, y)
    }

    override fun initialize(input: List<String>) {
        sensors = input.map {
            val sensorBeaconChunk = it.split(':')
            Sensor(
                parseChunkToVector(sensorBeaconChunk[0]),
                parseChunkToVector(sensorBeaconChunk[1])
            )
        }
    }

    private fun getBounds(sensors: List<Sensor>): Bounds {
        var xPositions = mutableListOf<Long>()
        xPositions.addAll(sensors.map { it.position.x - it.getManhattanDistanceToBeacon() })
        xPositions.addAll(sensors.map { it.position.x + it.getManhattanDistanceToBeacon() })

        xPositions.addAll(sensors.map { it.closestBeacon.x - it.getManhattanDistanceToBeacon() })
        xPositions.addAll(sensors.map { it.closestBeacon.x + it.getManhattanDistanceToBeacon() })

        var yPositions = mutableListOf<Long>()
        yPositions.addAll(sensors.map { it.position.y - it.getManhattanDistanceToBeacon() })
        yPositions.addAll(sensors.map { it.position.y + it.getManhattanDistanceToBeacon() })
        yPositions.addAll(sensors.map { it.closestBeacon.y - it.getManhattanDistanceToBeacon() })
        yPositions.addAll(sensors.map { it.closestBeacon.y + it.getManhattanDistanceToBeacon() })

        return Bounds(
            xPositions.minOf { it },
            xPositions.maxOf { it },
            yPositions.minOf { it },
            yPositions.maxOf { it }
        )
    }

    private fun canBeFoundByAnySensor(vector: Vector2DLong, sensors: List<Sensor>): Boolean {
        // don't process coordinates that have a sensor or beacon on it
        if (sensors.count { it.position == vector || it.closestBeacon == vector } > 0) return false

        for (sensor in sensors) {
            val radius = sensor.getManhattanDistanceToBeacon()
            val distance = sensor.getManhattanDistanceTo(vector)

            if (distance <= radius) return true
        }
        return false
    }

    override fun calculateSolutionA(): String {
        val bounds = getBounds(sensors)
        val y: Long = 2000000

        // check for each x, y coordinates
        val range = bounds.xMin..bounds.xMax
        return range.count {
            canBeFoundByAnySensor(Vector2DLong(it, y), sensors)
        }.toString()
    }

    private fun LongRange.overlapsWith(range: LongRange): Boolean {
        return (range.first >= first && range.first <= last + 1 && range.last >= last + 1)
    }

    private fun LongRange.constrain(range: LongRange): LongRange {
        return max(first, range.first)..min(last, range.last)
    }

    private fun getRangeCoveredBySensors(x: Long, sensors: List<Sensor>, range: Long): LongRange {
        // get covered y-range for each sensor
        // the y range on the source x is easy, then for each x that deviates (remove one top and bottom)
        val ranges = sensors
            .map { it.getYRangeAt(x) }
            .filter { it != Vector2DLong(0, 0) }
            .sortedBy { it.x }
            .map { it.x..it.y }

        // Sort by first element
        var minRange = ranges[0]

        // Find one contiguous range, where each consecutive range overlaps the other
        // If there is no overlap anymore, meaning that a hole that is not covered has been found
        for (range in ranges) {
            if (minRange.overlapsWith(range)) {
                // If the new range overlaps with the min range,
                // and the new range max value is bigger than the min range max value
                minRange = minRange.first..range.last
            }
        }
        return minRange
    }

    override fun calculateSolutionB(): String {
        var coordinates = mutableListOf<Vector2DLong>()
        val range: Long = 4000000

        // go through each row
        for (i in 0..range) {
            // and determine how much of the y range is covered by all sensors

            // if the full y range is not covered by the sensors, that means that the y coordinate is found
            // by adding 1 to the last covered coordinate
            var coveredRange =
                getRangeCoveredBySensors(i, sensors, range)
                    .constrain(0..range)
            if (coveredRange != 0..range) {
                coordinates.add(Vector2DLong(i, coveredRange.last + 1))
            }
        }
        return coordinates.first().let {
            it.x * range + it.y
        }.toString()
    }
}
