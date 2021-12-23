#pragma once

#include "Assignment.h"

class Assignment_1 : public Assignment
{
private:
    std::vector<int> parseInput(const std::vector<std::string>& input)
    {
        std::vector<int> output;

        for (const auto& entry : input) 
        {
            try
            {
                int value = std::stoi(entry);
                output.push_back(value);
            }
            catch (std::exception& e)
            {
                continue;
            }
        }

        return output;
    }

    int getChunkSum(const std::vector<int>& input, int index, int chunkSize)
    {
        int sum = 0;

        for (int i = 0; i < chunkSize; i++)
        {
            sum += input[index - i];
        }

        return sum;
    }

    //Combined solution
    int findIncreasingMeasurements(const std::vector<int>& input, int chunkSize)
    {
        int counter = 0;

        //start from index chunkSize, as indices below that cannot form a chunk together
        for (int i = chunkSize; i < input.size(); i++)
        {
            int sumCurrentChunk = getChunkSum(input, i, chunkSize);
            int sumPreviousChunk = getChunkSum(input, i - 1, chunkSize);

            if (sumCurrentChunk > sumPreviousChunk) counter++;
        }

        return counter;
    }

    std::string getSolutionPart1() 
    {
        int result = findIncreasingMeasurements(data, 1);

        return std::to_string(result);
    }

    std::string getSolutionPart2() 
    {
        int result = findIncreasingMeasurements(data, 3);

        return std::to_string(result);
    }

    std::vector<int> data;
public:
    void initialize()
    {
        std::vector<std::string> input = Utilities::readFile("../1/input");
        data = parseInput(input);
    }
};