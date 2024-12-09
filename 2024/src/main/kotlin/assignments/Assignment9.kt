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
            val id = it
            val blockLength = diskMap[it * 2].digitToInt()
            val emptySpace = (it * 2 + 1).let {
                if (it < diskMap.length) diskMap[it].digitToInt()
                else 0
            }
            File(
                id = id,
                blockLength = blockLength,
                emptySpaceLength = emptySpace
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

        while (true) {
            val indexToReplace = entries.indexOfFirst { !it.hasValue }
            val indexToUse = entries.indexOfLast { it.hasValue }

            if (indexToReplace > indexToUse) break

            val old = entries[indexToReplace]
            entries[indexToReplace] = entries[indexToUse]
            entries[indexToUse] = old
        }

        return entries
            .toChecksum()
            .toString()
    }

    override fun calculateSolutionB(): String {
        val itemsToFit = files.asReversed().toMutableList()
        for (item in itemsToFit) {
            val fileWithFreeSpace = files.firstOrNull { (it.emptySpaceLength - it.entryUnits.size) >= item.blockLength } ?: continue

            if (files.indexOf(fileWithFreeSpace) > files.indexOf(item)) continue

            fileWithFreeSpace.entryUnits.addAll(item.toUnits().filter { it.hasValue })

            files.first { it.id == item.id }.processed = true
        }

        val s = files.map {
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
        }.flatten()

        return s
            .toChecksum()
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

