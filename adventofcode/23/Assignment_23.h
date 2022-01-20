#pragma once

#include "Assignment.h"

class Assignment_23 : public Assignment
{
private:
    struct Unit 
    {
        //integer to represent what unit is occupying the node
        //values:
        // 0 = type A
        // 1 = type B
        // 2 = type C
        // 3 = type D
        int unitValue;

        //once a unit stops in the hallway, it will stay in that spot 
        //until it can move into its destination room
        bool hasMovedToHallway = false;

        int totalDistance = 0;

        Unit() {}
        Unit(int unitValue)
            : unitValue(unitValue)
        {
            
        }
        Unit(Unit* clone) 
        {
            this->unitValue = clone->unitValue;
            this->hasMovedToHallway = clone->hasMovedToHallway; 
            this->totalDistance = clone->totalDistance;
        }
    };
    struct Node
    {
        int id;

        std::vector<Node*> neighbors;

        bool connectsToRoom = false;

        //integer to represent what unit is occupying the node
        //values:
        //-1 = no room
        // 0 = type A
        // 1 = type B
        // 2 = type C
        // 3 = type D
        int roomValue = -1;

        Node() {}
    };

    struct Move
    {
        int distance;
        Node* target;
    };

    struct Input 
    {
        std::vector<Node*> nodes;
        std::map<int, Unit*> map;
    };

    void createLinks(std::vector<Node*>& nodes) 
    {
        for (int i = 0; i < nodes.size(); i++)
        {
            //node on the 'left'
            if (i > 0)
            {
                nodes[i]->neighbors.push_back(nodes[i - 1]);
            }

            //node on the 'right'
            if (i < nodes.size() - 1)
            {
                nodes[i]->neighbors.push_back(nodes[i + 1]);
            }
        }
    }

    void createConnection(Node* nodeA, Node* nodeB) 
    {
        nodeA->neighbors.push_back(nodeB);
        nodeB->neighbors.push_back(nodeA);
    }
    
    Input parseInput(const std::vector<std::string>& input)
    {
        Input data;

        std::vector<Node*> nodes;
        for (int i = 0; i < 11; i++) 
        {
            nodes.push_back(new Node());
        }

        createLinks(nodes);
        
        Node* roomA_1 = new Node();
        Node* roomA_2 = new Node();
                   
        roomA_1->roomValue = 0;
        roomA_2->roomValue = 0;

        Node* roomB_1 = new Node();
        Node* roomB_2 = new Node();

        roomB_1->roomValue = 1;
        roomB_2->roomValue = 1;

        Node* roomC_1 = new Node();
        Node* roomC_2 = new Node();

        roomC_1->roomValue = 2;
        roomC_2->roomValue = 2;

        Node* roomD_1 = new Node();
        Node* roomD_2 = new Node();

        roomD_1->roomValue = 3;
        roomD_2->roomValue = 3;

        createConnection(roomA_1, roomA_2);
        createConnection(roomB_1, roomB_2);
        createConnection(roomC_1, roomC_2);
        createConnection(roomD_1, roomD_2);

        //define the connections to the rooms
        createConnection(nodes[2], roomA_1);
        nodes[2]->connectsToRoom = true;

        createConnection(nodes[4], roomB_1);
        nodes[4]->connectsToRoom = true;

        createConnection(nodes[6], roomC_1);
        nodes[6]->connectsToRoom = true;

        createConnection(nodes[8], roomD_1);
        nodes[8]->connectsToRoom = true;

        nodes.push_back(roomA_1);
        nodes.push_back(roomA_2);
        nodes.push_back(roomB_1);
        nodes.push_back(roomB_2);
        nodes.push_back(roomC_1);
        nodes.push_back(roomC_2);
        nodes.push_back(roomD_1);
        nodes.push_back(roomD_2);

        std::map<int, Unit*> map;
        for (int i = 0; i < nodes.size(); i++) 
        {
            nodes[i]->id = i;

            map[i] = NULL;
        }

        map[11] = new Unit(1);
        map[12] = new Unit(0);

        map[13] = new Unit(2);
        map[14] = new Unit(3);

        map[15] = new Unit(1);
        map[16] = new Unit(2);

        map[17] = new Unit(3);
        map[18] = new Unit(0);

        data.nodes = nodes;
        data.map = map;
        return data;
    }

    std::vector<Node*> getNodesWithUnits(const std::vector<Node*>& nodes, std::map<int, Unit*>& map)
    {
        std::vector<Node*> nodesWithUnits;

        for (const auto& node : nodes) 
        {
            if (map[node->id] != NULL)
            {
                nodesWithUnits.push_back(node);
            }
        }

        return nodesWithUnits;
    }

    bool isDestinationValid(const Node* activeNode, const Node* targetNode, std::map<int, Unit*>& map)
    {
        Unit* unit = map[activeNode->id];

        //first check if the active node is in the correct room
        if (activeNode->roomValue == unit->unitValue) 
        {
            //if the active node only has 1 neighbor, it's already at the bottom position
            if (activeNode->neighbors.size() == 1) return false;

            //if the active node has 2 neighbors and has a neighbor that is also in the right location, the room is filled and complete
            if (activeNode->neighbors.size() == 2) 
            {
                for (const auto& neighbor : activeNode->neighbors) 
                {
                    //find the UNIT on the neighbor
                    Unit* neighborUnit = map[neighbor->id];
                    if (neighborUnit) 
                    {
                        if (neighborUnit->unitValue == activeNode->roomValue) return false;
                    }
                }
            }
        }

        //check if the active and target are both in the room already
        //if they are, the only valid move is to go to the node that only has 1 neigbor
        //it's basically allowing moving from the top room position to the bottom room position
        if (activeNode->roomValue == targetNode->roomValue)
        {
            if (targetNode->neighbors.size() > 1) return false;
        }        

        //if the unit is already in the hallway, it can only move if it
        //goes straight to the destination node
        if (unit->hasMovedToHallway)
        {
            //also make sure that the target position doesnt' have a unit stuck in the corner
            if (targetNode->roomValue == unit->unitValue) 
            {
                //if only 1 neighbor, the unit at that node must be of the correct type
                if (targetNode->neighbors.size() == 1) 
                {
                    Unit* neighborUnit = map[targetNode->id];
                    if (neighborUnit)
                    {
                        if (neighborUnit->unitValue != targetNode->roomValue) return false;
                    }
                }
                if (targetNode->neighbors.size() == 2) 
                {
                    for (const auto& neighbor : targetNode->neighbors)
                    {
                        //find the UNIT on the neighbor
                        Unit* neighborUnit = map[neighbor->id];
                        if (neighborUnit)
                        {
                            if (neighborUnit->unitValue != targetNode->roomValue) return false;
                        }
                    }
                }
            }
            else return false;
        }

        //check if the unit ends up at a node that is connected to a room
        //if that's the case, it should not be allowed (blocks others)
        if (targetNode->connectsToRoom) return false;

        return true;
    }

    bool containsMove(const std::vector<Move>& moves, const Node* node) 
    {
        for (int i = 0; i < moves.size(); i++) 
        {
            if (moves[i].target == node) return true;
        }
        return false;
    }

    void getPossibleMoves(const Node* activeNode, std::vector<Move>& moves, std::map<int, Unit*>& map, int distance)
    {
        for (const auto& node : activeNode->neighbors)
        {
            //if there is already a unit on a node, skip
            if (map[node->id] != NULL) continue;

            //don't backtrack, so check if the index is in there already
            if (containsMove(moves, node)) continue;

            Move move;
            move.target = node;
            move.distance = distance;
            moves.push_back(move);

            //recursively iterate through the next layer
            getPossibleMoves(node, moves, map, distance + 1);
        }
    }

    std::vector<Move> getValidatedMoves(const Node* activeNode, const std::vector<Move>& moves, std::map<int, Unit*>& map)
    {
        std::vector<Move> validatedMoves;

        //loop through all moves; intermediate steps can also be ending locations
        for (const auto& move : moves)
        {
            //check if the move is valid
            bool isValid = isDestinationValid(activeNode, move.target, map);
            if (isValid)
            {
                validatedMoves.push_back(move);
            }
        }

        return validatedMoves;
    }

    int getIndex(const Node* node, const std::vector<Node*> nodes) 
    {
        auto it = std::find(nodes.begin(), nodes.end(), node);
        if (it != nodes.end())
        {
            return it - nodes.begin();
        }
        return -1;
    }

    std::vector<std::map<int, Unit*>> getNextStartingConfigurations(const Node* activeNode, const std::vector<Node*>& nodes, std::map<int, Unit*>& map)
    {
        //basically you want to find all possible moves that the active node can go to
        //for each of these moves, we create a new set of nodes and apply the moved node
        std::vector<Move> moves;
        getPossibleMoves(activeNode, moves, map, 1);

        std::vector<Move> validatedMoves = getValidatedMoves(activeNode, moves, map);



        std::vector<std::map<int, Unit*>> configurations;

        //create a copy of each configuration, but then replace the activeNode with the validatedNode
        //and set all properties as necessary
        for (int i = 0; i < validatedMoves.size(); i++) 
        {
            std::map<int, Unit*> copy = map;
            
            //get the active node iterator
            int activeIndex = getIndex(activeNode, nodes);
            int targetIndex = getIndex(validatedMoves[i].target, nodes);

            if (activeIndex == -1 || targetIndex == -1) continue;

            //apply unit to target, and remove the original index unit
            Unit* unit = new Unit(map[activeIndex]);
            unit->hasMovedToHallway = true;
            unit->totalDistance += validatedMoves[i].distance;

            copy[targetIndex] = unit;
            copy[activeIndex] = NULL;

            configurations.push_back(copy);
        }

        //create a new list of starting configurations
        return configurations;
    }

    bool isConfigurationComplete(const std::vector<Node*> nodes, std::map<int, Unit*>& map)
    {
        //retrieve all nodes that have units on them
        std::vector<Node*> nodesWithUnits = getNodesWithUnits(nodes, map);

        //check if all units are in the right node
        for (const auto& node : nodesWithUnits) 
        {
            Unit* unit = map[node->id];
            if (!unit) return false;

            if (node->roomValue != unit->unitValue) return false;
        }

        return true;
    }

    int getCost(const Unit* unit) 
    {
        if (!unit) return 0;

        int multiplier = 1;

        if      (unit->unitValue == 0) multiplier = 1;
        else if (unit->unitValue == 1) multiplier = 10;
        else if (unit->unitValue == 2) multiplier = 100;
        else if (unit->unitValue == 3) multiplier = 1000;

        return multiplier * unit->totalDistance;
    }

    int lowestCost = INT_MAX;

    void processConfiguration(const std::vector<Node*> nodes, std::map<int, Unit*> map) 
    {
        //check if the configuration is complete
        if (isConfigurationComplete(nodes, map))
        {
            int totalCost = 0;
            for (const auto& pair : map) 
            {
                totalCost += getCost(pair.second);
            }

            if (totalCost < lowestCost) 
            {
                lowestCost = totalCost;

                std::cout << "Lowest cost so far: " << lowestCost << std::endl;
            }

            return;
        }

        //retrieve all nodes that have units on them
        std::vector<Node*> nodesWithUnits = getNodesWithUnits(nodes, map);

        //for each node, find all configurations that have processed one of the 
        //available moves for that node
        for (const auto& node : nodesWithUnits)
        {
            auto configs = getNextStartingConfigurations(node, nodes, map);

            //if a configuration is 'invalid', meaning it can't progress, it will simply
            //not regenerate any new starting configurations and will therefore terminate
            for(const auto& config : configs)
            {
                processConfiguration(nodes, config);
            }
        }
    }

    std::string getSolutionPart1()
    {
        int result = 0;

        processConfiguration(data.nodes, data.map);

        result = lowestCost;

        return std::to_string(result);
    }

    std::string getSolutionPart2()
    {
        int result = 0;

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
        return "23/input";
    }
};