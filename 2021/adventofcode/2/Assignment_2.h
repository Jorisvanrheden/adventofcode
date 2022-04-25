#pragma once

#include "Assignment.h"

class Assignment_2 : public Assignment
{
private:
    //Options are:
    //- forward (horizontal position)
    //- up (vertical position)
    //- down (vertical position)
    struct Direction
    {
        int x;
        int y;
        int magnitude;

        Direction()
        {
            x = 0;
            y = 0;
            magnitude = 0;
        }
    };

    std::vector<Direction*> parseInput(const std::vector<std::string>& input)
    {
        std::vector<Direction*> data;

        for (const auto& entry : input)
        {
            std::vector<std::string> substrings = Utilities::splitString(entry, " ");

            Direction* direction = createDirection(substrings);

            if (direction)
            {
                data.push_back(direction);
            }
        }

        return data;
    }

    bool parseDimension(Direction* direction, const std::string& dimension)
    {
        if (dimension == "forward")
        {
            direction->x = 1;
        }
        else if (dimension == "up")
        {
            direction->y = -1;
        }
        else if (dimension == "down")
        {
            direction->y = 1;
        }
        else
        {
            return false;
        }

        return true;
    }

    bool parseMagnitude(Direction* direction, const std::string& magnitude)
    {
        try
        {
            int value = std::stoi(magnitude);
            direction->magnitude = value;

            return true;
        }
        catch (std::exception& e)
        {
            return false;
        }
    }

    Direction* createDirection(const std::vector<std::string>& substrings)
    {
        //validate that the input string has 2 space-delimited substrings
        if (substrings.size() != 2) return NULL;

        Direction* direction = new Direction();

        if (!parseDimension(direction, substrings[0])) return NULL;
        if (!parseMagnitude(direction, substrings[1])) return NULL;

        return direction;
    }

    int computeTotalDistance(const std::vector<Direction*>& directions)
    {
        int distanceHorizontal = 0;
        int distanceVertical = 0;

        for (const auto& direction : directions)
        {
            distanceHorizontal += direction->x * direction->magnitude;
            distanceVertical += direction->y * direction->magnitude;
        }

        return distanceHorizontal * distanceVertical;
    }

    int computeTotalDistanceWithAim(const std::vector<Direction*>& directions)
    {
        int distance = 0;
        int depth = 0;
        int aim = 0;

        for (const auto& direction : directions)
        {
            aim += direction->y * direction->magnitude;
            distance += direction->x * direction->magnitude;

            //process depth only if the forward vector is valid
            if (direction->x != 0)
            {
                depth += aim * direction->magnitude;
            }
        }

        return distance * depth;
    }

    std::string getSolutionPart1()
    {
        int result = computeTotalDistance(data);

        return std::to_string(result);
    }

    std::string getSolutionPart2()
    {
        int result = computeTotalDistanceWithAim(data);

        return std::to_string(result);
    }

    std::vector<Direction*> data;

public:
    void initialize(const std::vector<std::string>& input)
    {
        data = parseInput(input);
    }

    std::string getInput()
    {
        return "2/input";
    }
};