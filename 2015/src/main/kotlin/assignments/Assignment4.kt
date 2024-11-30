package assignments

import java.math.BigInteger
import java.security.MessageDigest

class Assignment4 : Assignment() {

    override fun getInput(): String {
        return "input_4"
    }

    fun md5(input: String): String {
        val md = MessageDigest.getInstance("MD5")
        return BigInteger(1, md.digest(input.toByteArray())).toString(16).padStart(32, '0')
    }

    private lateinit var input: String

    override fun initialize(input: List<String>) {
        this.input = input[0]
    }

    private fun getHashNumberStartingWith(input: String, prefix: String): Int {
        var number = 0
        while (true) {
            val hash = md5("$input$number")
            if (hash.startsWith(prefix)) {
                break
            }
            number++
        }
        return number
    }

    override fun calculateSolutionA(): String =
        getHashNumberStartingWith(input, "00000")
            .toString()

    override fun calculateSolutionB(): String =
        getHashNumberStartingWith(input, "000000")
            .toString()
}
