package toolkit

data class Vector2D(var x: Int, var y: Int) {
    operator fun plus(vector: Vector2D): Vector2D {
        return Vector2D(x + vector.x, y + vector.y)
    }

    operator fun minus(vector: Vector2D): Vector2D {
        return Vector2D(x - vector.x, y - vector.y)
    }

    override fun toString(): String {
        return "($x, $y)"
    }

    fun up() = Vector2D(x - 1, y)
    fun down() = Vector2D(x + 1, y)
    fun right() = Vector2D(x, y + 1)
    fun left() = Vector2D(x, y - 1)
}

data class Vector2DLong(var x: Long, var y: Long) {
    operator fun plus(vector: Vector2DLong): Vector2DLong {
        return Vector2DLong(x + vector.x, y + vector.y)
    }

    operator fun minus(vector: Vector2DLong): Vector2DLong {
        return Vector2DLong(x - vector.x, y - vector.y)
    }
}
