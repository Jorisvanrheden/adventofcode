package assignments

import kotlin.math.abs

var shouldPrint = false
var lastDelta = 0L

class Assignment21 : Assignment() {

    override fun getInput(): String {
        return "input_21"
    }

    interface MathOperation {
        fun getResult(monkeys: Map<String, Monkey>): Long
        fun equals(monkeys: Map<String, Monkey>): Boolean = false
    }
    class StaticOperation(private val number: Long) : MathOperation {
        override fun getResult(monkeys: Map<String, Monkey>): Long = number
    }
    class AddOperation(private val monkeyA: String, private val monkeyB: String) : MathOperation {
        override fun getResult(monkeys: Map<String, Monkey>): Long =
            monkeys[monkeyA]!!.mathOperation.getResult(monkeys) + monkeys[monkeyB]!!.mathOperation.getResult(monkeys)

        override fun equals(monkeys: Map<String, Monkey>): Boolean {
            val a = monkeys[monkeyA]!!.mathOperation.getResult(monkeys)
            val b = monkeys[monkeyB]!!.mathOperation.getResult(monkeys)

            if (shouldPrint) {
                println("Comparing $a with $b")
                shouldPrint = false
            }
//            println("Comparing $a with $b")
            val delta = abs(a - b)
            println("Delta: ${abs(lastDelta - delta)}")
            lastDelta = delta

            return a == b
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
        var yellValue = 3759569744800L

        var its = 0

        // iterate yell values for 'humn', and check which one results in a passing evaluation for 'root'
        while (true) {
            if (its % 100000 == 0){
                shouldPrint = true
                println("another hundred")
            }
            its++
//            println(yellValue)
            monkeys["humn"]!!
                .mathOperation = StaticOperation(yellValue)

            if (monkeys["root"]!!.mathOperation.equals(monkeys)) {
                return yellValue.toString()
            }

            yellValue++
        }

        // each 2 units higher, diff is 28

        // per 100
        // a: 68634163965676
        // b: 68634163964266
        // diff = 1410

        // per 10000
        // a: 68634162566590
        // b: 68634161156222
        // diff = 1410368

        // total diff
        // 68634163976960
        // 15610303684582
        // 53023860292378

        // 37595762,44808305350093
        // 3759576200000,44808305350093

        return "No answer found"
    }
}
