#include <iostream>
#include <fstream>
#include <vector>
#include <algorithm>
#include <string>
#include <limits.h>
#include <map>

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

Signal parseSignal(std::string line)
{
    //split up signals and digits
    std::vector<std::string> parts = substring(line, " | ");

    if (parts.size() != 2)
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
    while (std::getline(file, line))
    {
        input.push_back(parseSignal(line));
    }

    return input;
}

std::vector<int> getAreasForDigit(int digit)
{
    switch (digit)
    {
    case 0:
        return { 1, 2, 3, 5, 6, 7 };
    case 1:
        return { 3, 6 };
    case 2:
        return { 1, 3, 4, 5, 7 };
    case 3:
        return { 1, 3, 4, 6, 7 };
    case 4:
        return { 2, 3, 4, 6 };
    case 5:
        return { 1, 2, 4, 6, 7 };
    case 6:
        return { 1, 2, 4, 5, 6, 7 };
    case 7:
        return { 1, 3, 6 };
    case 8:
        return { 1, 2, 3, 4, 5, 6, 7 };
    case 9:
        return { 1, 2, 3, 4, 6, 7 };
    default:
        return {};
        break;
    }
}
/*
    Some deducing necessary here, and we also have to write it in code. No problem.

    For the entries with the following lengths, we know what digits they represent:
    - digit 1 : length 2
    - digit 4 : length 4
    - digit 7 : length 3
    - digit 8 : length 7

    How to deduce the following areas:
    - AREA_1 = difference between 1 and 7

    Let's say we have the input 'ab'. That's of length 2, therefore the digit is 1.
    For the digit 1, digit areas [3, 6] are used. Meaning that for decoding, both characters must
    represent either the 3 or the 6.

    We can create a hashmap, where the key is the area and the value is the collection of
    possible characters that can represent that digit.

    The hashmap can be initialized with the 4 unique entries that are available.
    Perhaps it's already possible with this information to determine all entries.
*/

std::vector<std::string> generatePermutations(std::string input) 
{
    std::vector<std::string> permutations;

    permutations.push_back(input);

    while (std::next_permutation(input.begin(), input.end())) 
    {
        permutations.push_back(input);
    }

    return permutations;
}

int stringToDigit(const std::string& input, const std::string& map)
{
    std::vector<int> areas(input.length(), 0);

    //for each char in the input string, get the corresponding map-index
    for (int i = 0; i < input.length(); i++) 
    {
        auto it = std::find(map.begin(), map.end(), input[i]);
        if (it != map.end()) 
        {
            areas[i] = std::distance(map.begin(), it) + 1;
        }
    }

    //using the combination of all indices, we can check if a digit supports those combinations
    for (int i = 0; i < 10; i++) 
    {
        std::vector<int> digitAreas = getAreasForDigit(i);
        if (digitAreas.size() != areas.size()) continue;

        if (std::is_permutation(areas.begin(), areas.end(), digitAreas.begin())) 
        {
            return i;
        }
    }

    return -1;
}

bool checkIfPermutationIsValid(const std::string& permutation, const Signal& signal)
{
    //Check if the permutations gives valid input
    for (const auto& input : signal.signals)
    {
        int result = stringToDigit(input, permutation);

        if (result == -1) 
        {
            return false;
        }
    }
    return true;
}

std::string getPermutation(const Signal& signal, const std::vector<std::string>& permutations)
{
    for (int i = 0; i < permutations.size(); i++)
    {
        if (checkIfPermutationIsValid(permutations[i], signal))
        {
            std::cout << "VALID" << std::endl;
            return permutations[i];
        }
    }

    return std::string();
}

int main()
{
    std::vector<Signal> input = parseInput("./input");
    std::vector<std::string> permutations = generatePermutations("abcdefg");

    int total = 0;
    for (int i = 0; i < input.size(); i++) 
    {
        std::string map = getPermutation(input[i], permutations);
        if (map.length() > 0) 
        {
            std::string valueString;
            for (const auto& digit : input[i].digits) 
            {
                int r = stringToDigit(digit, map);
                valueString += std::to_string(r);
            }

            //parse the 4 digit number
            int value = std::stoi(valueString);
            total += value;
        }
    }

    int result = total;

    std::cout << "--- ANSWER IS ---" << std::endl;
    std::cout << result << std::endl;

    return 0;
}