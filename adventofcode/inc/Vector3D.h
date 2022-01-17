#pragma once
struct Vector3D
{
public:
    int x;
    int y;
    int z;

    Vector3D() {}
    Vector3D(int x, int y, int z)
        : x(x), y(y), z(z)
    {
    }
};