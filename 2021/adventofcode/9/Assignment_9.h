#pragma once

#include "Assignment.h"

class Assignment_9 : public Assignment
{
private:
    struct Coordinate
    {
        int x;
        int y;
        int value;

        bool visited = false;

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

        return neighbors;
    }

    bool isLowPoint(const std::vector<std::vector<Coordinate*>>& map, const Coordinate* coordinate)
    {
        std::vector<Coordinate*> neighbors = getNeighbors(map, coordinate);

        for (const auto& neighbor : neighbors)
        {
            if (neighbor->value <= coordinate->value)
            {
                return false;
            }
        }

        return true;
    }

    std::vector<Coordinate*> getLocalLowPoints(const std::vector<std::vector<Coordinate*>>& map)
    {
        std::vector<Coordinate*> coordinates;

        //for each point, check the neighbors
        for (int i = 0; i < map.size(); i++)
        {
            for (int j = 0; j < map[i].size(); j++)
            {
                if (isLowPoint(map, map[i][j]))
                {
                    coordinates.push_back(map[i][j]);
                }
            }
        }
        return coordinates;
    }

    int getTotalRiskValue(const std::vector<std::vector<Coordinate*>>& map)
    {
        int total = 0;

        std::vector<Coordinate*> coordinates = getLocalLowPoints(map);

        for (const auto& coordinate : coordinates)
        {
            total += (coordinate->value + 1);
        }

        return total;
    }

    int getBasinSize(std::vector<std::vector<Coordinate*>> map, Coordinate* start)
    {
        //perform breadth first search 

        std::vector<Coordinate*> basin;

        //use a queue to store all nodes that need to be visited
        //while queue not empty, process first values in the queue
        std::vector<Coordinate*> queue;

        //add the start coordinate to the queue
        queue.push_back(start);

        while (queue.size() > 0)
        {
            Coordinate* activeCoordinate = queue[0];

            //remove the value from the queue
            queue.erase(queue.begin());

            //retrieve the neighbors and check which ones are suitable
            std::vector<Coordinate*> neighbors = getNeighbors(map, activeCoordinate);

            for (int i = 0; i < neighbors.size(); i++)
            {
                if (neighbors[i]->visited) continue;

                //don't add neighbor to the basin collection if:
                //- the neighbor is already visited
                //- the neighbor value is lower than the current one
                //- the value is bigger or equal to 9
                //- the neighbor is not already in the queue
                if (neighbors[i]->visited) continue;
                if (neighbors[i]->value < activeCoordinate->value) continue;
                if (neighbors[i]->value >= 9) continue;
                if (std::find(queue.begin(), queue.end(), neighbors[i]) != queue.end()) continue;

                queue.push_back(neighbors[i]);
            }

            //label the current node as visited
            activeCoordinate->visited = true;

            //add the node to the basin
            basin.push_back(activeCoordinate);
        }

        return basin.size();
    }

    std::vector<int> getSortedBasinSizes(const std::vector<std::vector<Coordinate*>>& map)
    {
        std::vector<int> basinSizes;

        //find all low points as starting points
        std::vector<Coordinate*> coordinates = getLocalLowPoints(map);

        int largestSize = INT_MIN;

        for (int i = 0; i < coordinates.size(); i++)
        {
            int basinSize = getBasinSize(map, coordinates[i]);
            basinSizes.push_back(basinSize);
        }

        //sort, so we can easily pick the three largest ones
        std::sort(basinSizes.begin(), basinSizes.end());

        return basinSizes;
    }

    int getMultipliedLastItems(const std::vector<int>& items, int count)
    {
        int total = 1;
        for (int i = items.size() - count; i < items.size(); i++)
        {
            total *= items[i];
        }
        return total;
    }

    std::string getSolutionPart1()
    {
        int result = getTotalRiskValue(data);

        return std::to_string(result);
    }

    std::string getSolutionPart2()
    {
        std::vector<int> largestBasinSizes = getSortedBasinSizes(data);
        int result = getMultipliedLastItems(largestBasinSizes, 3);

        return std::to_string(result);
    }

    std::vector<std::vector<Coordinate*>> data;
public:
    void initialize(const std::vector<std::string>& input)
    {
        data = parseInput(input);
    }

    std::string getInput()
    {
        return "9/input";
    }
};