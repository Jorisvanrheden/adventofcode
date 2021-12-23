#pragma once

#include <vector>
#include <string>
#include <iostream>
#include "Utilities.h"

class Assignment 
{
public:
    virtual void run() 
    {
        initialize();

        std::string solution1 = getSolutionPart1();
        printSolution(1, solution1);

        std::string solution2 = getSolutionPart2();
        printSolution(2, solution2);
    }

private:
    void printSolution(int part, const std::string& solution)
    {
        std::cout << "--- Solution Part " << part << " ---"<< std::endl;
        std::cout << solution << std::endl;
    }

    virtual void initialize() = 0;

    virtual std::string getSolutionPart1() = 0;
    virtual std::string getSolutionPart2() = 0;
};