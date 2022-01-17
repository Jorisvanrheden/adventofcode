#pragma once

#include "Assignment.h"

class Assignment_23 : public Assignment
{
private:
    struct Map 
    {
        std::vector<int> hallway;

        //find the indices that have a room connected to them
        std::vector<int> roomIndices;

        std::vector<std::vector<int>> rooms;
    };


    std::string getSolutionPart1()
    {
        int result = 0;

        return std::to_string(result);
    }

    std::string getSolutionPart1()
    {
        int result = 0;

        return std::to_string(result);
    }

    std::vector<std::string> data;

public:
    void initialize(const std::vector<std::string>& input)
    {
        data = input;
    }

    std::string getInput() 
    {
        return "23/input";
    }
};