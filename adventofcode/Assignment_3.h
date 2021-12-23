#pragma once

#include "Assignment.h"

class Assignment_3 : public Assignment
{
private:
    const int OCCURRENCES_EQUAL = -1;

    int getMostOccurringDigit(const std::vector<std::string>& input, int column)
    {
        int zeroes = 0;
        int ones = 0;

        for (const auto& row : input)
        {
            //verify column index as valid
            if (column >= row.length()) continue;

            int value = row[column] - '0';

            if (value) ones++;
            else zeroes++;
        }

        if (ones > zeroes)return 1;
        else if (ones < zeroes)return 0;
        else if (ones == zeroes) return OCCURRENCES_EQUAL;
    }

    std::vector<std::string> filter(const std::vector<std::string>& input, int column, char filter)
    {
        std::vector<std::string> filteredCollection;

        for (int i = 0; i < input.size(); i++)
        {
            if (input[i][column] == filter)
            {
                filteredCollection.push_back(input[i]);
            }
        }

        return filteredCollection;
    }

    int getInputColumns(const std::vector<std::string>& input)
    {
        if (input.size() > 0) return input[0].length();

        return 0;
    }

    std::string createMostCommonBinary(const std::vector<std::string>& input)
    {
        int length = getInputColumns(input);

        std::string gamma;
        gamma.resize(length);

        for (int i = 0; i < length; i++)
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

        for (int i = 0; i < binary.length(); i++)
        {
            int numericalValue = binary[i] - '0';
            int flippedValue = !numericalValue;

            //overwrite the copied index with the flipped value
            flippedBinary[i] = flippedValue + '0';
        }

        return flippedBinary;
    }

    int multiplyBinaries(const std::string& binaryA, const std::string& binaryB)
    {
        int a = std::stoi(binaryA, 0, 2);
        int b = std::stoi(binaryB, 0, 2);

        return a * b;
    }

    std::string findOxygenGeneratorRating(std::vector<std::string> input)
    {
        //for each column, find the most common value
        int columns = getInputColumns(input);

        for (int i = 0; i < columns; i++)
        {
            //if an equal amount of cases arises, select '1'
            int digit = getMostOccurringDigit(input, i);
            if (digit == OCCURRENCES_EQUAL)
            {
                digit = 1;
            }

            char digitFilter = digit + '0';

            //filter the collection
            input = filter(input, i, digitFilter);

            if (input.size() == 1) return input[0];
        }

        return std::string();
    }

    std::string findCO2ScrubberRating(std::vector<std::string> input)
    {
        //for each column, find the most common value
        int columns = getInputColumns(input);

        for (int i = 0; i < columns; i++)
        {
            //if an equal amount of cases arises, select '1'
            int digit = getMostOccurringDigit(input, i);
            if (digit == OCCURRENCES_EQUAL)
            {
                digit = 1;
            }

            //flip the digit for least occurrences
            digit = !digit;

            char digitFilter = digit + '0';

            //filter the collection
            input = filter(input, i, digitFilter);

            if (input.size() == 1) return input[0];
        }

        return std::string();
    }

    std::string getSolutionPart1()
    {
        std::string gammaBinary = createMostCommonBinary(data);
        std::string epsilonBinary = flipBinary(gammaBinary);

        int result = multiplyBinaries(gammaBinary, epsilonBinary);

        return std::to_string(result);
    }

    std::string getSolutionPart2()
    {
        std::string oxygenGeneratorRating = findOxygenGeneratorRating(data);
        std::string CO2ScrubberRating = findCO2ScrubberRating(data);

        int result = multiplyBinaries(oxygenGeneratorRating, CO2ScrubberRating);

        return std::to_string(result);
    }

    std::vector<std::string> data;
public:
    void initialize()
    {
        data = Utilities::readFile("../3/input");
    }
};