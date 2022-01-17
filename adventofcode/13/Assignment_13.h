#pragma once

#include "Assignment.h"
#include <algorithm>
#include <map>

class Assignment_13 : public Assignment
{
private:
    struct Matrix
    {
        std::vector<std::vector<int>> values;

        int rows;
        int columns;

        Matrix() {}
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

    struct Fold
    {
        std::string dimension;
        int value;

        Fold() {}
        Fold(const std::string& dimension, int value)
            : dimension(dimension), value(value)
        {
        }
    };

    struct Input
    {
        std::vector<Vector2D> coordinates;
        std::vector<Fold> folds;

        Input() {}
        Input(const std::vector<Vector2D>& coordinates, const std::vector<Fold>& folds)
            : coordinates(coordinates), folds(folds)
        {
        }
    };

    Input parseInput(const std::vector<std::string>& input)
    {
        std::vector<Vector2D> coordinates;
        std::vector<Fold> folds;

        const std::string FOLD_IDENTIFIER = "fold along ";

        for(const auto& entry : input)
        {
            if (entry.empty()) continue;

            size_t f = entry.find(FOLD_IDENTIFIER);
            if (f != std::string::npos)
            {
                std::string input = entry;
                input.erase(f, FOLD_IDENTIFIER.size());

                //now there should be a dimensions, '=' and a value
                std::vector<std::string> parts = Utilities::splitString(input, "=");

                if (parts.size() == 2)
                {
                    int value = std::stoi(parts[1]);

                    folds.push_back(Fold(parts[0], value));
                }
            }
            else
            {
                //parse coordinate
                std::vector<std::string> parts = Utilities::splitString(entry, ",");

                if (parts.size() == 2)
                {
                    int x = std::stoi(parts[0]);
                    int y = std::stoi(parts[1]);

                    coordinates.push_back(Vector2D(x, y));
                }
            }
        }

        return Input(coordinates, folds);
    }

    void getBounds(const std::vector<Vector2D>& coordinates, int& maxX, int& maxY)
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

    Matrix createMatrix(const std::vector<Vector2D>& coordinates)
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

    void write(const Matrix& matrix)
    {
        std::ofstream myfile;
        myfile.open("../13/output");

        for (int i = 0; i < matrix.columns; i++)
        {
            std::string row;

            for (int j = 0; j < matrix.rows; j++)
            {
                if (matrix.values[j][i] > 0) row += "#";
                else row += ".";
            }
            myfile << row << "\n";
        }

        myfile.close();
    }

    std::string getSolutionPart1()
    {
        Matrix matrix = createMatrix(data.coordinates);

        for (int i = 0; i < data.folds.size() && i < 1; i++)
        {
            fold(matrix, data.folds[i]);
        }

        int result = getDotCount(matrix);

        return std::to_string(result);
    }

    std::string getSolutionPart2()
    {
        Matrix matrix = createMatrix(data.coordinates);

        for (int i = 0; i < data.folds.size(); i++)
        {
            fold(matrix, data.folds[i]);
        }

        write(matrix);

        return "See the output text file for the answer";
    }

    Input data;
public:
    void initialize(const std::vector<std::string>& input)
    {
        data = parseInput(input);
    }

    std::string getInput()
    {
        return "13/input";
    }
};