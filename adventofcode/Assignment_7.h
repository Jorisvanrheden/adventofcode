#pragma once

#include "Assignment.h"

class Assignment_7 : public Assignment
{
private:

    class ICostCalculator 
    {
    public:
        virtual int getCost(int distance) = 0;
    };

    class CostCalculatorA : public ICostCalculator
    {
    public:
        CostCalculatorA() {}
        int getCost(int distance) 
        {
            return distance;
        }
    };

    class CostCalculatorB : public ICostCalculator
    {
    public:
        CostCalculatorB() {}
        int getCost(int distance)
        {
            int cost = 0;
            for (int i = 0; i <= distance; i++)
            {
                cost += i;
            }
            return cost;
        }
    };

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

    void getBounds(const std::vector<int>& input, int& min, int& max)
    {
        min = INT_MAX;
        max = INT_MIN;

        for (int i = 0; i < input.size(); i++)
        {
            if (input[i] < min)min = input[i];
            if (input[i] > max)max = input[i];
        }
    }

    int getCost(const std::vector<int>& input, int x, ICostCalculator* calculator)
    {
        int total = 0;

        for (int i = 0; i < input.size(); i++)
        {
            int distance = abs(input[i] - x);
            total += calculator->getCost(distance);
        }

        return total;
    }

    int getLowestCost(const std::vector<int>& input, ICostCalculator* calculator)
    {
        int min, max;
        getBounds(input, min, max);

        int lowestCost = INT_MAX;

        for (int i = min; i <= max; i++)
        {
            int cost = getCost(input, i, calculator);
            if (cost < lowestCost)lowestCost = cost;
        }

        return lowestCost;
    }

    std::string getSolutionPart1()
    {
        int result = getLowestCost(data, new CostCalculatorA());

        return std::to_string(result);
    }

    std::string getSolutionPart2()
    {
        int result = getLowestCost(data, new CostCalculatorB());

        return std::to_string(result);
    }

    std::vector<int> data;
public:
    void initialize()
    {
        std::vector<std::string> input = Utilities::readFile("../7/input");
        data = parseInput(input);
    }
};