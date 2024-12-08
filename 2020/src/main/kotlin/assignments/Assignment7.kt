package assignments

import models.assignment.Assignment

class Assignment7 : Assignment(7) {

    data class Bag(val name: String, val count: Int) {
        override fun equals(other: Any?): Boolean {
            return (other as Bag).name == name
        }
        override fun hashCode(): Int {
            return name.hashCode()
        }
    }

    private lateinit var dictionary: MutableMap<Bag, List<Bag>>

    override fun initialize(input: List<String>) {
        // parse all key/value pairs

        // in the end that means that there will be one big dictionary with:
        // (key, value) = [bag, bags]
        val m: MutableMap<Bag, List<Bag>> = mutableMapOf()

        // pass the map during processing of each line, as we want to populate the map
        for (x in input) {
            processLine(m, x)
        }

        dictionary = m
        // for solution A, we don't need to know the amount for each bag, but it might be necessary for solution B
    }

    private fun processLine(m: MutableMap<Bag, List<Bag>>, line: String) {
        val keyValuePair = line.split(" contain ")

        // construct key and value
        val bag:Bag = parseBag(keyValuePair[0])
        val bags:List<Bag> = parseBags(keyValuePair[1])

        if(!m.containsKey(bag)){
            m[bag] = bags
        }
    }

    private fun parseBag(input: String): Bag {
        // 2 clear olive bags.
        // bright aqua bags
        val chunks = input.split(" ")

        if(chunks[0].count() == 1){
            val amount = chunks[0].toInt()
            val description = chunks[1] + chunks[2]
            return Bag(description, amount)
        }
        else{
            val description = chunks[0] + chunks[1]
            return Bag(description, 1)
        }
    }

    private fun parseBags(input: String): List<Bag> {
        val chunks = input.split(", ")
        return chunks.map { parseBag(it) }
    }

    private fun containsBag(bag: Bag, m: MutableMap<Bag, List<Bag>>, nameToFind:String): Boolean {
        if(bag.name == nameToFind) return true

        // you need recursion here
        if(m.containsKey(bag)){
            val keys = m[bag]
            if (keys != null) {
                for(x in keys){
                    if(containsBag(x, m, nameToFind)) return true
                }
            }
        }
        return false
    }

    private fun getTotalCount(bag: Bag, m: MutableMap<Bag, List<Bag>>, nameToFind:String): Int {
        var total = 0

        if(m.containsKey(bag)){
            if(bag.name != nameToFind){
                total += bag.count
            }

            val values = m[bag]
            if(values!=null){
                for(x in values){
                    total += getTotalCount(x, m, nameToFind) * bag.count
                }
            }
        }
        return total
    }

    override fun calculateSolutionA(): String {
        // go through each key, and track through the map to find if one of the sub keys is the shiny bag
        val nameToFind = "shinygold"

        return dictionary.count { it.key.name != nameToFind  && containsBag(it.key, dictionary, nameToFind) }.toString()
    }

    override fun calculateSolutionB(): String {
        // go through each key, and track through the map to find if one of the sub keys is the shiny bag
        val nameToFind = "shinygold"

        return getTotalCount(Bag(nameToFind, 1), dictionary, nameToFind).toString()
    }
}