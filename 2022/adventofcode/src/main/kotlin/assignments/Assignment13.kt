package assignments

import toolkit.Vector2D
import utilities.Utilities
import kotlin.math.min

class Assignment13 : Assignment() {
    interface PacketData {
        fun compare(x: PacketDataValue): Int
        fun compare(x: PacketDataList): Int

        fun compare(x: PacketData): Int {
            // Kotlin has limitations regarding multiple dispatching, hence the following 'meh' solution
            when (x) {
                is PacketDataValue -> {
                    when (this) {
                        is PacketDataValue -> return this.compare(x)
                        is PacketDataList -> return this.compare(x)
                    }
                }
                is PacketDataList -> {
                    when (this) {
                        is PacketDataValue -> return this.compare(x)
                        is PacketDataList -> return this.compare(x)
                    }
                }
            }

            return 0
        }
    }
    class PacketDataValue(var value: Int) : PacketData {
        override fun compare(x: PacketDataValue): Int {
            if (value < x.value) return -1
            if (value > x.value) return 1
            return 0
        }
        override fun compare(x: PacketDataList): Int {
            val packetValueList = PacketDataList(
                Packet(
                    mutableListOf(PacketDataValue(value))
                )
            )
            return packetValueList.compare(x)
        }
    }
    class PacketDataList(var packets: Packet) : PacketData {
        override fun compare(x: PacketDataValue): Int {
            val packetValueList = PacketDataList(
                Packet(
                    mutableListOf(PacketDataValue(x.value))
                )
            )
            return this.compare(packetValueList)
        }
        override fun compare(x: PacketDataList): Int {
            val count = min(packets.packets.size, x.packets.packets.size)
            for (i in 0 until count) {
                val result = packets.packets[i].compare(x.packets.packets[i])
                if (result != 0) return result
            }
            if (packets.packets.size < x.packets.packets.size) return -1
            if (packets.packets.size > x.packets.packets.size) return 1
            return 0
        }
    }

    data class Packet(var packets: MutableList<PacketData>) {
        fun compare(x: Packet): Int {
            val valueCount = min(packets.size, x.packets.size)
            for (i in 0 until valueCount) {
                val result = packets[i].compare(x.packets[i])
                if (result != 0) return result
            }
            if (packets.size < x.packets.size) return -1
            if (packets.size > x.packets.size) return 1
            return 0
        }
    }

    override fun getInput(): String {
        return "input_13"
    }

    private lateinit var packets: MutableList<Packet>

    private fun getOuterMatchingBrackets(input: String): Vector2D {
        var counter = 0
        var startIndex = input.indexOfFirst { it == '[' }
        var endIndex = startIndex

        for (i in startIndex until input.length) {
            if (input[i] == '[') counter++
            if (input[i] == ']') counter--

            if (counter == 0) {
                endIndex = i
                break
            }
        }
        return Vector2D(startIndex, endIndex + 1)
    }

    private fun getLayerOfCharacter(input: String, index: Int): Int {
        var counter = 0
        for (i in input.indices) {
            if (input[i] == '[') counter++
            if (input[i] == ']') counter--

            if (i == index) return counter
        }
        return counter
    }

    private fun parseInputToPacket(input: String): Packet {
        // get the first layer in between brackets
        var subString = input.substring(
            getOuterMatchingBrackets(input).x,
            getOuterMatchingBrackets(input).y
        ).let { it.substring(1, it.length - 1) }

        // split the packet content string into chunks
        // these chunks can be found by checking for ','
        // however, some of these ',' are not parts of this layer

        // 1.) Find indices of all ',' characters
        var indices = mutableListOf<Int>()
        for (i in subString.indices) {
            if (subString[i] == ',') indices.add(i)
        }
        // 2.) For each occurrence, check if the character is part of the current layer
        indices = indices
            .filter { getLayerOfCharacter(subString, it) == 0 }
            .toMutableList()

        if (indices.size > 0) {
            indices.add(0, 0)
            indices.add(subString.lastIndex)
        }

        // 3.) Chunk all parts of the string based on the verified indices
        var chunks = mutableListOf<String>()

        for (i in 0 until indices.size - 1) {
            var endIndex = indices[i + 1]
            if (i == indices.size - 2) endIndex++

            var chunk = subString.substring(indices[i], endIndex)
            if (chunk.first() == ',') chunk = chunk.removeRange(0, 1)
            if (chunk.last() == ',') chunk = chunk.removeRange(chunk.lastIndex - 1, 1)
            chunks.add(chunk)
        }

        if (chunks.size == 0 && subString.isNotEmpty()) {
            chunks.add(subString)
        }

        // 4.) Move parse each individual chunk to a packet?
        val packetValues = chunks.map {
            // parse lists
            if (it.contains('[')) {
                PacketDataList(parseInputToPacket(it))
            }

            // parse value array
            else {
                PacketDataValue(it.toInt())
            }
        }
            .toMutableList()
        return Packet(packetValues)
    }

    override fun initialize(input: List<String>) {
        val chunks = Utilities.packageByEmptyLine(input)

        var packetList = mutableListOf<Packet>()
        chunks.forEach { chunk ->
            packetList.add(parseInputToPacket(chunk[0]))
            packetList.add(parseInputToPacket(chunk[1]))
        }
        packets = packetList
    }

    override fun calculateSolutionA(): String {
        var total = 0
        for (i in 0 until packets.size / 2) {
            val index = i * 2
            if (packets[index].compare(packets[index + 1]) == -1) total += (i + 1)
        }
        return total.toString()
    }

    override fun calculateSolutionB(): String {
        var editedPackets = packets.toMutableList()

        // add decoders:
        val decoder2 = Packet(
            mutableListOf(PacketDataValue(2))
        )
        val decoder6 = Packet(
            mutableListOf(PacketDataValue(6))
        )
        editedPackets.add(decoder2)
        editedPackets.add(decoder6)

        editedPackets = editedPackets.sortedWith { a, b ->
            when {
                a.compare(b) == -1 -> -1
                a.compare(b) == 1 -> 1
                else -> 0
            }
        }.toMutableList()

        return listOf(
            editedPackets.indexOf(decoder2),
            editedPackets.indexOf(decoder6)
        )
            .map { it + 1 }
            .reduce { a, b -> a * b }
            .toString()
    }
}
