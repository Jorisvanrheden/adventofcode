#pragma once

#include "Assignment.h"

class Assignment_11 : public Assignment
{
private:
    struct Coordinate
    {
        int x;
        int y;
        int value;

        bool flashed = false;

        Coordinate(int x, int y, int value)
            : x(x), y(y), value(value)
        {

        }
    };

    std::vector<std::vector<Coordinate*>> parseInput(const std::vector<std::string>& input)
    {
        std::vector<std::vector<Coordinate*>> data;

        for (const auto& entry : input)
        {
            std::vector<Coordinate*> row;

            for (int i = 0; i < entry.length(); i++)
            {
                int value = entry[i] - '0';

                row.push_back(new Coordinate(data.size(), i, value));
            }

            data.push_back(row);
        }

        return data;
    }

    bool isValidCoordinate(const std::vector<std::vector<Coordinate*>>& map, int x, int y)
    {
        int columns = map.size();
        int rows = map[0].size();

        if (x < 0) return false;
        if (y < 0) return false;

        if (x >= columns) return false;
        if (y >= rows) return false;

        return true;
    }

    std::vector<Coordinate*> getNeighbors(const std::vector<std::vector<Coordinate*>>& map, const Coordinate* coordinate)
    {
        std::vector<Coordinate*> neighbors;

        if (isValidCoordinate(map, coordinate->x + 1, coordinate->y)) neighbors.push_back(map[coordinate->x + 1][coordinate->y]); //right
        if (isValidCoordinate(map, coordinate->x - 1, coordinate->y)) neighbors.push_back(map[coordinate->x - 1][coordinate->y]); //left
        if (isValidCoordinate(map, coordinate->x, coordinate->y + 1)) neighbors.push_back(map[coordinate->x][coordinate->y + 1]); //top
        if (isValidCoordinate(map, coordinate->x, coordinate->y - 1)) neighbors.push_back(map[coordinate->x][coordinate->y - 1]); //bottom

        //diagonals
        if (isValidCoordinate(map, coordinate->x + 1, coordinate->y + 1)) neighbors.push_back(map[coordinate->x + 1][coordinate->y + 1]);
        if (isValidCoordinate(map, coordinate->x - 1, coordinate->y + 1)) neighbors.push_back(map[coordinate->x - 1][coordinate->y + 1]);
        if (isValidCoordinate(map, coordinate->x + 1, coordinate->y - 1)) neighbors.push_back(map[coordinate->x + 1][coordinate->y - 1]);
        if (isValidCoordinate(map, coordinate->x - 1, coordinate->y - 1)) neighbors.push_back(map[coordinate->x - 1][coordinate->y - 1]);

        return neighbors;
    }

    bool canFlash(Coordinate* coordinate)
    {
        if (coordinate->value <= 9) return false;
        if (coordinate->flashed) return false;

        return true;
    }

    int checkAndApplyFlash(const std::vector<std::vector<Coordinate*>>& map, Coordinate* coordinate)
    {
        int flashes = 1;

        //a flash sets the current value to 0
        coordinate->value = 0;
        coordinate->flashed = true;

        //for all surrounding values, increment the value by 1, but only if it hasn't flashed that step
        std::vector<Coordinate*> neighbors = getNeighbors(map, coordinate);
        for (int i = 0; i < neighbors.size(); i++)
        {
            //if the neighbor already flashed, it cannot increment the value
            if (!neighbors[i]->flashed)
            {
                neighbors[i]->value++;

                if (neighbors[i]->value > 9)
                {
                    flashes += checkAndApplyFlash(map, neighbors[i]);
                }
            }
        }

        return flashes;
    }

    int processStep(std::vector<std::vector<Coordinate*>>& map)
    {
        //increment all entries with 1
        for (int i = 0; i < map.size(); i++)
        {
            for (int j = 0; j < map[i].size(); j++)
            {
                //reset the flashed flag
                map[i][j]->flashed = false;

                map[i][j]->value++;
            }
        }

        int flashes = 0;

        //foreach entry with a value higher than 9, it should flash
        for (int i = 0; i < map.size(); i++)
        {
            for (int j = 0; j < map[i].size(); j++)
            {
                if (canFlash(map[i][j]))
                {
                    flashes += checkAndApplyFlash(map, map[i][j]);
                }
            }
        }

        return flashes;
    }

    void freeData(std::vector<std::vector<Coordinate*>> data) 
    {
        for (int i = 0; i < data.size(); i++) 
        {
            for (Coordinate* coordinate : data[i]) 
            {
                free(coordinate);
            }
        }
    }

    std::string getSolutionPart1()
    {
        std::vector<std::vector<Coordinate*>> data = parseInput(input);

        int totalFlashes = 0;
        for(int i=0;i<100;i++)
        {
            int flashes = processStep(data);
            totalFlashes += flashes;
        }

        int result = totalFlashes;

        freeData(data);

        return std::to_string(result);
    }

    std::string getSolutionPart2()
    {
        std::vector<std::vector<Coordinate*>> data = parseInput(input);

        int totalCoordinates = 0;
        for (int i = 0; i < data.size(); i++)
        {
            for (int j = 0; j < data[i].size(); j++)
            {
                totalCoordinates++;
            }
        }

        int iter = 0;
        while (true)
        {
            int flashes = processStep(data);

            if (flashes == totalCoordinates)
            {
                break;
            }

            iter++;
        }

        int result = iter;

        freeData(data);

        return std::to_string(result);
    }

    std::vector<std::string> input;
public:
    void initialize()
    {
        input = Utilities::readFile("../11/input");
    }
};