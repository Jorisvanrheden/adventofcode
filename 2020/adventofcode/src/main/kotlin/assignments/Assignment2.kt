package assignments
class Assignment2 : Assignment()
{
    data class PasswordEntry(val min:Int, val max:Int, val letter:Char, val password:String)

    private lateinit var passwordEntries:List<PasswordEntry>

    override fun getInput(): String {
        return "input_2"
    }

    private fun stringToPasswordEntry(input:String):PasswordEntry{
        val chunks = input.split(": ")
        val descriptionChunks = chunks[0].split(" ")
        val rangeChunks = descriptionChunks[0].split("-")

        val min = rangeChunks[0].toInt()
        val max = rangeChunks[1].toInt()

        val letter = descriptionChunks[1][0]
        val password = chunks[1]

        return PasswordEntry(min, max, letter, password)
    }

    override fun initialize(input: List<String>) {
        passwordEntries = input.map {
            stringToPasswordEntry(it)
        }
    }

    private fun isCorrectPasswordA(passwordEntry: PasswordEntry):Boolean{
        val occurrences:Int = passwordEntry.password.count { it == passwordEntry.letter }
        return occurrences >= passwordEntry.min && occurrences <= passwordEntry.max
    }

    private fun isCorrectPasswordB(passwordEntry: PasswordEntry):Boolean{
        //max 1 matching letter should be at the location

        //if both indices contain the letter, password is invalid
        //if 0 indices contain the letter, password is also invalid

        var correctLetterCount:Int = 0
        if(passwordEntry.password[passwordEntry.min - 1] == passwordEntry.letter) correctLetterCount++
        if(passwordEntry.password[passwordEntry.max - 1] == passwordEntry.letter) correctLetterCount++

        return correctLetterCount == 1
    }

    override fun calculateSolutionA(): String {
        var correctCount:Int = 0
        for(entry in passwordEntries){
            if(isCorrectPasswordA(entry)) correctCount++
        }
        return correctCount.toString()
    }

    override fun calculateSolutionB(): String {
        var correctCount:Int = 0
        for(entry in passwordEntries){
            if(isCorrectPasswordB(entry)) correctCount++
        }
        return correctCount.toString()
    }
}