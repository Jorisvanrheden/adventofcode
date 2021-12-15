#include <iostream>
#include <fstream>
#include <vector>
#include <algorithm>
#include <string.h>
#include <limits.h>

struct Signal
{
    std::vector<std::string> signals;
    std::vector<std::string> digits;

    Signal(const std::vector<std::string>& signals, const std::vector<std::string>& digits)
        : signals(signals), digits(digits)
    {

    }
};

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

Signal parseSignal(std::string line)
{
    //split up signals and digits
    std::vector<std::string> parts = substring(line, " | ");

    if(parts.size() != 2)
    {
        throw std::invalid_argument("invalid input");
    }

    std::vector<std::string> signals = substring(parts[0], " ");
    std::vector<std::string> digits = substring(parts[1], " ");

    return Signal(signals, digits);
}

std::vector<Signal> parseInput(const std::string& filepath)
{
    std::vector<Signal> input;

    std::ifstream file(filepath);

    std::string line;
    while(std::getline(file, line))
    {
        input.push_back(parseSignal(line));
    }

    return input;
}

//1: cf         -> 2 groups
//4: bcdf       -> 4 groups
//7: acf        -> 3 groups
//8: abcdefg    -> 7 groups

const std::string ONE = "cf";
const std::string FOUR = "bcdf";
const std::string SEVEN = "acf";
const std::string EIGHT = "abcdefg";

bool matchesOneOfCollectionLength(const std::vector<std::string>& collection, const std::string& value)
{
    for(const auto& entry : collection)
    {
        if(entry.length() == value.length()) return true;
    }

    return false;
}

int getUniqueOccurrenceCount(const std::vector<Signal>& input)
{
    int count = 0;

    std::vector<std::string> uniqueValues = {ONE, FOUR, SEVEN, EIGHT};

    for(const auto& signal : input)
    {
        for(const auto& digit : signal.digits)
        {
            if(matchesOneOfCollectionLength(uniqueValues, digit)) count ++;
        }
    }

    return count;
}

int main()
{
    std::vector<Signal> input = parseInput("./input");
 
    int result = getUniqueOccurrenceCount(input);

    std::cout<<"--- ANSWER IS ---"<<std::endl;
    std::cout<<result<<std::endl;
        
    return 0;
}