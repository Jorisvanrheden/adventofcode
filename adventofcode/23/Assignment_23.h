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

        Unit() {}
        Unit(int unitValue)
            : unitValue(unitValue)
        {
            
        }
        Unit(Unit* clone) 
        {
            this->unitValue = clone->unitValue;
            this->hasMovedToHallway = clone->hasMovedToHallway;
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
        int roomValue;

        Unit* unit = NULL;

        Node() {}
        Node(Node* clone) 
        {
            this->neighbors = std::vector<Node*>(clone->neighbors.size());
            for (int i = 0; i < neighbors.size(); i++) 
            {
                this->neighbors[i] = new Node(clone->neighbors[i]);
            }

            this->connectsToRoom = clone->connectsToRoom;
            this->roomValue = clone->roomValue;

            this->unit = new Unit(clone->unit);
        }
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

    Node* createRoomNode(Unit* unit, int type) 
    {
        Node* node = new Node();

        node->unit = unit;

        return node;
    }

    void createConnection(Node* nodeA, Node* nodeB) 
    {
        nodeA->neighbors.push_back(nodeB);
        nodeB->neighbors.push_back(nodeA);
    }
    
    std::vector<Node*> parseInput(const std::vector<std::string>& input)
    {
        std::vector<Node*> nodes;
        for (int i = 0; i < 5; i++) 
        {
            nodes.push_back(new Node());
        }

        createLinks(nodes);
        
        Node* roomA_1 = createRoomNode(new Unit(0), 0);
        Node* roomA_2 = createRoomNode(new Unit(1), 0);
                                        
        Node* roomB_1 = createRoomNode(new Unit(0), 1);
        Node* roomB_2 = createRoomNode(new Unit(1), 1);

        createConnection(roomA_1, roomA_2);
        createConnection(roomB_1, roomB_2);

        //define the connections to the rooms
        createConnection(nodes[1], roomA_1);
        nodes[1]->connectsToRoom = true;

        createConnection(nodes[3], roomB_1);
        nodes[3]->connectsToRoom = true;

        nodes.push_back(roomA_1);
        nodes.push_back(roomA_2);
        nodes.push_back(roomB_1);
        nodes.push_back(roomB_2);

        for (int i = 0; i < nodes.size(); i++) 
        {
            nodes[i]->id = i;
        }

        return nodes;
    }

    std::vector<Node*> getNodesWithUnits(const std::vector<Node*>& nodes)
    {
        std::vector<Node*> nodesWithUnits;

        for (const auto& node : nodes) 
        {
            if (node->unit != NULL) 
            {
                nodesWithUnits.push_back(node);
            }
        }

        return nodesWithUnits;
    }

    bool isDestinationValid(const Unit* unit, const Node* targetNode)
    {
        //if the unit is already in the hallway, it can only move if it
        //goes straight to the destination node
        if (unit->hasMovedToHallway)
        {
            return (targetNode->roomValue == unit->unitValue);
        }

        //check if the unit ends up at a node that is connected to a room
        //if that's the case, it should not be allowed (blocks others)
        if (targetNode->connectsToRoom) return false;

        return true;
    }

    void getPossibleMoves(const Node* activeNode, std::vector<Node*>& moves) 
    {
        for (const auto& node : activeNode->neighbors)
        {
            //if there is already a unit on a node, skip
            if (node->unit != NULL) continue;

            //don't backtrack, so check if the index is in there already
            if (std::find(moves.begin(), moves.end(), node) != moves.end()) continue;

            moves.push_back(node);

            //recursively iterate through the next layer
            getPossibleMoves(node, moves);
        }
    }

    std::vector<Node*> getValidatedMoves(const Node* activeNode, const std::vector<Node*>& moves) 
    {
        std::vector<Node*> validatedMoves;

        //loop through all moves; intermediate steps can also be ending locations
        for (const auto& move : moves)
        {
            //check if the move is valid
            bool isValid = isDestinationValid(activeNode->unit, move);
            if (isValid)
            {
                validatedMoves.push_back(move);
            }
        }

        return validatedMoves;
    }

    std::vector<Node*> copy(const std::vector<Node*>& nodes) 
    {
        std::vector<Node*> copiedNodes = std::vector<Node*>(nodes.size());
        for (int i = 0; i < copiedNodes.size(); i++) 
        {
            copiedNodes[i] = new Node(nodes[i]);
        }
        return copiedNodes;
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

    std::vector<std::vector<Node*>> getNextStartingConfigurations(const Node* activeNode, const std::vector<Node*>& nodes)
    {
        //basically you want to find all possible moves that the active node can go to
        //for each of these moves, we create a new set of nodes and apply the moved node

        std::vector<Node*> moves;
        getPossibleMoves(activeNode, moves);

        std::vector<Node*> validatedMoves = getValidatedMoves(activeNode, moves);



        std::vector<std::vector<Node*>> configurations;

        //create a copy of each configuration, but then replace the activeNode with the validatedNode
        //and set all properties as necessary
        for (int i = 0; i < validatedMoves.size(); i++) 
        {
            auto copiedNodes = copy(nodes);

            //get the active node iterator
            int activeIndex = getIndex(activeNode, nodes);
            int targetIndex = getIndex(validatedMoves[i], nodes);

            if (activeIndex == -1 || targetIndex == -1) continue;

            //apply unit to target, and remove the original index unit
            copiedNodes[targetIndex]->unit = new Unit(nodes[activeIndex]->unit);
            copiedNodes[activeIndex]->unit = NULL;

            configurations.push_back(copiedNodes);
        }

        //create a new list of starting configurations
        return configurations;
    }

    bool isConfigurationComplete(const std::vector<Node*> nodes) 
    {
        //retrieve all nodes that have units on them
        std::vector<Node*> nodesWithUnits = getNodesWithUnits(nodes);

        //check if all units are in the right node
        for (const auto& node : nodesWithUnits) 
        {
            if (node->roomValue != node->unit->unitValue) return false;
        }

        return true;
    }

    void processConfiguration(const std::vector<Node*> nodes) 
    {
        //check if the configuration is complete
        if (isConfigurationComplete(nodes)) 
        {
            std::cout << "Configuration complete" << std::endl;
            return;
        }

        //retrieve all nodes that have units on them
        std::vector<Node*> nodesWithUnits = getNodesWithUnits(nodes);

        //for each node, find all configurations that have processed one of the 
        //available moves for that node
        for (const auto& node : nodesWithUnits)
        {
            auto configs = getNextStartingConfigurations(node, nodes);

            //if a configuration is 'invalid', meaning it can't progress, it will simply
            //not regenerate any new starting configurations and will therefore terminate
            for(const auto& config : configs)
            {
                processConfiguration(config);
            }
        }
    }

    std::string getSolutionPart1()
    {
        int result = 0;

        processConfiguration(data);

        return std::to_string(result);
    }

    std::string getSolutionPart2()
    {
        int result = 0;

        return std::to_string(result);
    }

    std::vector<Node*> data;

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