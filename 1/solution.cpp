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

int main()
{
    std::cout<<"Hello world"<<std::endl;

    std::vector<int> input = parseInput("./input");

    std::cout<<input.size()<<std::endl;

    return 0;
}

//Parse input
