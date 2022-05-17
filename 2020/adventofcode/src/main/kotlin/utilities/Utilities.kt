package utilities

import java.io.File

class Utilities
{
    companion object
    {
        fun readFile(filePath:String):List<String>
        {
            return File(filePath).readLines()
        }
    }
}