#pragma once

#include "Assignment.h"
#include <algorithm>
#include <map>

class Assignment_14 : public Assignment
{
private:

    struct Entry 
    {
        std::string before;
        std::string after;

        Entry() {}
        Entry(const std::string& before, const std::string& after) 
            : before(before), after(after)
        {
        
        }
    };
    struct Input 
    {
        std::vector<Entry> entries;
        std::string sequence;

        Input() {}
        Input(const std::vector<Entry>& entries, const std::string& sequence)
            : entries(entries), sequence(sequence)
        {

        }
    };

    Input parseInput(const std::vector<std::string>& input)
    {
        std::vector<Entry> entries;
        std::string sequence;

        for (const auto& entry : input)
        {
            if (entry.empty()) continue;

            //if the entry continas -> process entries
            //otherwise process sequence
            if (entry.find("->") != std::string::npos) 
            {
                std::vector<std::string> parts = Utilities::splitString(entry, " -> ");
                entries.push_back(Entry(parts[0], parts[1]));
            }
            else 
            {
                sequence = entry;
            }      
        }

        return Input(entries, sequence);
    }

    std::string process(const std::string& input, const std::vector<Entry>& entries) 
    {
        std::string modifiedInput = input;

        std::map<std::string, std::string> entryMap;
        for (const auto& entry : entries) 
        {
            entryMap[entry.before] = entry.after;
        }

        //keep track of the indices and what their inject value should be
        std::map<unsigned long long, std::string> map;

        for (int i = 0; i < input.length() - 1; i++) 
        {
            std::string pair;          
            pair += input[i];
            pair += input[i + 1];

            //check if the pair is in the sequence
            if (entryMap.find(pair) != entryMap.end()) 
            {
                int index = i + map.size() + 1;
                map[index] = entryMap.at(pair);
            }
        }

        for (const auto& pair : map) 
        {
            modifiedInput.insert(pair.first, pair.second);
        }

        return modifiedInput;
    }

    std::map<unsigned long long, char> flipMap(const std::map<char, unsigned long long>& map)
    {
        std::map<unsigned long long, char> flippedMap;

        for (const auto& pair : map) 
        {
            flippedMap[pair.second] = pair.first;
        }

        return flippedMap;
    }

    std::map<char, unsigned long long> indexSequence(const std::string& sequence)
    {
        std::map<char, unsigned long long> map;

        for (int i = 0; i < sequence.length(); i++) 
        {
            if (map.find(sequence[i]) == map.end()) 
            {
                map[sequence[i]] = 0;
            }

            map[sequence[i]]++;
        }
        
        return map;
    }

    unsigned long long getResultAfterSteps(Input input, int steps) 
    {
        std::string sequence = input.sequence;
        for (int i = 0; i < steps; i++)
        {
            std::cout << "Processing day " << i << std::endl;
            sequence = process(sequence, input.entries);
        }

        std::map<char, unsigned long long> map = indexSequence(sequence);
        std::map<unsigned long long, char> sortedMap = flipMap(map);

        char leastCommonChar = sortedMap.begin()->second;
        char mostCommonChar = std::prev(sortedMap.end())->second;

        return map.at(mostCommonChar) - map.at(leastCommonChar);
    }

    std::string getSolutionPart1()
    {
        unsigned long long result = getResultAfterSteps(data, 10);

        return std::to_string(result);
    }

    std::string getSolutionPart2()
    {
        unsigned long long result = getResultAfterSteps(data, 40);

        return std::to_string(result);
    }

    Input data;
public:
    void initialize()
    {
        std::vector<std::string> input = Utilities::readFile("../14/input");
        data = parseInput(input);
    }
};