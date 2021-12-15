#include <iostream>
#include <fstream>
#include <vector>
#include <algorithm>
#include <string.h>
#include <limits.h>

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

std::vector<int> parseInput(const std::string& filepath)
{
    std::vector<int> input;

    std::ifstream file(filepath);

    std::string line;
    while(std::getline(file, line))
    {
        std::vector<std::string> parts = substring(line, ",");
        for(int i=0;i<parts.size();i++)
        {
            try
            {
                int value = std::stoi(parts[i]);
                input.push_back(value);
            }
            catch(std::exception& e){}
        }
    }

    return input;
}

void getBounds(const std::vector<int>& input, int& min, int& max)
{
    min = INT_MAX;
    max = INT_MIN;

    for(int i=0;i<input.size();i++)
    {
        if(input[i]<min)min=input[i];
        if(input[i]>max)max=input[i];
    }
}

int getCostSolutionA(int distance)
{
    return distance;
}

int getCostSolutionB(int distance)
{
    int cost = 0;
    for(int i=0;i<=distance;i++)
    {
        cost+=i;
    }
    return cost;
}

int getCost(const std::vector<int>& input, int x)
{
    int total = 0;

    for(int i=0;i<input.size();i++)
    {
        int distance = abs(input[i] - x);
        total += getCostSolutionB(distance);
    }

    return total;
}

int getLowestCost(const std::vector<int>& input)
{
    int min, max;
    getBounds(input, min, max);

    int lowestCost = INT_MAX;

    for(int i=min;i<=max;i++)
    {
        int cost = getCost(input, i);
        if(cost<lowestCost)lowestCost = cost;
    }

    return lowestCost;
}

int main()
{
    std::vector<int> input = parseInput("./input");
 
    int result = getLowestCost(input);

    std::cout<<"--- ANSWER IS ---"<<std::endl;
    std::cout<<result<<std::endl;
        
    return 0;
}