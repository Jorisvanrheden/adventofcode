package assignments

import models.assignment.Assignment

class Assignment15 : Assignment(15) {
    private data class Box(val lenses: MutableList<Lens>)
    private data class Lens(val label: String, val focalStrength: Int)

    private lateinit var boxes: List<Box>
    private lateinit var sequence: List<String>

    override fun initialize(input: List<String>) {
        sequence = input.first().split(',')
    }

    override fun calculateSolutionA(): String {
        return sequence.sumOf {
            it.toScore()
        }.toString()
    }

    override fun calculateSolutionB(): String {
        boxes = IntRange(0, 255).map { Box(mutableListOf()) }

        // rn=1,cm-,qp=3,cm=2,qp-,pc=4,ot=9,ab=5,pc-,pc=6,ot=7
        sequence.forEach {
            if (it.contains("=")) {
                val label = it.split('=')[0]
                val boxIndex = label.toScore()
                val focalStrength = it.split('=')[1].toInt()

                // If there is already a lens in the box with the same label,
                // replace the old lens with the new lens:
                // remove the old lens and put the new lens in its place, not moving any other lenses in the box.
                val existingLabelIndex = boxes[boxIndex].lenses.indexOfFirst { it.label == label }
                if (existingLabelIndex == -1) {
                    boxes[boxIndex].lenses.add(Lens(label, focalStrength))
                } else {
                    boxes[boxIndex].lenses[existingLabelIndex] = Lens(label, focalStrength)
                }
                // If there is not already a lens in the box with the same label,
                // add the lens to the box immediately behind any lenses already in the box.
                // Don't move any of the other lenses when you do this.
                // If there aren't any lenses in the box, the new lens goes all the way to the front of the box.
            } else if (it.contains("-")) {
                val label = it.replace("-", "")
                val boxIndex = label.toScore()

                boxes[boxIndex].lenses.removeIf { it.label == label }

                // If the operation character is a dash (-),
                // go to the relevant box and remove the lens with the given label if it is present in the box.
                // Then, move any remaining lenses as far forward in the box as they can go without changing their order,
                // filling any space made by removing the indicated lens.
                // (If no lens in that box has the given label, nothing happens.)
            }
        }

        return sequence
            .map {
                if (it.contains("=")) {
                    it.split('=')[0]
                } else {
                    it.replace("-", "")
                }
            }
            .distinct()
            .sumOf { label ->
                val box = boxes.firstOrNull { box ->
                    box.lenses.count { lens -> lens.label == label } > 0
                }

                if (box == null) {
                    0
                } else {
                    //  multiply:
                    //   - 1 + box number of lens
                    //   - 1 + index of lens in that box
                    //   - focal strength
                    listOf(
                        1 + boxes.indexOf(box),
                        1 + box.lenses.indexOfFirst { it.label == label },
                        box.lenses.first { it.label == label }.focalStrength,
                    ).reduce { a, b -> a * b }
                }
            }
            .toString()
    }

    private fun String.toScore(): Int {
        var score = 0
        toCharArray().forEach { c ->
            score = c.toScore(score)
        }
        return score
    }

    private fun Char.toScore(startValue: Int): Int {
        var newValue = startValue + code
        newValue *= 17
        newValue %= 256
        return newValue
    }
}
