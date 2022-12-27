package assignments

class Assignment21 : Assignment() {

    override fun getInput(): String {
        return "input_21"
    }

    interface MathOperation {
        fun getResult(monkeys: List<Monkey>): Long
    }
    class StaticOperation(private val number: Long) : MathOperation {
        override fun getResult(monkeys: List<Monkey>): Long = number
    }
    class AddOperation(private val monkeyA: String, private val monkeyB: String) : MathOperation {
        override fun getResult(monkeys: List<Monkey>): Long =
            monkeys.first { it.name == monkeyA }.mathOperation!!.getResult(monkeys) +
            monkeys.first { it.name == monkeyB }.mathOperation!!.getResult(monkeys)
    }
    class SubtractOperation(private val monkeyA: String, private val monkeyB: String) : MathOperation {
        override fun getResult(monkeys: List<Monkey>): Long =
            monkeys.first { it.name == monkeyA }.mathOperation!!.getResult(monkeys) -
            monkeys.first { it.name == monkeyB }.mathOperation!!.getResult(monkeys)
    }
    class MultiplyOperation(private val monkeyA: String, private val monkeyB: String) : MathOperation {
        override fun getResult(monkeys: List<Monkey>): Long =
            monkeys.first { it.name == monkeyA }.mathOperation!!.getResult(monkeys) *
            monkeys.first { it.name == monkeyB }.mathOperation!!.getResult(monkeys)
    }
    class DivideOperation(private val monkeyA: String, private val monkeyB: String) : MathOperation {
        override fun getResult(monkeys: List<Monkey>): Long =
            monkeys.first { it.name == monkeyA }.mathOperation!!.getResult(monkeys) /
            monkeys.first { it.name == monkeyB }.mathOperation!!.getResult(monkeys)
    }

    data class Monkey(val name: String, var mathOperation: MathOperation?)

    private lateinit var monkeys: List<Monkey>

    private fun String.toMathOperation(monkeys: List<Monkey>): MathOperation? {
        // split on spaces
        var chunks = this.trim().split(' ')

        // if only 1 item, it's a static one
        if (chunks.size == 1) return StaticOperation(chunks[0].toLong())
        if (chunks.size == 3) {
            when (chunks[1]) {
                "+" -> return AddOperation(chunks[0], chunks[2])
                "-" -> return SubtractOperation(chunks[0], chunks[2])
                "/" -> return DivideOperation(chunks[0], chunks[2])
                "*" -> return MultiplyOperation(chunks[0], chunks[2])
            }
        }
        return null
    }

    private fun parseMonkey(input: String, monkeys: List<Monkey>): Monkey {
        val chunks = input.split(':')
        return Monkey(
            chunks[0],
            chunks[1].toMathOperation(monkeys)
        )
    }

    override fun initialize(input: List<String>) {
        var initMonkeys = input.map {
            Monkey(it.split(':')[0], null)
        }

        monkeys = input.map {
            parseMonkey(it, initMonkeys)
        }
    }

    override fun calculateSolutionA(): String {
        val monkey = monkeys.first { it.name == "root" }
        return monkey.mathOperation!!.getResult(monkeys).toString()
        return ""
    }

    override fun calculateSolutionB(): String {
        return ""
    }
}
