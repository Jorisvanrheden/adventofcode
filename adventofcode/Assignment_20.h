#pragma once

#include "Assignment.h"
#include <algorithm>
#include <map>

class Assignment_20 : public Assignment
{
private:

    struct Input 
    {
        std::string algorithm;
        std::vector<std::vector<int>> pixels;

        Input() {}
        Input(const std::string& algorithm, const std::vector<std::vector<int>>& pixels)
            : algorithm(algorithm), pixels(pixels)
        {

        }
    };

    Input parseInput(const std::vector<std::string>& input)
    {
        std::string algorithm;
        std::vector<std::vector<int>> pixels;

        if (input.size() > 0) 
        {
            algorithm = input[0];
        }
        
        for (int i = 1; i < input.size(); i++)
        {
            if (input[i].empty()) continue;
           
            std::vector<int> row(input[i].size());
            for (int j = 0; j < input[i].size(); j++) 
            {
                if (input[i][j] == '#') row[j] = 1;
                else if (input[i][j] == '.') row[j] = 0; 
            }

            pixels.push_back(row);
        }

        return Input(algorithm, pixels);
    }

    std::string getSolutionPart1()
    {
        int result = 0;

        return std::to_string(result);
    }

    std::string getSolutionPart2()
    {
        int result = 0;

        return std::to_string(result);
    }

    Input data;

public:
    void initialize()
    {
        std::vector<std::string> input = Utilities::readFile("../20/input");
        data = parseInput(input);
    }
};