#include <iostream>
#include <fstream>
#include <vector>
#include <algorithm>
#include <string.h>
#include <limits.h>

struct Vector2D
{
    int x;
    int y;

    Vector2D(int x, int y)
        : x(x), y(y)
    {

    }
};

struct Line
{
    Vector2D from;
    Vector2D to; 

    Line(Vector2D from, Vector2D to)
        : from(from), to(to)
    {

    }
};

struct Input
{
    std::vector<Line> lines;

    Input(const std::vector<Line>& lines)
        : lines(lines)
    {

    }
};

std::vector<std::string> substring(const std::string& line, const std::string& delimiter)
{
    std::string mutableLine = std::string(line);

    std::vector<std::string> substrings;

    size_t position = 0;

    //while another instance of the delimiter has been found
    while(mutableLine.find(delimiter) != std::string::npos)
    {
        position = mutableLine.find(delimiter);

        std::string token = mutableLine.substr(0, position);

        if(!token.empty())
        {
            substrings.push_back(token);
        }

        mutableLine.erase(0, position + delimiter.length());
    }

    substrings.push_back(mutableLine);

    return substrings;
}

Vector2D createVector(std::vector<std::string> input)
{
    if(!input.size() == 2) 
    {
        throw std::invalid_argument("incorrect input size");
    }

    int x = std::stoi(input[0]);
    int y = std::stoi(input[1]);
    return Vector2D(x,y);
}

Line parseInputLine(const std::string& inputLine)
{
    std::vector<std::string> parts = substring(inputLine, " -> ");

    if(parts.size() != 2)
    {
        throw std::invalid_argument("incorrect input size");
    }
    
    Vector2D from = createVector(substring(parts[0], ","));
    Vector2D to   = createVector(substring(parts[1], ","));
    return Line(from, to);
}   

std::vector<Line> parseInput(const std::string& filepath)
{
    std::vector<Line> lines;

    std::ifstream file(filepath);

    std::string line;
    while(std::getline(file, line))
    {
        lines.push_back(parseInputLine(line));
    }

    return lines;
}

void findDataDimensions(const std::vector<Line>& lines, int& maxX, int& maxY)
{
    maxX = INT32_MIN;
    maxY = INT32_MIN;

    for(const auto& line : lines)
    {
        if(line.from.x > maxX) maxX = line.from.x;
        if(line.from.y > maxY) maxY = line.from.y;

        if(line.to.x > maxX) maxX = line.to.x;
        if(line.to.y > maxY) maxY = line.to.y;
    }

    maxX += 1;
    maxY += 1;
}

std::vector<std::vector<int>> createMatrix(int rows, int columns)
{
    std::vector<std::vector<int>> matrix(rows);

    for(int i=0;i<rows;i++)
    {
        std::vector<int> row(columns, 0);
        matrix[i] = row;
    }

    return matrix;
}

//find all coordinates of a line
std::vector<Vector2D> lineToCoordinates(const Line& line)
{
    std::vector<Vector2D> coordinates;

    int xDir = 0;
    int yDir = 0;

    int xDiff = line.to.x - line.from.x;
    int yDiff = line.to.y - line.from.y;

    if(xDiff != 0)
    {
        if(xDiff > 0) xDir = 1;
        else xDir = -1;
    }

    if(yDiff != 0)
    {
        if(yDiff > 0) yDir = 1;
        else yDir = -1;
    }

    int xDiffAbs = abs(xDiff) + 1;
    int yDiffAbs = abs(yDiff) + 1;

    if(xDir != 0 && yDir != 0) 
    {
        //diagonals travel the distance horizontal and vertical
        //therefore both xDiffAbs or yDiffAbs can be used
        for(int i=0;i<xDiffAbs;i++)
        {
            int x = line.from.x + i*xDir;
            int y = line.from.y + i*yDir;

            coordinates.push_back(Vector2D(x,y));
        }
    }
    else
    {
        //loop through abs value of the diff
        //multiply index with dir and add the from to it
        for(int i=0;i<xDiffAbs;i++)
        {
            int x = line.from.x + i*xDir;

            for(int j=0;j<yDiffAbs;j++)
            {
                int y = line.from.y + j*yDir;

                coordinates.push_back(Vector2D(x,y));
            }
        }
    }

    return coordinates;
}

void applyLinesToMatrix(const std::vector<Line>& lines, std::vector<std::vector<int>>& matrix)
{
    for(int i=0;i<lines.size();i++)
    {
        std::vector<Vector2D> coordinates = lineToCoordinates(lines[i]);

        for(const auto& coordinate : coordinates)
        {
            //increment matrix at coordinates to indicate it has visited
            matrix[coordinate.x][coordinate.y]++;
        }
    }
}

int getVisitedCountWithThreshold(const std::vector<std::vector<int>>& matrix, int threshold)
{
    int count = 0;

    for(int i=0;i<matrix.size();i++)
    {
        for(int j=0;j<matrix[i].size();j++)
        {
            if(matrix[i][j] >= threshold) count++;
        }
    }

    return count;
}

int main()
{
    std::vector<Line> input = parseInput("./input");

    int maxX = 0, maxY = 0;
    findDataDimensions(input, maxX, maxY);

    auto matrix = createMatrix(maxX, maxY);
    applyLinesToMatrix(input, matrix);

    int result = getVisitedCountWithThreshold(matrix, 2);
    std::cout<<"--- ANSWER IS ---"<<std::endl;
    std::cout<<result<<std::endl;

    return 0;
}