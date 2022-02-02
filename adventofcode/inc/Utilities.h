#pragma once

#include <iostream>
#include <fstream>
#include <vector>
#include <string>
#include <cctype>

class Utilities
{
    public:
        static std::vector<std::string> readFile(const std::string& filePath) 
        {
            std::vector<std::string> lines;

            std::ifstream file(filePath);

            std::string line;
            while (std::getline(file, line))
            {
                lines.push_back(line);
            }

            return lines;
        }

        static std::vector<std::string> splitString(const std::string& line, const std::string& delimiter) 
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

        static std::string ltrim(const std::string& input, const std::string trimChar)
        {
            size_t start = input.find_first_not_of(trimChar);
            return (start == std::string::npos) ? "" : input.substr(start);
        }

        static std::string rtrim(const std::string& input, const std::string trimChar)
        {
            size_t end = input.find_last_not_of(trimChar);
            return (end == std::string::npos) ? "" : input.substr(0, end + 1);
        }

        static std::string trim(const std::string& input, const std::string trimChar) 
        {
            return rtrim(ltrim(input, trimChar), trimChar);
        }

        static bool isUpperCase(const std::string& input)
        {
            for (const auto& c : input)
            {
                if (!std::isupper(c)) return false;
            }

            return true;
        }

        static char intToChar(int x) 
        {
            return x + '0';
        }

        static int charToInt(int c) 
        {
            return c - '0';
        }

        static unsigned long long binaryToDecimal(const std::string& binary)
        {
            return std::stoll(binary, 0, 2);
        }
private:
};
