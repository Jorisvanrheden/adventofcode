package toolkit

data class Vector3D(var x: Int, var y: Int, var z: Int) {
    operator fun plus(vector: Vector3D): Vector3D {
        return Vector3D(x + vector.x, y + vector.y, z + vector.z)
    }

    operator fun minus(vector: Vector3D): Vector3D {
        return Vector3D(x - vector.x, y - vector.y, z - vector.z)
    }
}
