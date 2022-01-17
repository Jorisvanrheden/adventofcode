#pragma once

#include "Assignment.h"
#include <algorithm>
#include <map>

class Assignment_15 : public Assignment
{
private:
    struct Coordinate
    {
        int x;
        int y;
        int value;

        int fScore = INT_MAX;

        //store a link to construct a path
        Coordinate* link = NULL;

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

    int calculateHeuristic(const Coordinate* node, const Coordinate* target) 
    {
        return abs(target->x - node->x) + abs(target->y - node->y);
    }

    Coordinate* popNodeWithLowestFScore(std::vector<Coordinate*>& openList)
    {
        int lowestFScore = INT_MAX;
        int lowestFScoreIndex = 0;

        for (int i=0;i<openList.size();i++)
        {
            Coordinate* node = openList[i];

            if (node->fScore <= lowestFScore) 
            {
                lowestFScore = node->fScore;
                lowestFScoreIndex = i;
            }
        }

        Coordinate* lowestFScoreNode = openList[lowestFScoreIndex];

        openList.erase(openList.begin() + lowestFScoreIndex);

        return lowestFScoreNode;
    }

    std::vector<Coordinate*> constructPath(Coordinate* node) 
    {
        std::vector<Coordinate*> path;

        Coordinate* activeNode = node;

        while (activeNode != NULL) 
        {
            path.push_back(activeNode);

            activeNode = activeNode->link;
        }

        std::reverse(path.begin(), path.end());

        return path;
    }

    std::vector<Coordinate*> findBestPath(std::vector<std::vector<Coordinate*>>& map, Coordinate* start, Coordinate* target)
    {
        std::vector<Coordinate*> openList;
        std::vector<Coordinate*> closedList;

        openList.push_back(start);
        
        //initialize f scores for start
        start->fScore = start->value;

        while (openList.size() > 0)
        {
            Coordinate* activeNode = popNodeWithLowestFScore(openList);
            if (activeNode == NULL) 
            {
                std::cout << "Oops, something went wrong" << std::endl;
                break;
            }

            //push the item to the closed list so it won't be looked at again
            closedList.push_back(activeNode);

            if (activeNode == target) 
            {
                std::cout << "Path is found!" << std::endl;
                return constructPath(target);
            }

            //check neighbors
            std::vector<Coordinate*> neighbors = getNeighbors(map, activeNode);
            for (const auto& neighbor : neighbors) 
            {
                //check if the neighbor can be processed
                if (std::find(closedList.begin(), closedList.end(), neighbor) != closedList.end()) continue;

                int neighborFScore = activeNode->fScore + neighbor->value;

                if (neighborFScore < neighbor->fScore) 
                {
                    //set the connecting node
                    neighbor->link = activeNode;

                    //update neighbor scores
                    neighbor->fScore = neighborFScore;

                    //add the neighbor to the open list
                    openList.push_back(neighbor);
                }
            }
        }

        return {};
    }

    std::vector<std::vector<Coordinate*>> createFullMap(const std::vector<std::vector<Coordinate*>>& map)
    {
        const int MULTIPLIER = 5;

        std::vector<std::vector<Coordinate*>> fullMap(map.size() * MULTIPLIER);

        for (int i = 0; i < map.size(); i++) 
        {
            int columns = map[i].size();

            std::vector<Coordinate*> row(columns * MULTIPLIER);

            for (int j = 0; j < columns; j++)
            {
                for (int m = 0; m < MULTIPLIER; m++) 
                {
                    int x = i;
                    int y = j + columns * m;
                    int value = map[i][j]->value + m;
                    
                    if (value > 9) 
                    {
                        value = value % 9;
                    }

                    Coordinate* coordinate = new Coordinate(x, y, value);
                    row[j + columns * m] = coordinate;
                }
            }

            fullMap[i] = row;
        }

        for (int i = 0; i < map.size(); i++) 
        {
            int columns = map[i].size() * MULTIPLIER;

            for (int m = 1; m < MULTIPLIER; m++)
            {
                //create a new row
                std::vector<Coordinate*> row(columns);

                for (int j = 0; j < columns; j++)
                {
                    int x = i + map.size() * m;
                    int y = j;
                    int value = fullMap[i][j]->value + m;

                    if (value > 9)
                    {
                        value = value % 9;
                    }

                    Coordinate* coordinate = new Coordinate(x, y, value);

                    row[j] = coordinate;
                }

                //inject the row
                fullMap[i + map.size() * m] = row;
            }
        }

        return fullMap;
    }

    std::string getSolutionPart1()
    {
        Coordinate* start = data[0][0];
        Coordinate* target = data[data.size() - 1][data[0].size() -1];
        std::vector<Coordinate*> path = findBestPath(data, start, target);


        int totalCost = 0;
        for (int i = 1; i < path.size(); i++)
        {
            totalCost += path[i]->value;
        }

        int result = totalCost;

        return std::to_string(result);
    }

    std::string getSolutionPart2()
    {
        std::vector<std::vector<Coordinate*>> fullMap = createFullMap(data);

        Coordinate* start = fullMap[0][0];
        Coordinate* target = fullMap[fullMap.size() - 1][fullMap[0].size() - 1];
        std::vector<Coordinate*> path = findBestPath(fullMap, start, target);

        int totalCost = 0;
        for (int i = 1; i < path.size(); i++)
        {
            totalCost += path[i]->value;
        }

        int result = totalCost;

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
        return "15/input";
    }
};