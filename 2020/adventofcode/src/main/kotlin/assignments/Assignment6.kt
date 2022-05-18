package assignments

import utilities.Utilities

class Assignment6 : Assignment()
{
    private lateinit var groupAnswers:List<List<String>>

    override fun getInput(): String {
        return "input_6"
    }

    override fun initialize(input: List<String>) {
        groupAnswers = Utilities.packageByEmptyLine(input)
    }

    private fun allEntriesContain(chunk:List<String>, letter:Char):Boolean{
        for(x in chunk){
            if(!x.contains(letter)) return false
        }
        return true
    }

    private fun getAnswerCountPerChunk(chunk:List<String>):Int{
        var total:Int = 0
        val alphabet:String = "abcdefghijklmnopqrstuvwxyz"
        for(c in alphabet){
            if(allEntriesContain(chunk, c)) total++
        }
        return total
    }

    override fun calculateSolutionA(): String {
        var answers:MutableList<String> = mutableListOf()
        for(x in groupAnswers){
            answers.add(x.joinToString { it }.replace(", ", ""))
        }

        var total:Int = 0
        for(x in answers){
            total += x.toSet().size
        }
        return total.toString()
    }

    override fun calculateSolutionB(): String {
        var total:Int = 0
        for(x in groupAnswers){
            total += getAnswerCountPerChunk(x)
        }
        return total.toString()
    }
}