package assignments

import models.assignment.Assignment
import kotlin.math.ceil

class Assignment9 : Assignment(9) {
    private data class File(
        val id: Int,
        val blockLength: Int,
        val emptySpaceLength: Int,
        val entryUnits: MutableList<EntryUnit> = mutableListOf(),
        var processed: Boolean = false
    )
    data class EntryUnit(
        val id: Int,
        val hasValue: Boolean,
    )

    private lateinit var files: List<File>

    override fun initialize(input: List<String>) {
        val diskMap = input.first()
        files = IntRange(
            0,
            ceil(diskMap.length.toDouble() / 2).toInt() - 1,
        ).map {
            val startIndex = it * 2
            File(
                id = it,
                blockLength = diskMap[startIndex].digitToInt(),
                emptySpaceLength = (startIndex + 1).let {
                    if (it < diskMap.length) diskMap[it].digitToInt()
                    else 0
                }
            )
        }
    }

    private fun File.toUnits(): List<EntryUnit> {
        val list = mutableListOf<EntryUnit>()
        for (i in 0 until blockLength) {
            list.add(EntryUnit(id, true))
        }
        for (i in 0 until emptySpaceLength) {
            list.add(EntryUnit(id, false))
        }
        return list
    }

    override fun calculateSolutionA(): String {
        val entries = files.flatMap { it.toUnits() }.toMutableList()

        val emptyIndices = entries.mapIndexedNotNull { index, s -> index.takeIf { !s.hasValue } }
        val filledIndices  = entries.mapIndexedNotNull { index, s -> index.takeIf { s.hasValue } }

        for (i in emptyIndices.indices) {
            val emptySpaceIndex = emptyIndices[i]
            val filledSpaceIndex = filledIndices[filledIndices.lastIndex - i]
            if (emptySpaceIndex > filledSpaceIndex) break

            val old = entries[emptySpaceIndex]
            entries[emptySpaceIndex] = entries[filledSpaceIndex]
            entries[filledSpaceIndex] = old
        }
        return entries
            .toChecksum()
            .toString()
    }

    override fun calculateSolutionB(): String {
        val items = files.sortedByDescending { it.id }
        for (item in items) {
            val fileWithFreeSpace = files.firstOrNull { (it.emptySpaceLength - it.entryUnits.size) >= item.blockLength } ?: continue

            if (files.indexOf(fileWithFreeSpace) > files.indexOf(item)) continue

            fileWithFreeSpace.entryUnits.addAll(item.toUnits().filter { it.hasValue })

            item.processed = true
        }

        return files.flatMap {
            val list = mutableListOf<EntryUnit>()
            if (it.processed) {
                for (i in 0 until it.blockLength + it.emptySpaceLength) {
                    list.add(EntryUnit(it.id, false))
                }
                // add entries
                for (i in it.blockLength until it.blockLength + it.entryUnits.size) {
                    list[i] = it.entryUnits[i - it.blockLength]
                }
            } else {
                // create the complete space
                for (i in 0 until it.blockLength + it.emptySpaceLength) {
                    list.add(EntryUnit(it.id, false))
                }
                // overwrite actual block values
                for (i in 0 until it.blockLength) {
                    list[i] = EntryUnit(it.id, true)
                }
                for (i in it.blockLength until it.blockLength + it.entryUnits.size) {
                    list[i] = it.entryUnits[i - it.blockLength]
                }
            }
            list
        }.toChecksum()
            .toString()
    }

    private fun List<EntryUnit>.toChecksum() =
        mapIndexedNotNull { index, it ->
            if (it.hasValue) {
                (index * it.id).toLong()
            } else {
                null
            }
        }.sum()
}

