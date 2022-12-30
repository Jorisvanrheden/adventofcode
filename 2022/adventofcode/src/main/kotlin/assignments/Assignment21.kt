package assignments

class Assignment21 : Assignment() {

    override fun getInput(): String {
        return "input_21"
    }

    interface MathOperation {
        fun getResult(monkeys: Map<String, Monkey>): Long
        fun getValues(monkeys: Map<String, Monkey>): List<Long> = emptyList()
    }
    class StaticOperation(private val number: Long) : MathOperation {
        override fun getResult(monkeys: Map<String, Monkey>): Long = number
    }
    class AddOperation(private val monkeyA: String, private val monkeyB: String) : MathOperation {
        override fun getResult(monkeys: Map<String, Monkey>): Long =
            monkeys[monkeyA]!!.mathOperation.getResult(monkeys) + monkeys[monkeyB]!!.mathOperation.getResult(monkeys)

        override fun getValues(monkeys: Map<String, Monkey>): List<Long> {
            val a = monkeys[monkeyA]!!.mathOperation.getResult(monkeys)
            val b = monkeys[monkeyB]!!.mathOperation.getResult(monkeys)
            return listOf(a, b)
        }
    }
    class SubtractOperation(private val monkeyA: String, private val monkeyB: String) : MathOperation {
        override fun getResult(monkeys: Map<String, Monkey>): Long =
            monkeys[monkeyA]!!.mathOperation.getResult(monkeys) - monkeys[monkeyB]!!.mathOperation.getResult(monkeys)
    }
    class MultiplyOperation(private val monkeyA: String, private val monkeyB: String) : MathOperation {
        override fun getResult(monkeys: Map<String, Monkey>): Long =
            monkeys[monkeyA]!!.mathOperation.getResult(monkeys) * monkeys[monkeyB]!!.mathOperation.getResult(monkeys)
    }
    class DivideOperation(private val monkeyA: String, private val monkeyB: String) : MathOperation {
        override fun getResult(monkeys: Map<String, Monkey>): Long =
            monkeys[monkeyA]!!.mathOperation.getResult(monkeys) / monkeys[monkeyB]!!.mathOperation.getResult(monkeys)
    }

    data class Monkey(val name: String, var mathOperation: MathOperation = StaticOperation(0))

    private lateinit var monkeys: Map<String, Monkey>

    private fun String.toMathOperation(monkeys: List<Monkey>): MathOperation {
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
        return StaticOperation(0)
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
            Monkey(it.split(':')[0])
        }

        monkeys = input.map {
            parseMonkey(it, initMonkeys)
        }.associateBy { it.name }
    }

    override fun calculateSolutionA(): String {
        return monkeys["root"]!!
            .mathOperation
            .getResult(monkeys)
            .toString()
    }

    override fun calculateSolutionB(): String {
        var yellMin = 0L
        var yellMax = Long.MAX_VALUE / 1000000
        var yellValue = 0L

        while (true) {
            yellValue = (yellMax + yellMin) / 2

            monkeys["humn"]!!.mathOperation = StaticOperation(yellValue)
            val values = monkeys["root"]!!.mathOperation.getValues(monkeys)

            if (values[0] < values[1]) {
                yellMax = yellValue
            } else if (values[0] > values[1]) {
                yellMin = yellValue
            } else if (values[0] == values[1]) {
                break
            }
        }
        return yellValue.toString()
    }
}
