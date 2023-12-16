package models

fun String.indicesOf(predicate: (Char) -> Boolean): List<Int> {
    val indicesThatMatchesPredicate = mutableListOf<Int>()
    for (i in indices) {
        if (predicate(this[i])) {
            indicesThatMatchesPredicate.add(i)
        }
    }
    return indicesThatMatchesPredicate
}