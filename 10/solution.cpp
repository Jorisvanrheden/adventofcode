#include <iostream>
#include <fstream>
#include <vector>
#include <algorithm>
#include <string>
#include <limits.h>
#include <map>

std::vector<std::string> substring(const std::string& line, const std::string& delimiter)
{
    std::string mutableLine = std::string(line);

    std::vector<std::string> substrings;

    size_t position = 0;

    //while another instance of the delimiter has been found
    while (mutableLine.find(delimiter) != std::string::npos)
    {
        position = mutableLine.find(delimiter);

        std::string token = mutableLine.substr(0, position);

        if (!token.empty())
        {
            substrings.push_back(token);
        }

        mutableLine.erase(0, position + delimiter.length());
    }

    substrings.push_back(mutableLine);

    return substrings;
}

std::vector<std::string> parseInput(const std::string& filepath)
{
    std::vector<std::string> input;

    std::ifstream file(filepath);

    std::string line;
    while (std::getline(file, line))
    {
        input.push_back(line);
    }

    return input;
}

std::vector<char> getInvalidCharacters(const std::string& line)
{
    const std::string OPEN_SET = "([{<";
    const std::string CLOSED_SET = ")]}>";

    //traverse through the string
    //keep track of the OPEN_SET types that occurred
    //the moment a CLOSED_SET type occurs, store the index and backtrack?

    //keep track of how MANY open sets are in a row

    //lets say the input looks like "([{"
    //3 open types
    //the moment a closed type comes along, it firstly MUST pair
    //with the latest open set

    //probably keep track of open sets and their respective characters

    std::vector<int> openIndices;
    
    for (int i = 0; i < line.length(); i++)
    {
        //open set char found
        auto openSetIterator = std::find(OPEN_SET.begin(), OPEN_SET.end(), line[i]);
        if (openSetIterator != OPEN_SET.end()) 
        {
            int index = openSetIterator - OPEN_SET.begin();

            openIndices.push_back(index);
        }
        
        //closed set char found
        auto closedSetIterator = std::find(CLOSED_SET.begin(), CLOSED_SET.end(), line[i]);
        if (closedSetIterator != CLOSED_SET.end())
        {
            int index = closedSetIterator - CLOSED_SET.begin();

            //if no open set has occurred before, the line is corrupt
            if (openIndices.size() == 0)
            {
                return { CLOSED_SET[index] };
            }

            //check what the latest openIndex is
            int latestOpenIndex = openIndices[openIndices.size() - 1];

            //the closed set char must be the counterpart of the openIndex char
            if (index == latestOpenIndex) 
            {
                //if they are the same, the latest open index must be popped from the vector
                //this enables comparing with the next closed char
                openIndices.pop_back();
            }
            else 
            {
                //if they are not the same, there is a pair discrepancy
                std::cout << "Expected " << CLOSED_SET[latestOpenIndex] << ", got " << CLOSED_SET[index] << std::endl;
                return { CLOSED_SET[index] };
            }
        }
    }

    if (openIndices.size() > 0) 
    {
        std::cout << "Error: open pairs detected" << std::endl;
    }

    return {};
}

int getInvalidCharacterScore(const std::vector<char>& characters) 
{
    int total = 0;

    for (const auto& c : characters)
    {
        if      (c == ')') total += 3;
        else if (c == ']') total += 57;
        else if (c == '}') total += 1197;
        else if (c == '>') total += 25137;
    }

    return total;
}

int getAutoCompleteCharacterScore(const std::vector<char>& characters)
{
    int total = 0;

    for (const auto& c : characters)
    {
        if      (c == ')') total += 1;
        else if (c == ']') total += 2;
        else if (c == '}') total += 3;
        else if (c == '>') total += 4;
    }

    return total;
}

int main()
{
    std::vector<std::string> input = parseInput("D:\\repos\\adventofcode\\10\\input");

    std::vector<char> invalidCharacters;
    for (int i = 0; i < input.size(); i++)
    {
        std::vector<char> output = getInvalidCharacters(input[i]);
        invalidCharacters.insert(invalidCharacters.end(), output.begin(), output.end());
    }
    
    //solution A
    int result = getInvalidCharacterScore(invalidCharacters);





    //solution B
    //int result = 0;

    std::cout << "--- ANSWER IS ---" << std::endl;
    std::cout << result << std::endl;

    return 0;
}