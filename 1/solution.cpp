#include <iostream>
#include <fstream>

#include <vector>

std::vector<int> parseInput(std::string filepath)
{
    std::vector<int> input;

    std::ifstream file(filepath);

    std::string line;
    while(std::getline(file, line))
    {
        int value = 0;

        try
        {
            value = std::stoi(line);
        }
        catch(std::exception& e)
        {
            continue;
        }

        input.push_back(value);
    }

    return input;
}

int findIncreasingMeasurements(const std::vector<int>& input)
{
    int counter = 0;

    //start from index 1, as index 0 has no value to compare with
    for(int i=1;i<input.size();i++)
    {
        int current = input[i];
        int previous = input[i-1];

        if(current > previous) counter++;
    }

    return counter;
}

int main()
{
    std::vector<int> input = parseInput("./input");

    int result = findIncreasingMeasurements(input);

    std::cout<<"--- ANSWER IS ---"<<std::endl;
    std::cout<<result<<std::endl;

    return 0;
}