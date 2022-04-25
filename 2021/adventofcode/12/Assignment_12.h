#pragma once

#include "Assignment.h"

class Assignment_12 : public Assignment
{
private:

    class INodeVisitor
    {
    public:
        virtual bool isVisitAllowed(const std::map<std::string, std::vector<std::string>>& map, const std::map<std::string, int>& visitedMap, const std::string& node) = 0;
    };

    class NodeVisitorLargeCaves : public INodeVisitor
    {
    public:
        bool isVisitAllowed(const std::map<std::string, std::vector<std::string>>& map, const std::map<std::string, int>& visitedMap, const std::string& node) 
        {
            //if the node is not in the visited map, return true
            if (visitedMap.find(node) == visitedMap.end())
            {
                return true;
            }

            if (!Utilities::isUpperCase(node))
            {
                int visitationLimit = 1;

                /*if (isAllowedSmallRecurringVisits(map, visitedMap))
                {
                    visitationLimit = 2;
                }*/

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
    };

    class NodeVisitorLargeAndSmallCaves : public INodeVisitor
    {
    public:
        bool isVisitAllowed(const std::map<std::string, std::vector<std::string>>& map, const std::map<std::string, int>& visitedMap, const std::string& node)
        {
            //if the node is not in the visited map, return true
            if (visitedMap.find(node) == visitedMap.end())
            {
                return true;
            }

            if (!Utilities::isUpperCase(node))
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
    private:
        int isAllowedSmallRecurringVisits(const std::map<std::string, std::vector<std::string>>& map, const std::map<std::string, int>& visitedMap)
        {
            //there can only be 1 small cave that's visited twice, so we'll have to loop (yuck)
            //through all items to find if there's already been one of those occurrences
            for (const auto& pair : visitedMap)
            {
                if (Utilities::isUpperCase(pair.first)) continue;

                if (pair.second >= 2) return false;
            }

            return true;
        }
    };

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

    std::map<std::string, std::vector<std::string>> parseInput(const std::vector<std::string>& input)
    {
        std::map<std::string, std::vector<std::string>> data;

        for (const auto& entry : input)
        {
            //split by '-'
            //then you get a [from, to] tuple
            std::vector<std::string> parts = Utilities::splitString(entry, "-");
            if (parts.size() != 2)
            {
                std::cout << "Invalid input" << std::endl;
            }

            std::string key = parts[0];
            std::string value = parts[1];

            //add keys/values both ways
            addKeysToMap(data, key, value);
            addKeysToMap(data, value, key);
        }

        return data;
    }

    void processNode(const std::map<std::string, 
                    std::vector<std::string>>& map, std::map<std::string, int> visitedNodes, 
                    const std::string& start, 
                    std::vector<std::vector<std::string>>& paths, 
                    std::vector<std::string> path,
                    INodeVisitor* visitor)
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
            if (!visitor->isVisitAllowed(map, visitedNodes, node)) continue;

            processNode(map, visitedNodes, node, paths, path, visitor);
        }
    }

    std::vector<std::vector<std::string>> getPaths(const std::map<std::string, std::vector<std::string>>& map, INodeVisitor* visitor)
    {
        //it's sort of a breadth first search, but with recursion

        //start with the 'start' node
        std::vector<std::string> path;
        std::vector<std::vector<std::string>> paths;
        std::map<std::string, int> visitedNodes;

        processNode(map, visitedNodes, "start", paths, path, visitor);

        return paths;
    }


    std::string getSolutionPart1()
    {
        std::vector<std::vector<std::string>> paths = getPaths(data, new NodeVisitorLargeCaves());

        int result = paths.size();

        return std::to_string(result);
    }

    std::string getSolutionPart2()
    {
        std::vector<std::vector<std::string>> paths = getPaths(data, new NodeVisitorLargeAndSmallCaves());

        int result = paths.size();

        return std::to_string(result);
    }

    std::map<std::string, std::vector<std::string>> data;
public:
    void initialize(const std::vector<std::string>& input)
    {
        data = parseInput(input);
    }

    std::string getInput()
    {
        return "12/input";
    }
};