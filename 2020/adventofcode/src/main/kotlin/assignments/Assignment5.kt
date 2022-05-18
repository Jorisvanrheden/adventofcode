package assignments

import toolkit.Vector2D
import kotlin.math.abs
import kotlin.math.floor

class Assignment5 : Assignment()
{
    data class BoardingPass(val rowSequence:List<Int>, val columnSequence:List<Int>)

    private lateinit var boardingPasses:List<BoardingPass>

    override fun getInput(): String {
        return "input_5"
    }

    private fun createBoardingPass(input:String):BoardingPass{
        val rowData = input.substring(0, 7)
        val columnData = input.substring(7, 10)

        val rowSequence:List<Int> = rowData.map { if(it == 'B')  1 else 0 }
        val columnSequence:List<Int> = columnData.map { if(it == 'R')  1 else 0 }

        return BoardingPass(rowSequence, columnSequence)
    }

    override fun initialize(input: List<String>) {
        boardingPasses = input.map { createBoardingPass(it) }
    }

    private fun getRange(range:Vector2D, dimension:Int):Vector2D
    {
        val diff = abs(range.x - range.y).toDouble()
        val addition = floor(diff / 2).toInt()

        if(dimension == 0){
            return Vector2D(range.x, range.x + addition)
        }
        else{
            return Vector2D(range.y - addition, range.y)
        }
    }

    private fun transform(sequence:List<Int>, range:Vector2D):Int{
        var updatedRange = range
        for(x in sequence){
            updatedRange = getRange(updatedRange, x)
        }
        return updatedRange.x
    }

    private fun calculateID(rowIndex:Int, columnIndex:Int):Int{
        return rowIndex * 8 + columnIndex
    }

    private fun getIDs():MutableList<Int>{
        var ids:MutableList<Int> = mutableListOf()
        for(x in boardingPasses){
            val row = transform(x.rowSequence, Vector2D(0, 127))
            val column = transform(x.columnSequence, Vector2D(0, 7))

            val id = calculateID(row, column)
            ids.add(id)
        }
        return ids
    }

    private fun getMissingID(ids:List<Int>):Int{
        for(i in 0..ids.size){
            //check where the difference is 2
            if(i < ids.size - 1){
                if(ids[i + 1] - ids[i] == 2) return ids[i] + 1
            }
        }
        return -1
    }

    override fun calculateSolutionA(): String {
        val ids:MutableList<Int> = getIDs()
        val max:Int = ids.maxOrNull() ?: 0
        return max.toString()
    }

    override fun calculateSolutionB(): String {
        val ids:MutableList<Int> = getIDs()
        ids.sort()
        return getMissingID(ids).toString()
    }
}