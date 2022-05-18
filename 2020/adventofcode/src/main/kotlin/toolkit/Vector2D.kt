package toolkit

data class Vector2D(var x:Int, var y:Int){
    operator fun plus(vector:Vector2D):Vector2D{
        return Vector2D(x + vector.x, y + vector.y)
    }

    operator fun minus(vector:Vector2D):Vector2D{
        return Vector2D(x - vector.x, y - vector.y)
    }
}