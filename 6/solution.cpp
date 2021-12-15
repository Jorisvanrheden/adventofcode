#include <iostream>
#include <fstream>
#include <vector>
#include <algorithm>
#include <string.h>
#include <limits.h>

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

std::vector<int> parseInput(const std::string& filepath)
{
    std::vector<int> input;

    std::ifstream file(filepath);

    std::string line;
    while(std::getline(file, line))
    {
        std::vector<std::string> parts = substring(line, ",");
        for(int i=0;i<parts.size();i++)
        {
            try
            {
                int value = std::stoi(parts[i]);
                input.push_back(value);
            }
            catch(std::exception& e){}
        }
    }

    return input;
}

void printTracker(std::vector<long long> tracker, int day)
{
    std::cout<<"Day "<<day<<std::endl;
    std::string summary;
    std::string indices;

    for(int i=0;i<tracker.size();i++)
    {
        summary += std::to_string(tracker[i]);
        summary += ", ";

        indices += std::to_string(i);
        indices += ", ";
    }
    std::cout<<indices<<std::endl;
    std::cout<<summary<<std::endl;

}

unsigned long long process(std::vector<int>& input, int days)
{
    //keep track of the amount of fish with 0-8 days
    //a simple array suffice (keep long long though for large amounts)

    std::vector<unsigned long long> tracker(9, 0);

    //initialize the tracker with the original input
    for(int i=0;i<input.size();i++)
    {
        tracker[input[i]]++;
    }
    
    for(int i=0;i<days;i++)
    {
        //Day 0    
        //Original array: 3,4,3,1,2
        //New format:
        // 0, 1, 2, 3, 4, 5, 6, 7, 0
        // 0, 1, 1, 2, 1, 0, 0, 0, 0

        //Day 1    
        //Original array: 2,3,2,0,1
        //New format:
        // 0, 1, 2, 3, 4, 5, 6, 7, 8
        // 1, 1, 2, 1, 0, 0, 0, 0, 0

        //Day 2    
        //Original array: 1,2,1,6,0,8
        //New format:
        // 0, 1, 2, 3, 4, 5, 6, 7, 8
        // 1, 2, 1, 0, 0, 0, 1, 0, 1

        //cache the original zero value
        unsigned  long long originalZeroValue = tracker[0];      

        for(int j=0;j<tracker.size();j++)
        {        
            //set the active tracker value to the following one in the sequence
            if(j < tracker.size() - 1)
            {
                unsigned long long nextValue = tracker[j + 1];

                //set the active index to the following value
                tracker[j] = nextValue;
            }
            //last element in the sequence doesn't have a following one, so it should be set to 0
            else if(j == tracker.size() - 1)
            {
                tracker[j] = 0;
            }            
        }

        //check if the previous day should have fish spawned on the next day
        if(originalZeroValue > 0) 
        {
            //instead, increment values of index 8 and 6
            tracker[8] += originalZeroValue;
            tracker[6] += originalZeroValue;
        }
    }

    unsigned long long total = 0;
    for(int i=0;i<tracker.size();i++)
    {
        total += tracker[i];
    }
    return total;
}

int main()
{
    std::vector<int> input = parseInput("./input");
    
    unsigned long long result = process(input, 256);

    std::cout<<"--- ANSWER IS ---"<<std::endl;
    std::cout<<result<<std::endl;
        
    return 0;
}