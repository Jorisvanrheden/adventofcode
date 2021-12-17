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

    bool flashed = false;

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

bool canFlash(Coordinate* coordinate)
{
    if(coordinate->value <= 9) return false;
    if(coordinate->flashed) return false;

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
    for(int i=0;i<neighbors.size();i++)
    {    
        //if the neighbor already flashed, it cannot increment the value
        if(!neighbors[i]->flashed)
        {
            neighbors[i]->value++;

            if(neighbors[i]->value > 9)
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
    for(int i=0;i<map.size();i++)
    {
        for(int j=0;j<map[i].size();j++)
        {
            //reset the flashed flag
            map[i][j]->flashed = false;

            map[i][j]->value++;
        }
    }

    int flashes = 0;

    //foreach entry with a value higher than 9, it should flash
    for(int i=0;i<map.size();i++)
    {
        for(int j=0;j<map[i].size();j++)
        {
            if(canFlash(map[i][j]))
            {
                flashes += checkAndApplyFlash(map, map[i][j]);
            }
        }
    }

    return flashes;
}

void printMap(const std::vector<std::vector<Coordinate*>>& map)
{
    for(int i=0;i<map.size();i++)
    {
        std::string row;
        for(int j=0;j<map[i].size();j++)
        {
            row += std::to_string(map[i][j]->value);
            row += ", ";
        }
        std::cout<<row<<std::endl;
    }
}

int main()
{
    std::vector<std::vector<Coordinate*>> input = parseInput("./input");

    int totalCoordinates = 0;
    for(int i=0;i<input.size();i++)
    {
        for(int j=0;j<input[i].size();j++)
        {
            totalCoordinates++;
        }
    }



    // int totalFlashes = 0;
    // for(int i=0;i<100;i++)
    // {
    //     printMap(input);

    //     int flashes = processStep(input);
    //     totalFlashes += flashes;


    //     std::cout<<"Total flashes after index " << i<<" is: "<< totalFlashes<<std::endl;
    // }


    //solution A
    //int result = 0;



    int iter = 0;
    while(true)
    {
        int flashes = processStep(input);

        if(flashes == totalCoordinates)
        {
            break;
        }

        iter++;
    }

    //solution B
    int result = iter;

    std::cout << "--- ANSWER IS ---" << std::endl;
    std::cout << result << std::endl;

    getchar();

    return 0;
}