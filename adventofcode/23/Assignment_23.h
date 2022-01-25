#pragma once

#include "Assignment.h"

class Assignment_23 : public Assignment
{
private:
    struct Unit 
    {
        //integer to represent what unit is occupying the node
        //values:
        //-1 = no type
        // 0 = type A
        // 1 = type B
        // 2 = type C
        // 3 = type D
        int roomValue;

        //once a unit stops in the hallway, it will stay in that spot 
        //until it can move into its destination room
        bool hasMovedToHallway = false;

        int totalDistance = 0;

        Unit() {}
        Unit(int roomValue)
            : roomValue(roomValue)
        {
            
        }

        int getCost() const
        {
            int multiplier = 1;

            if      (roomValue == 0) multiplier = 1;
            else if (roomValue == 1) multiplier = 10;
            else if (roomValue == 2) multiplier = 100;
            else if (roomValue == 3) multiplier = 1000;

            return multiplier * totalDistance;
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
        std::vector<Unit> map;
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

    //TODO: make this dynamic for both part 1 and 2
    std::vector<Node*> createRoomNodes(int roomValue) 
    {
        Node* node1 = new Node();
        Node* node2 = new Node();

        node1->roomValue = roomValue;
        node2->roomValue = roomValue;

        createConnection(node1, node2);

        return { node1, node2 };
    }

    int roomNameToID(const std::string& name) 
    {
        if      (name == "A") return 0;
        else if (name == "B") return 1;
        else if (name == "C") return 2;
        else if (name == "D") return 3;
        
        return -1;
    }
    
    Input parseInput(const std::vector<std::string>& input)
    {
        Input data;

        const int NODE_COUNT = 11;
        const int ROOM_COUNT = 4;

        std::vector<Node*> nodes;
        for (int i = 0; i < NODE_COUNT; i++)
        {
            nodes.push_back(new Node());
        }

        createLinks(nodes);

        //TODO: make this dynamic based on the input file, don't hard code it
        auto roomA = createRoomNodes(0);
        auto roomB = createRoomNodes(1);
        auto roomC = createRoomNodes(2);
        auto roomD = createRoomNodes(3);

        //define the connections to the rooms
        createConnection(nodes[2], roomA[0]);
        nodes[2]->connectsToRoom = true;

        createConnection(nodes[4], roomB[0]);
        nodes[4]->connectsToRoom = true;

        createConnection(nodes[6], roomC[0]);
        nodes[6]->connectsToRoom = true;

        createConnection(nodes[8], roomD[0]);
        nodes[8]->connectsToRoom = true;

        nodes.insert(nodes.end(), roomA.begin(), roomA.end());
        nodes.insert(nodes.end(), roomB.begin(), roomB.end());
        nodes.insert(nodes.end(), roomC.begin(), roomC.end());
        nodes.insert(nodes.end(), roomD.begin(), roomD.end());

        std::vector<Unit> map(nodes.size());
        for (int i = 0; i < nodes.size(); i++) 
        {
            nodes[i]->id = i;

            map[i] = Unit(-1);
        }

        std::vector<std::vector<int>> roomValues(ROOM_COUNT);
        for (int i = 0; i < roomValues.size(); i++) 
        {
            roomValues[i] = std::vector<int>();
        }

        for (const auto& line : input) 
        {
            std::string nLine = Utilities::trim(line, " \n\r\t\f\v");
            std::vector<std::string> parts = Utilities::splitString(nLine, "#");
            if (parts.size() >= ROOM_COUNT)
            {
                for (int i = 0; i < ROOM_COUNT; i++)
                {
                    int id = roomNameToID(parts[i]);

                    roomValues[i].push_back(id);
                }
            }
        }

        int roomUnitIndex = NODE_COUNT;
        for (int i = 0; i < roomValues.size(); i++) 
        {
            auto roomUnits = roomValues[i];
            for (const auto& unit : roomUnits) 
            {
                map[roomUnitIndex] = Unit(unit);

                roomUnitIndex++;
            }
        }

        data.nodes = nodes;
        data.map = map;
        return data;
    }

    bool isValidUnit(const Unit& unit) 
    {
        return unit.roomValue != -1;
    }

    std::vector<Node*> getNodesWithUnits(const std::vector<Node*>& nodes, const std::vector<Unit>& map)
    {
        std::vector<Node*> nodesWithUnits;

        for (const auto& node : nodes) 
        {
            if (isValidUnit(map[node->id]))
            {
                nodesWithUnits.push_back(node);
            }
        }

        return nodesWithUnits;
    }

    bool isRoomComplete(const Node* node, const std::vector<Unit>& map)
    {
        for (const auto& neighbor : node->neighbors)
        {
            //check if the neighboring nodes in the same room
            if (neighbor->roomValue == node->roomValue)
            {
                //if there is no unit on the node, the room is not complete
                Unit neighborUnit = map[neighbor->id];
                if (!isValidUnit(neighborUnit)) return false;

                if (neighborUnit.roomValue != node->roomValue) return false;
            }
        }
        return true;
    }

    bool isDestinationValid(const Node* activeNode, const Node* targetNode, const std::vector<Unit>& map)
    {
        Unit unit = map[activeNode->id];

        //check if the unit ends up at a node that is connected to a room
        //if that's the case, it should not be allowed (blocks others)
        if (targetNode->connectsToRoom) return false;

        //if the unit has already moved, the next move should be the destination room
        if (unit.hasMovedToHallway) 
        {
            if (unit.roomValue != targetNode->roomValue) return false;
        }

        //check if the active and target are both in the room already
        //if they are, the only valid move is to go to the node that only has 1 neigbor
        //it's basically allowing moving from the top room position to the bottom room position
        if (activeNode->roomValue == targetNode->roomValue)
        {
            if (targetNode->neighbors.size() > 1) return false;
        }

        //if the unit is on the active node is already in the correct position, don't process move
        if (unit.roomValue == activeNode->roomValue)
        {
            //corner position validation
            if (activeNode->neighbors.size() == 1) return false;

            //check if the room is complete
            if (isRoomComplete(activeNode, map)) return false;

            //else if the active node is in the right location, and the corner position is empty,
            //that should be the only allowed move
            Unit neighborUnit = map[activeNode->neighbors[0]->id];
            if (!isValidUnit(neighborUnit)) 
            {
                //if the target position is not this neighboring node, then don't allow the move
                if (targetNode != activeNode->neighbors[0]) return false;
            }
        }

        //if the destination target is of the correct type,
        //the unit should not block any non-correct units in that room
        if (unit.roomValue == targetNode->roomValue) 
        {
            for (const auto& neighbor : targetNode->neighbors) 
            {
                //find the neighbor with the same room value
                if (neighbor->roomValue == neighbor->roomValue) 
                {
                    if (neighbor->neighbors.size() == 1) 
                    {
                        Unit neighborUnit = map[neighbor->id];
                        if (isValidUnit(neighborUnit))
                        {
                            if (neighborUnit.roomValue != targetNode->roomValue) return false;
                        }
                    }
                }
            }
        }

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

    void getPossibleMoves(const Node* activeNode, std::vector<Move>& moves, const std::vector<Unit>& map, int distance)
    {
        for (const auto& node : activeNode->neighbors)
        {
            //if there is already a unit on a node, skip
            if (isValidUnit(map[node->id])) continue;

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

    std::vector<Move> getValidatedMoves(const Node* activeNode, const std::vector<Move>& moves, const std::vector<Unit>& map)
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

    std::vector<std::vector<Unit>> getNextStartingConfigurations(const Node* activeNode, const std::vector<Node*>& nodes, const std::vector<Unit>& map)
    {
        //basically you want to find all possible moves that the active node can go to
        //for each of these moves, we create a new set of nodes and apply the moved node
        std::vector<Move> moves;
        getPossibleMoves(activeNode, moves, map, 1);

        std::vector<Move> validatedMoves = getValidatedMoves(activeNode, moves, map);

        std::vector<std::vector<Unit>> configurations(validatedMoves.size());

        //create a copy of each configuration, but then replace the activeNode with the validatedNode
        //and set all properties as necessary
        for (int i = 0; i < validatedMoves.size(); i++) 
        {
            std::vector<Unit> copy = map;
            
            //get the active node iterator
            int activeIndex = activeNode->id; 
            int targetIndex = validatedMoves[i].target->id;

            //apply unit to target, and remove the original index unit
            Unit unit = map[activeIndex];
            unit.hasMovedToHallway = true;
            unit.totalDistance += validatedMoves[i].distance;

            copy[targetIndex] = unit;
            copy[activeIndex].roomValue = -1;

            configurations[i] = copy;
        }

        //create a new list of starting configurations
        return configurations;
    }

    bool isConfigurationComplete(const std::vector<Node*> nodes, const std::vector<Unit>& map)
    {
        //retrieve all nodes that have units on them
        std::vector<Node*> nodesWithUnits = getNodesWithUnits(nodes, map);

        //check if all units are in the right node
        for (const auto& node : nodesWithUnits) 
        {
            Unit unit = map[node->id];
            if (!isValidUnit(unit)) return false;

            if (node->roomValue != unit.roomValue) return false;
        }

        return true;
    }

    int lowestCost = INT_MAX;

    std::string mapToKey(const std::vector<Unit>& map) 
    {
        std::string key;
        for (const auto& unit : map) 
        {
            std::string input = "x";

            if (isValidUnit(unit))
            {
                input = std::to_string(unit.roomValue);
            }

            input += ";";

            key += input;
        }
        return key;
    }

    int getCost(const std::vector<Unit>& map)
    {
        int totalCost = 0;
        for (const auto& unit : map)
        {
            if (isValidUnit(unit))
            {
                totalCost += unit.getCost();
            }
        }
        return totalCost;
    }

    std::map<std::string, int> history;

    void processConfiguration(const std::vector<Node*> nodes, const std::vector<Unit>& map) 
    {
        int totalCost = getCost(map);

        //check if the current configuration has already been executed once
        std::string key = mapToKey(map);
        if (history.find(key) == history.end()) 
        {
            history[key] = totalCost;
        }
        else 
        {
            //if it has, check the cost of that configuration
            //if the current cost is lower, overwrite the cost and continue
            //otherwise return, because a more cheaper option is already considered
            if (totalCost >= history[key]) 
            {
                return;
            }
            else 
            {
                history[key] = totalCost;
            }
        }


        if (totalCost >= lowestCost) return;

        //check if the configuration is complete
        if (isConfigurationComplete(nodes, map))
        {
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
        return "23/input_example_2";
    }
};