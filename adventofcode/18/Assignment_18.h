#pragma once

#include "Assignment.h"
#include <algorithm>
#include <map>

class Assignment_18 : public Assignment
{
private:

    struct Pair 
    {
        Pair* parent = NULL;
        std::vector<Pair*> children;

        int value;

        Pair(Pair* parent, int value)
            : parent(parent), value(value)
        {
            
        }

        void setChildren(const std::vector<Pair*>& children) 
        {
            this->children = children;

            for (const auto& child : children) 
            {
                child->parent = this;
            }
        }
    };

    Pair* parsePair(const std::string& input, Pair* parentNode) 
    {
        std::string mutableInput = input;

        //find content between opening and closing '[]'

        //find all opening and closing indices
        //the inner indices then form pairs?

        std::vector<int> openingSymbolIndices;
        std::vector<int> closingSymbolIndices;
        std::vector<int> separatorSymbolIndices;

        for (int i = 0; i < input.size(); i++) 
        {
            if      (input[i] == '[') openingSymbolIndices.push_back(i);
            else if (input[i] == ']') closingSymbolIndices.push_back(i);
            else if (input[i] == ',') separatorSymbolIndices.push_back(i);
        }

        //opening and closing symbols must be of same size
        if (openingSymbolIndices.size() != closingSymbolIndices.size()) 
        {
            std::cout << "Opening and closing symbol occurrences must be equal - was not the case" << std::endl;
            return NULL;
        }

        //no more splits happen when the size is one, then the entry can be parsed
        if (openingSymbolIndices.size() == 1) 
        {
            //parse the single pair
            //left and right value

            std::vector<std::string> parts = Utilities::splitString(input, ",");

            //remove the symbols from the string
            parts[0].erase(0, 1);
            parts[1].erase(parts[1].size()-1, 1);

            Pair* node = new Pair(parentNode, 0);

            Pair* left = parsePair(parts[0], node);
            Pair* right = parsePair(parts[1], node);

            node->setChildren({ left,right });

            return node;
        }
        else if (openingSymbolIndices.size() == 0)
        {
            //parse values of entry
            int value = std::stoi(input);
            return new Pair(parentNode, value);
        }

        //find the 'split', where there are equally many pairs left as there are right 
        //of the same depth then

        //the split is the index where a ',' appears
        //if this happens at depth '1' (only one open bracket), then the split is detected

        for (int i = 0; i < separatorSymbolIndices.size(); i++)
        {
            //calculate the depth by counting the open and closed brackets
            int depth = 0;
            int separatorIndex = separatorSymbolIndices[i];

            for (const auto& openIndex : openingSymbolIndices) 
            {
                if (openIndex < separatorIndex) depth++;
            }
            for (const auto& closedIndex : closingSymbolIndices)
            {
                if (closedIndex < separatorIndex) depth--;
            }

            if (depth == 1) 
            {
                std::string leftInput = input.substr(0, separatorIndex);
                leftInput.erase(0, 1);

                std::string rightInput = input.substr(separatorIndex + 1, input.size() - separatorIndex - 1);
                rightInput.erase(rightInput.size() - 1, 1);

                Pair* node = new Pair(parentNode, 0);

                Pair* left = parsePair(leftInput, node);
                Pair* right = parsePair(rightInput, node);

                node->setChildren({ left,right });

                return node;
            }
        }

        return NULL;
    }

    std::vector<Pair*> parseInput(const std::vector<std::string>& input)
    {
        std::vector<Pair*> data;

        for (const auto& entry : input)
        {
            Pair* pair = parsePair(entry, NULL);

            data.push_back(pair);
        }

        return data;
    }

    void addToCollection(Pair* pair, std::vector<Pair*>& collection) 
    {
        if (pair->children.size() == 0) collection.push_back(pair);

        else 
        {
            for (const auto& c : pair->children) 
            {
                addToCollection(c, collection);
            }
        }
    }

    std::vector<Pair*> index(Pair* pair) 
    {
        //start at the parent node
        Pair* activeNode = pair;
        while (true) 
        {
            if (activeNode->parent == NULL) break;

            activeNode = activeNode->parent;
        }

        std::vector<Pair*> collection;
        addToCollection(activeNode, collection);

        return collection;
    }

    void explodePair(Pair* pair) 
    {
        std::vector<Pair*> collection = index(pair);
        //find the index of the active item
        auto itLeft  = std::find(collection.begin(), collection.end(), pair->children[0]);
        auto itRight = std::find(collection.begin(), collection.end(), pair->children[1]);

        if (itLeft == collection.end() || itRight == collection.end())
        {
            std::cout << "shouldnt happen" << std::endl;
            return;
        }

        int indexLeft = itLeft - collection.begin();
        int indexRight = itRight - collection.begin();

        Pair* left = NULL;
        Pair* right = NULL;

        if (indexLeft > 0) left = collection[indexLeft - 1];
        if (indexRight < collection.size() - 1) right = collection[indexRight + 1];

        if (left) 
        {
            left->value += pair->children[0]->value;
        }
        else
        {
            pair->parent->children[0]->value = 0;
        }

        if (right)
        {
            right->value += pair->children[1]->value;
        }
        else 
        {
            pair->parent->children[1]->value = 0;
        }

        pair->children = {};
    }

    void splitPair(Pair* pair) 
    {
        int leftValue = floor((float)pair->value / 2);
        int rightValue = ceil((float)pair->value / 2);

        pair->value = 0;
        pair->setChildren({ new Pair(pair, leftValue), new Pair(pair, rightValue) });
    }

    Pair* addPairs(Pair* a, Pair* b) 
    {
        Pair* n = new Pair(NULL, 0);
        n->setChildren({ a,b });
        return n;
    }

    Pair* getPairForExplosion(Pair* pair, int depth)
    {
        if (pair->children.size() > 0) 
        {
            if (depth == 4) 
            {
                //get the first leftmost pair
                Pair* p = pair;
                while (p->children.size() > 0) 
                {
                    if      (p->children[0]->children.size() > 0) p = p->children[0];
                    else if (p->children[1]->children.size() > 0) p = p->children[1];

                    else p = p->children[0];
                }
                return p->parent;
            }
        }

        for (const auto& child : pair->children) 
        {
            Pair* c = getPairForExplosion(child, depth + 1);
            if (c) return c;
        }

        return NULL;
    }

    Pair* getPairForSplitting(Pair* pair)
    {
        if (pair->value >= 10)
        {
            return pair;
        }

        for (const auto& child : pair->children)
        {
            Pair* c = getPairForSplitting(child);
            if (c) return c;
        }

        return NULL;
    }

    Pair* calculateSum(Pair* pair) 
    {
        while (true)
        {
            Pair* explosionPair = getPairForExplosion(pair, 0);
            if (explosionPair) 
            {
                explodePair(explosionPair);
                continue;
            }

            Pair* splittingPair = getPairForSplitting(pair);
            if (splittingPair)
            {
                splitPair(splittingPair);
                continue;
            }  

            break;
        }

        return pair;
    }

    std::string exportToString(Pair* pair, std::string& output)
    {
        if (pair->children.size() > 0)
        {
            output += "[";

            for (int i = 0; i < pair->children.size(); i++)
            {
                exportToString(pair->children[i], output);

                if (i < pair->children.size() - 1)
                {
                    output += ",";
                }
            }

            output += "]";

        }
        else
        {
            output += std::to_string(pair->value);
        }

        return output;
    }

    void printPair(Pair* pair) 
    {
        std::string s;
        std::cout << exportToString(pair, s) << std::endl;
    }

    int getMagnitude(Pair* pair) 
    {
        if (pair->children.size() > 0) 
        {
            return 3 * getMagnitude(pair->children[0]) + 2 * getMagnitude(pair->children[1]);
        }

        return pair->value;
    }

    std::string getSolutionPart1()
    {
        int result = 0;

        auto data = parseInput(input);

        Pair* input = data[0];

        for (int i = 1; i < data.size(); i++) 
        {
            input = addPairs(input, data[i]);

            input = calculateSum(input);
        }

        printPair(input);

        result = getMagnitude(input);

        return std::to_string(result);
    }

    std::string getSolutionPart2()
    {
        int largestMagnitude = 0;

        auto data = parseInput(input);

        for (int i = 0; i < data.size(); i++) 
        {
            for (int j = 0; j < data.size(); j++)
            {
                std::vector<Pair*> copy = parseInput(input);

                Pair* input = addPairs(copy[i], copy[j]);

                input = calculateSum(input);
                
                int magnitude = getMagnitude(input);
                if (magnitude > largestMagnitude) 
                {
                    largestMagnitude = magnitude;
                }
            }
        }

        int result = largestMagnitude;

        return std::to_string(result);
    }

    std::vector<std::string> input;

public:
    void initialize(const std::vector<std::string>& input)
    {
        this->input = input;
    }

    std::string getInput()
    {
        return "18/input";
    }
};