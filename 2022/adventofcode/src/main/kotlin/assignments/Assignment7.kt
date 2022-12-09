package assignments

import kotlin.math.abs

class Assignment7 : Assignment() {

    data class File(val size: Int, val name: String)
    data class Directory(val name: String, var files: MutableList<File>, var dirs: MutableList<Directory>, var parent: Directory?) {
        fun getSize(): Int =
            files.sumOf { it.size } + dirs.sumOf { it.getSize() }

        fun getFolderByName(name: String): Directory =
            dirs.first { it.name == name }

        fun listSubFolders(): MutableList<Directory> {
            var subFolders = mutableListOf(this)
            for (dir in dirs) {
                subFolders.addAll(dir.listSubFolders())
            }
            return subFolders
        }

        fun addFolder(name: String) {
            if (dirs.count { it.name == name } > 0) return
            dirs.add(
                Directory(name, mutableListOf(), mutableListOf(), this)
            )
        }

        fun addFile(name: String, size: Int) {
            if (files.count { it.name == name } > 0) return
            files.add(
                File(size, name)
            )
        }
    }

    private var dir: Directory = Directory("/", mutableListOf(), mutableListOf(), null)
    private var activeDir: Directory = dir

    override fun getInput(): String {
        return "input_7"
    }

    private fun addContentToDirectory(directory: Directory, index: Int, input: List<String>): Int {
        var i = index

        // add content until the next command to the current directory
        while (!input[i].startsWith("$")) {
            val parts = input[i].split(' ')
            if (parts[0] == "dir") {
                directory.addFolder(parts[1])
            } else {
                directory.addFile(parts[1], parts[0].toInt())
            }

            if (i >= input.size - 1) break
            i += 1
        }
        return i
    }

    private fun processInput(index: Int, input: List<String>): Int {
        val line = input[index]
        val parts = line.split(' ')

        if (line == "$ cd /") {
            // go to top level directory
            return index + 1
        } else if (line == "$ ls") {
            // process the content and also move the lines pointer
            return addContentToDirectory(activeDir, index + 1, input)
        } else if (line.startsWith("$ cd") && parts.size == 3) {
            // set the active folder to be the parent of the current one
            if (parts[2] == "..") {
                if (activeDir.parent != null) activeDir = activeDir.parent!!
            }
            // set the active folder to be the child directory of the current folder
            else {
                activeDir = activeDir.getFolderByName(parts[2])
            }
            return index + 1
        }

        return index + 1
    }

    override fun initialize(input: List<String>) {
        var index = 0
        while (index < input.size) {
            index = processInput(index, input)
        }
    }

    override fun calculateSolutionA(): String {
        return dir.listSubFolders()
            .map { it.getSize() }
            .filter { it < 100000 }
            .sum()
            .toString()
    }

    override fun calculateSolutionB(): String {
        val spaceLeft = 70000000 - dir.getSize()
        val spaceRequired = 30000000
        return dir.listSubFolders()
            .map { it.getSize() }
            .sortedBy { it }
            .first { it >= abs(spaceLeft - spaceRequired) }
            .toString()
    }
}
