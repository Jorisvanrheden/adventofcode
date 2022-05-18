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

        fun packageByEmptyLine(lines:List<String>):List<List<String>>{
            var output:MutableList<MutableList<String>> = mutableListOf()

            var buffer:MutableList<String> = mutableListOf()
            var index:Int = 0

            while(index < lines.size){
                if(lines[index].isEmpty()){
                    if(buffer.size > 0) output.add(buffer)

                    //reset the buffer
                    buffer = mutableListOf()
                }
                else{
                    buffer.add(lines[index])
                }

                index++
            }

            if(buffer.size > 0) output.add(buffer)

            return output
        }
    }
}