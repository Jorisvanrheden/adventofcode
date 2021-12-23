#pragma once

#include "Assignment.h"

class Assignment_6 : public Assignment
{
private:
    std::vector<int> parseInput(const std::vector<std::string>& input)
    {
        std::vector<int> data;

        for (const auto& entry : input)
        {
            std::vector<std::string> parts = Utilities::splitString(entry, ",");
            for (int i = 0; i < parts.size(); i++)
            {
                try
                {
                    int value = std::stoi(parts[i]);
                    data.push_back(value);
                }
                catch (std::exception& e) {}
            }
        }

        return data;
    }

    unsigned long long process(std::vector<int>& input, int days)
    {
        //keep track of the amount of fish with 0-8 days
        //a simple array suffice (keep long long though for large amounts)

        std::vector<unsigned long long> tracker(9, 0);

        //initialize the tracker with the original input
        for (int i = 0; i < input.size(); i++)
        {
            tracker[input[i]]++;
        }

        for (int i = 0; i < days; i++)
        {
            //Day 0    
            //Original array: 3,4,3,1,2
            //New format:
            // 0, 1, 2, 3, 4, 5, 6, 7, 0
            // 0, 1, 1, 2, 1, 0, 0, 0, 0

            //Day 1    
            //Original array: 2,3,2,0,1
            //New format:
            // 0, 1, 2, 3, 4, 5, 6, 7, 8
            // 1, 1, 2, 1, 0, 0, 0, 0, 0

            //Day 2    
            //Original array: 1,2,1,6,0,8
            //New format:
            // 0, 1, 2, 3, 4, 5, 6, 7, 8
            // 1, 2, 1, 0, 0, 0, 1, 0, 1

            //cache the original zero value
            unsigned  long long originalZeroValue = tracker[0];

            for (int j = 0; j < tracker.size(); j++)
            {
                //set the active tracker value to the following one in the sequence
                if (j < tracker.size() - 1)
                {
                    unsigned long long nextValue = tracker[j + 1];

                    //set the active index to the following value
                    tracker[j] = nextValue;
                }
                //last element in the sequence doesn't have a following one, so it should be set to 0
                else if (j == tracker.size() - 1)
                {
                    tracker[j] = 0;
                }
            }

            //check if the previous day should have fish spawned on the next day
            if (originalZeroValue > 0)
            {
                //instead, increment values of index 8 and 6
                tracker[8] += originalZeroValue;
                tracker[6] += originalZeroValue;
            }
        }

        unsigned long long total = 0;
        for (int i = 0; i < tracker.size(); i++)
        {
            total += tracker[i];
        }
        return total;
    }

    std::string getSolutionPart1()
    {
        unsigned long long result = process(data, 80);

        return std::to_string(result);
    }

    std::string getSolutionPart2()
    {
        unsigned long long result = process(data, 256);

        return std::to_string(result);
    }

    std::vector<int> data;
public:
    void initialize()
    {
        std::vector<std::string> input = Utilities::readFile("../6/input");
        data = parseInput(input);
    }
};