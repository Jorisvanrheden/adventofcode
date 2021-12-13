#include <iostream>
#include <fstream>
#include <vector>
#include <stdlib.h>

std::vector<std::string> parseInput(const std::string& filepath)
{
    std::vector<std::string> input;

    std::ifstream file(filepath);

    std::string line;
    while(std::getline(file, line))
    {
        input.push_back(line);
    }

    return input;
}

int getMostOccurringDigit(const std::vector<std::string>& input, int column)
{
    const int NUMBERS = 9;

    int occurrenceCounter[NUMBERS] = {0};

    for(const auto& row : input)
    {
        //verify column index as valid
        if(column >= row.length()) continue;

        int value = row[column] - '0';

        //iterate counter
        occurrenceCounter[value]++;
    }

    //retrieve most occurrences index
    int occurencesIndex = 0;
    int occurences = 0;
    for(int i=0;i<NUMBERS;i++)
    {
        if(occurrenceCounter[i] > occurences)
        {
            occurences = occurrenceCounter[i];
            occurencesIndex = i;    
        }
    }

    return occurencesIndex;
}

std::string createMostCommonBinary(const std::vector<std::string>& input)
{
    int length = 0;
    if(input.size()>0) length = input[0].length();

    std::string gamma;
    gamma.resize(length);

    for(int i=0;i<length;i++)
    {
        int digit = getMostOccurringDigit(input, i);

        //transform int to char
        gamma[i] = digit + '0';
    }

    return gamma;
}

std::string flipBinary(const std::string& binary)
{
    std::string flippedBinary = std::string(binary);

    for(int i=0;i<binary.length();i++)
    {
        int numericalValue = binary[i] - '0';
        int flippedValue = !numericalValue;

        //overwrite the copied index with the flipped value
        flippedBinary[i] = flippedValue + '0';
    }

    return flippedBinary;
}

int computePowerConsumption(const std::string& binaryGammaRate, const std::string& binaryEpsilonRate)
{
    int gamma   = std::stoi(binaryGammaRate, 0, 2);
    int epsilon = std::stoi(binaryEpsilonRate, 0, 2);

    return gamma * epsilon;
}

int main()
{
    std::vector<std::string> input = parseInput("./input");

    std::string gammaBinary   = createMostCommonBinary(input);
    std::string epsilonBinary = flipBinary(gammaBinary);

    int result = computePowerConsumption(gammaBinary, epsilonBinary);

    std::cout<<"--- ANSWER IS ---"<<std::endl;
    std::cout<<result<<std::endl;

    return 0;
}