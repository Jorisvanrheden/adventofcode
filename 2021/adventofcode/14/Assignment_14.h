#pragma once

#include "Assignment.h"

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

    void process_2(std::map<std::string, unsigned long long>& map, std::map<char, unsigned long long>& indexedMap, const std::vector<Entry>& entries)
    {
        //keep track of symbols and their inject-value
        std::map<std::string, std::string> symbolMap;
        std::map<std::string, unsigned long long> additions;

        for (const auto& entry : entries)
        {
            //initialize symbol mapping
            symbolMap[entry.before] = entry.after;
            additions[entry.before] = 0;
        }

        //keep track of how many times each combination occurs
        for (const auto& pair : map)
        {
            if (pair.second == 0) continue;

            std::string symbol = pair.first;

            //check if the pair is in the sequence
            if (symbolMap.find(symbol) != symbolMap.end())
            {
                std::string symbolToInject = symbolMap.at(symbol);

                //also increment the two created new values
                std::string valueA;
                valueA += symbol[0];
                valueA += symbolToInject;

                std::string valueB;
                valueB += symbolToInject;
                valueB += symbol[1];

                additions[valueA] += pair.second;   
                additions[valueB] += pair.second;

                //process the indexed map additions
                if (indexedMap.find(symbolToInject[0]) == indexedMap.end())
                {
                    indexedMap[symbolToInject[0]] = 0;
                }
                indexedMap[symbolToInject[0]] += pair.second;

                //since the original combination doesn't exist anymore, remove it
                map[symbol] -= pair.second;
            }                       
        }

        //add combination count in the map
        for (const auto& pair : additions) 
        {
            map[pair.first] += pair.second;
        }
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

    unsigned long long getResultAfterSteps(Input input, int steps) 
    {
        //initialize map and indexed map
        std::map<std::string, unsigned long long> map;
        std::map<char, unsigned long long> indexedMap;

        for (const auto& entry : input.entries)
        {
            //initialize the map counter 
            map[entry.before] = 0;
        }
        for (const auto& c : input.sequence)
        {
            //initialize the indexed map counter 
            if (indexedMap.find(c) == indexedMap.end()) 
            {
                indexedMap[c] = 0;
            }

            indexedMap[c]++;
        }

        for (int i=0;i<input.sequence.size()-1;i++)
        {
            std::string symbol;
            symbol += input.sequence[i];
            symbol += input.sequence[i + 1];

            map[symbol]++;
        }

        for (int i = 0; i < steps; i++)
        {
            std::cout << "Processing day " << i << std::endl;
            process_2(map, indexedMap, input.entries);
        }

        std::map<unsigned long long, char> flippedMap = flipMap(indexedMap);

        char leastCommonChar = flippedMap.begin()->second;
        char mostCommonChar = std::prev(flippedMap.end())->second;

        return indexedMap.at(mostCommonChar) - indexedMap.at(leastCommonChar);
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
    void initialize(const std::vector<std::string>& input)
    {
        data = parseInput(input);
    }

    std::string getInput()
    {
        return "14/input";
    }
};