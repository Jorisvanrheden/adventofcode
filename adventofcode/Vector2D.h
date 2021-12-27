#pragma once
struct Vector2D
{
public:
    int x;
    int y;

    Vector2D() {}
    Vector2D(int x, int y)
        : x(x), y(y)
    {
    }
};

struct Area
{
public:
    Vector2D min;
    Vector2D max;

    Area() {}
    Area(const Vector2D& min, const Vector2D& max)
        : min(min), max(max)
    {

    }

    bool containsPoint(const Vector2D& point) const
    {
        if (point.x < min.x) return false;
        if (point.x > max.x) return false;

        if (point.y < min.y) return false;
        if (point.y > max.y) return false;

        return true;
    }

    int getHeight() const
    {
        return max.y - min.y;
    }

    int getWidth() const
    {
        return max.x - min.x;
    }
};