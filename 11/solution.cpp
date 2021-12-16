#include <iostream>
#include <fstream>
#include <vector>
#include <algorithm>
#include <string>
#include <limits.h>
#include <map>

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

std::vector<std::string> substring(const std::string& line, const std::string& delimiter)
{
    std::string mutableLine = std::string(line);

    std::vector<std::string> substrings;

    size_t position = 0;

    //while another instance of the delimiter has been found
    while (mutableLine.find(delimiter) != std::string::npos)
    {
        position = mutableLine.find(delimiter);

        std::string token = mutableLine.substr(0, position);

        if (!token.empty())
        {
            substrings.push_back(token);
        }

        mutableLine.erase(0, position + delimiter.length());
    }

    substrings.push_back(mutableLine);

    return substrings;
}

std::vector<std::vector<Coordinate*>> parseInput(const std::string& filepath)
{
    std::vector<std::vector<Coordinate*>> input;

    std::ifstream file(filepath);

    std::string line;
    while (std::getline(file, line))
    {
        std::vector<Coordinate*> row;

        for (int i = 0; i < line.length(); i++)
        {
            int value = line[i] - '0';

            row.push_back(new Coordinate(input.size(), i, value));
        }

        input.push_back(row);
    }

    return input;
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

int main()
{
    std::vector<std::vector<Coordinate*>> input = parseInput("./input");

    std::cout<<input.size()<<std::endl;

    //solution A
    int result = 0;

    //solution B
    //int result = 0;

    std::cout << "--- ANSWER IS ---" << std::endl;
    std::cout << result << std::endl;

    return 0;
}