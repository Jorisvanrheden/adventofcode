package assignments

import utilities.Utilities

class Assignment4 : Assignment() {
    data class Passport(val map: Map<String, String>)

    private lateinit var passports: List<Passport>
    private val properties: List<String> = listOf("byr", "iyr", "eyr", "hgt", "hcl", "ecl", "pid", "cid")

    override fun getInput(): String {
        return "input_4"
    }

    override fun initialize(input: List<String>) {
        var passports: MutableList<Passport> = mutableListOf()

        var chunks = Utilities.packageByEmptyLine(input)

        for (x in chunks) {

            var map: MutableMap<String, String> = mutableMapOf()

            val entry: String = x.joinToString { it }.replace(",", "")
            val chunks: List<String> = entry.split(" ")

            for (chunk in chunks) {
                val pair = chunk.split(":")
                map[pair[0]] = pair[1]
            }

            passports.add(Passport(map))
        }

        this.passports = passports
    }

    private fun containsRequiredProperties(passport: Passport): Boolean {
        // check if each passport contains all properties
        for (x in properties) {
            if (x == "cid") continue
            if (!passport.map.containsKey(x)) return false
        }
        return true
    }

    private fun isValidYear(year: String, min: Int, max: Int): Boolean {
        if (year.count() != 4) return false

        val value = year.toInt()
        return (value in min..max)
    }

    private fun isValidHeight(height: String): Boolean {
        if ("in" in height) {
            return height.replace("in", "").toInt() in 59..76
        }
        if ("cm" in height) {
            return height.replace("cm", "").toInt() in 150..193
        }

        return false
    }

    private fun isValidHairColor(hairColor: String): Boolean {
        if (hairColor[0] != '#') return false

        var color: String = hairColor.replace("#", "")
        for (c in color) {
            if (!c.isDigit()) {
                return c - 'a' in 0..5
            }
        }

        return true
    }

    private fun isValidEyeColor(eyeColor: String): Boolean {
        val colors: List<String> = listOf("amb", "blu", "brn", "gry", "grn", "hzl", "oth")
        return colors.contains(eyeColor)
    }

    private fun isValidPassportID(id: String): Boolean {
        return (id.count() == 9)
    }

    private fun hasValidProperties(passport: Passport): Boolean {
        for (x in passport.map) {
            when (x.key) {
                "byr" -> if (!isValidYear(x.value, 1920, 2002)) return false
                "iyr" -> if (!isValidYear(x.value, 2010, 2020)) return false
                "eyr" -> if (!isValidYear(x.value, 2020, 2030)) return false
                "hgt" -> if (!isValidHeight(x.value)) return false
                "hcl" -> if (!isValidHairColor(x.value)) return false
                "ecl" -> if (!isValidEyeColor(x.value)) return false
                "pid" -> if (!isValidPassportID(x.value)) return false
                else -> continue
            }
        }
        return true
    }

    override fun calculateSolutionA(): String =
        passports.filter { containsRequiredProperties(it) }.size.toString()

    override fun calculateSolutionB(): String =
        passports.filter { containsRequiredProperties(it) && hasValidProperties(it) }.size.toString()
}
