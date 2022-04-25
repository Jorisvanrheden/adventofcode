#pragma once

#include <vector>
#include <string>
#include <algorithm>
#include <map>
#include <unordered_map>
#include <math.h>
#include <iostream>
#include <stdint.h>

#include "Utilities.h"
#include "Vector2D.h"
#include "Vector3D.h"

class Assignment 
{
public:
    virtual void initialize(const std::vector<std::string>& input) = 0;
    virtual void run() 
    {
        std::string solution1 = getSolutionPart1();
        printSolution(1, solution1);

        std::string solution2 = getSolutionPart2();
        printSolution(2, solution2);
    }

    virtual std::string getInput() { return""; };

private:
    void printSolution(int part, const std::string& solution)
    {
        std::cout << "--- Solution Part " << part << " ---"<< std::endl;
        std::cout << solution << std::endl;
    }

    virtual std::string getSolutionPart1() = 0;
    virtual std::string getSolutionPart2() = 0;
};