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

int getChunkSum(const std::vector<int>& input, int index, int chunkSize)
{
    int sum = 0;

    for(int i=0;i<chunkSize;i++)
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
    for(int i=chunkSize;i<input.size();i++)
    {
        int sumCurrentChunk  = getChunkSum(input, i, chunkSize);
        int sumPreviousChunk = getChunkSum(input, i - 1, chunkSize);

        if(sumCurrentChunk > sumPreviousChunk) counter++;
    }

    return counter;
}

int main()
{
    std::vector<int> input = parseInput("./input");

    int result = findIncreasingMeasurements(input, 1);

    std::cout<<"--- ANSWER IS ---"<<std::endl;
    std::cout<<result<<std::endl;

    return 0;
}