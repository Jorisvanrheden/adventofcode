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

fun String.indicesOf(predicate: String): List<Int> {
    return (0..length - predicate.length)
        .filter { substring(it, it + predicate.length) == predicate }
}