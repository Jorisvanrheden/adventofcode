#include <iostream>
#include <fstream>
#include <vector>
#include <algorithm>
#include <string>
#include <limits.h>
#include <cctype>
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

void addKeysToMap(std::map<std::string, std::vector<std::string>>& map, const std::string& key, const std::string& value) 
{
    //if no key exists
    if (map.find(key) == map.end())
    {
        //initialize the map for that key
        map[key] = std::vector<std::string>();
    }

    //add value to key if value does not exists in list yet
    if (std::find(map[key].begin(), map[key].end(), value) == map[key].end())
    {
        map[key].push_back(value);
    }
}

std::map<std::string, std::vector<std::string>> parseInput(const std::string& filepath)
{
    std::map<std::string, std::vector<std::string>> input;

    std::ifstream file(filepath);

    std::string line;
    while (std::getline(file, line))
    {
        //split by '-'
        //then you get a [from, to] tuple

        std::vector<std::string> parts = substring(line, "-");
        if (parts.size() != 2) 
        {
            std::cout << "Invalid input" << std::endl;
        }

        std::string key = parts[0];
        std::string value = parts[1];
  
        //add keys/values both ways
        addKeysToMap(input, key, value);
        addKeysToMap(input, value, key);
    }

    return input;
}

bool isUpperCase(const std::string& input) 
{
    for (const auto& c : input)
    {
        if (!std::isupper(c)) return false;
    }

    return true;
}

int isAllowedSmallRecurringVisits(const std::map<std::string, std::vector<std::string>>& map, const std::map<std::string, int>& visitedMap)
{
    //there can only be 1 small cave that's visited twice, so we'll have to loop (yuck)
    //through all items to find if there's already been one of those occurrences
    for (const auto& pair : visitedMap)
    {
        if (isUpperCase(pair.first)) continue;

        if (pair.second >= 2) return false;
    }

    return true;
}

bool isAllowedToCheckNode(const std::map<std::string, std::vector<std::string>>& map, const std::map<std::string, int>& visitedMap, const std::string& node)
{
    //if the node is not in the visited map, return true
    if (visitedMap.find(node) == visitedMap.end()) 
    {
        return true;
    }

    if (!isUpperCase(node))
    {
        int visitationLimit = 1;

        if (isAllowedSmallRecurringVisits(map, visitedMap)) 
        {
            visitationLimit = 2;
        }

        //overwrite limits for start and end nodes
        if (node == "start" || node == "end")
        {
            visitationLimit = 1;
        }

        //for small maps, check if the node isn't visited the max amount of times already
        return (visitedMap.at(node) < visitationLimit);
    }

    return true;
}

void processNode(const std::map<std::string, std::vector<std::string>>& map, std::map<std::string, int> visitedNodes, const std::string& start, std::vector<std::vector<std::string>>& paths, std::vector<std::string> path)
{
    //we pass a copy of the visited nodes + path, because for each branching, the nodes can be updated differently
    //these should not impact any other possible paths

    path.push_back(start);

    //label the node as visited through the map data structure 
    if (visitedNodes.find(start) == visitedNodes.end()) 
    {
        visitedNodes[start] = 0;
    }
    //increment visited count
    visitedNodes[start]++;

    //if the goal is found, return the path
    if (start == "end") 
    {
        paths.push_back(path);
        return;
    }

    if (map.find(start) == map.end()) return;

    std::vector<std::string> nodes = map.at(start);

    //for each of the nodes, check if they are allowed to visit
    for (const auto& node : nodes) 
    {
        if (!isAllowedToCheckNode(map, visitedNodes, node)) continue;

        processNode(map, visitedNodes, node, paths, path);
    }
}

std::vector<std::vector<std::string>> getPaths(const std::map<std::string, std::vector<std::string>>& map)
{
    //it's sort of a breadth first search, but with recursion

    //start with the 'start' node
    std::vector<std::string> path;
    std::vector<std::vector<std::string>> paths;
    std::map<std::string, int> visitedNodes;

    processNode(map, visitedNodes, "start", paths, path);

    return paths;
}

void print(std::vector<std::string>& input) 
{
    std::string line;

    for (int i = 0; i < input.size(); i++) 
    {
        line += input[i];

        if (i < input.size() - 1) 
        {
            line += ",";
        }
    }
    std::cout << line << std::endl;
}

int main()
{
    std::map<std::string, std::vector<std::string>> map = parseInput("./input");

    std::vector<std::vector<std::string>> paths = getPaths(map);

    std::sort(paths.begin(), paths.end());
   
    //solution 
    int result = paths.size();

    std::cout << "--- ANSWER IS ---" << std::endl;
    std::cout << result << std::endl;

    return 0;
}