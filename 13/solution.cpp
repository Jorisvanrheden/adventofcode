#include <iostream>
#include <fstream>
#include <vector>
#include <algorithm>
#include <string>
#include <limits.h>
#include <cctype>
#include <map>

struct Matrix 
{
    std::vector<std::vector<int>> values;

    int rows;
    int columns;

    Matrix(int rows, int columns) 
        : rows(rows), columns(columns)
    {
        values = std::vector<std::vector<int>>(rows);
        for (int i = 0; i < rows; i++) 
        {
            std::vector<int> row = std::vector<int>(columns, 0);
            values[i] = row;
        }
    }
};

struct Coordinate 
{
    int x;
    int y;

    Coordinate(int x, int y)
        : x(x), y(y)
    {
    }
};

struct Fold 
{
    std::string dimension;
    int value;

    Fold(const std::string& dimension, int value)
        : dimension(dimension), value(value)
    {
    }
};

struct Input 
{
    std::vector<Coordinate> coordinates;
    std::vector<Fold> folds;

    Input(const std::vector<Coordinate>& coordinates, const std::vector<Fold>& folds)
        : coordinates(coordinates), folds(folds)
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

Input parseInput(const std::string& filepath)
{
    std::vector<Coordinate> coordinates;
    std::vector<Fold> folds;

    std::ifstream file(filepath);

    const std::string FOLD_IDENTIFIER = "fold along ";

    std::string line;
    while (std::getline(file, line))
    {
        if (line.empty()) continue;

        size_t f = line.find(FOLD_IDENTIFIER);
        if (f != std::string::npos)
        {
            std::string input = line;
            input.erase(f, FOLD_IDENTIFIER.size());
            
            //now there should be a dimensions, '=' and a value
            std::vector<std::string> parts = substring(input, "=");

            if (parts.size() == 2)
            {
                int value = std::stoi(parts[1]);

                folds.push_back(Fold(parts[0], value));
            }
        }
        else 
        {
            //parse coordinate
            std::vector<std::string> parts = substring(line, ",");

            if (parts.size() == 2) 
            {
                int x = std::stoi(parts[0]);
                int y = std::stoi(parts[1]);

                coordinates.push_back(Coordinate(x, y));
            }
        }
    }

    return Input(coordinates, folds);
}

void getBounds(const std::vector<Coordinate>& coordinates, int& maxX, int& maxY) 
{
    maxX = INT_MIN;
    maxY = INT_MIN;

    for (const auto& coordinate : coordinates) 
    {
        if (coordinate.x > maxX) maxX = coordinate.x;
        if (coordinate.y > maxY) maxY = coordinate.y;
    }

    maxX++;
    maxY++;
}

void fold(Matrix& matrix, const Fold& fold) 
{
    //if over x, everything from the right comes to the left

    //if over y, everything from the bottom comes to the top

    if (fold.dimension == "y") 
    {
        //for each point on the right side of the 'line', calculate the distance
        //move the point that distance to the opposite side
        
        for (int i = 0; i < matrix.rows; i++) 
        {
            for (int j = fold.value; j < matrix.columns; j++)
            {
                //check all values, and move everything with a value higher than 0 

                int value = matrix.values[i][j];

                if (value > 0) 
                {
                    int offset = abs(j - fold.value);

                    //apply the value to the folded side
                    matrix.values[i][fold.value - offset] = value;

                    //clear the original value
                    matrix.values[i][j] = 0;
                }
            }
        }
        
    }
    else if (fold.dimension == "x") 
    {
        for (int i = 0; i < matrix.columns; i++)
        {
            for (int j = fold.value; j < matrix.rows; j++)
            {
                //check all values, and move everything with a value higher than 0 

                int value = matrix.values[j][i];

                if (value > 0)
                {
                    int offset = abs(j - fold.value);

                    //apply the value to the folded side
                    matrix.values[fold.value - offset][i] = value;

                    //clear the original value
                    matrix.values[j][i] = 0;
                }
            }
        }
    }
}

Matrix createMatrix(const std::vector<Coordinate>& coordinates)
{
    int maxX, maxY;
    getBounds(coordinates, maxX, maxY);

    Matrix matrix(maxX, maxY);

    for (const auto& coordinate : coordinates) 
    {
        matrix.values[coordinate.x][coordinate.y] = 1;
    }

    return matrix;
}

int getDotCount(const Matrix& matrix) 
{
    int count = 0;

    for (int i = 0; i < matrix.rows; i++) 
    {
        for (int j = 0; j < matrix.columns; j++) 
        {
            if (matrix.values[i][j] > 0) count++;
        }
    }

    return count;
}

void print(const Matrix& matrix) 
{
    std::cout << "PRINTING" << std::endl;
    for (int i = 0; i < matrix.columns; i++)
    {
        std::string row;

        for (int j = 0; j < matrix.rows; j++)
        {
            if (matrix.values[j][i] > 0) row += "#";
            else row += ".";
        }
        std::cout << row << std::endl;
    }
}

void write(const Matrix& matrix) 
{
    std::ofstream myfile;
    myfile.open("./output");

    for (int i = 0; i < matrix.columns; i++)
    {
        std::string row;

        for (int j = 0; j < matrix.rows; j++)
        {
            if (matrix.values[j][i] > 0) row += "#";
            else row += ".";
        }
        std::cout << row << std::endl;

        myfile << row << "\n";
    }

   
    myfile.close();
}

int main()
{
    auto input = parseInput("./input");

    Matrix matrix = createMatrix(input.coordinates);

    for (int i = 0; i < input.folds.size(); i++) 
    {
        fold(matrix, input.folds[i]);
    }

    write(matrix);
    print(matrix);
    
    //solution 
    int result = getDotCount(matrix);

    std::cout << "--- ANSWER IS ---" << std::endl;
    std::cout << result << std::endl;

    getchar();

    return 0;
}