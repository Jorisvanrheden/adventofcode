#pragma once

#include "Assignment.h"
#include <algorithm>
#include <map>

class Assignment_17 : public Assignment
{
private:
    struct Object 
    {
        Vector2D position;
        Vector2D velocity;

        Object(const Vector2D& position, const Vector2D velocity)
            : position(position), velocity(velocity)
        {
            
        }
    };

    void getRangeValues(const std::string& input, int& min, int& max) 
    {
        std::vector<std::string> parts = Utilities::splitString(input, "..");

        min = std::stoi(parts[0]);
        max = std::stoi(parts[1]);
    }

    Area parseInput(const std::vector<std::string>& input) 
    {
        int minX, maxX;
        int minY, maxY;

        std::vector<std::string> parts = Utilities::splitString(input[0], ": ");

        std::vector<std::string> coordinates = Utilities::splitString(parts[1], ", ");

        std::vector<std::string> xParts = Utilities::splitString(coordinates[0], "=");
        std::vector<std::string> yParts = Utilities::splitString(coordinates[1], "=");

        getRangeValues(xParts[1], minX, maxX);
        getRangeValues(yParts[1], minY, maxY);

        return Area(Vector2D(minX, minY), Vector2D(maxX, maxY));
    }

    void processStep(Object& object)
    {
        //apply velocity to position
        object.position.x += object.velocity.x;
        object.position.y += object.velocity.y;

        //apply drag to velocity
        if (object.velocity.x != 0)
        {
            if (object.velocity.x > 0) object.velocity.x--;
            if (object.velocity.x < 0) object.velocity.x++;
        }     

        //because of gravity, y-velocity decreases every step
        object.velocity.y -= 1;
    }

    int getYTop(const Vector2D& position, const Vector2D& velocity, const Area& area) 
    {
        Object object(position, velocity);

        int maxY = 0;

        while (true) 
        {
            processStep(object);

            //out of bounds
            if (object.position.y < area.min.y - area.getHeight()) 
            {
                maxY = -1;
                break;
            }

            if (object.position.y > maxY) maxY = object.position.y;

            //stop searching if the y coordinate is in limits of the area
            if (object.position.y >= area.min.y &&
                object.position.y <= area.max.y)
            {
                break;
            }
        }

        return maxY;
    }

    bool isValidXVelocity(const Vector2D& velocity, const Area& area)
    {
        Object object(Vector2D(0, 0), velocity);

        while (true) 
        {
            //for each initialized coordinate, check if after a specific step the object falls in the bounds
            //of the area
            processStep(object);

            //if x-velocity is 0 and the position is left of the area, it is invalid
            //if x-position at any given moment is right of the area, it is invalid
            if (object.velocity.x == 0 && object.position.x < area.min.x) break;
            if (object.position.x > area.max.x) break;

            //check if the x coordinate is in bounds
            if (object.position.x >= area.min.x &&
                object.position.x <= area.max.x) return true;
        }

        return false;
    }

    bool isValidVelocity(const Vector2D& velocity, const Area& area) 
    {
        Object object(Vector2D(0, 0), velocity);

        while (true)
        {
            //for each initialized coordinate, check if after a specific step the object falls in the bounds
            //of the area
            processStep(object);

            //check if the x coordinate is in bounds
            if (area.containsPoint(object.position)) return true;

            //velocity + position validations
            if (object.velocity.x == 0 && object.position.x < area.min.x) break;
            if (object.position.x > area.max.x) break;
            if (object.position.y < area.min.y) break;
        }

        return false;
    }

    std::vector<int> getPossibleXCoordinates(const Area& area) 
    {
        //there is a limited amount of x-coordinates that can be used a start
        //if too small, the target won't be reached
        //if too large, the target will be overshot
        std::vector<int> validXVelocities;

        int range = area.max.x;
        for (int i = 0; i <= range; i++) 
        {
            Vector2D velocity(i, 0);

            if (isValidXVelocity(velocity, area)) 
            {
                validXVelocities.push_back(i);
            }
        }

        return validXVelocities;
    }

    std::string getSolutionPart1()
    {
        int highestYCoordinate = -1;
        for (int i = 0; i < 100; i++) 
        {
           int y = getYTop(Vector2D(0,0), Vector2D(0, i), data);
           if (y > highestYCoordinate) 
           {
               highestYCoordinate = y;

               std::cout << i << std::endl;
           }
        }

        int result = highestYCoordinate;

        return std::to_string(result);
    }

    std::string getSolutionPart2()
    {
        std::vector<int> xCoordinates = getPossibleXCoordinates(data);
        std::vector<Vector2D> velocities;

        for (const auto& x : xCoordinates) 
        {
            for (int i = -100; i < 100; i++) 
            {
                Vector2D velocity(x, i);
                if (isValidVelocity(velocity, data)) 
                {
                    velocities.push_back(velocity);
                }
            }
        }

        int result = velocities.size();

        return std::to_string(result);
    }

    Area data;

public:
    void initialize()
    {
        std::vector<std::string> input = Utilities::readFile("../17/input");
        data = parseInput(input);
    }
};