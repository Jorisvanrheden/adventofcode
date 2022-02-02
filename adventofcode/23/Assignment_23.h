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

    //TODO: GLOBALS that need to be processed differently
    std::map<int, std::vector<Node*>> roomMap;
    std::map<std::string, int> history;

    

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

    //TODO: make this dynamic for both part 1 and 2
    std::vector<Node*> createRoomNodes(int roomValue, int nodeCount) 
    {
        std::vector<Node*> roomNodes(nodeCount);
        for (int i = 0; i < roomNodes.size(); i++) 
        {
            Node* node = new Node();
            node->roomValue = roomValue;

            roomNodes[i] = node;
        }

        createLinks(roomNodes);

        roomMap[roomValue] = roomNodes;

        return roomNodes;
    }

    int roomNameToID(const std::string& name) 
    {
        if      (name == "A") return 0;
        else if (name == "B") return 1;
        else if (name == "C") return 2;
        else if (name == "D") return 3;
        
        return -1;
    }

    std::string IDToRoomName(int id)
    {
        if      (id == 0) return "A";
        else if (id == 1) return "B";
        else if (id == 2) return "C";
        else if (id == 3) return "D";

        return ".";
    }
    
    Input parseInput(const std::vector<std::string>& input)
    {
        Input data;

        const int NODE_COUNT = 11;
        const int ROOM_COUNT = 4;
        std::vector<int> connectionIndices = { 2, 4, 6, 8 };

        std::vector<Node*> nodes;
        for (int i = 0; i < NODE_COUNT; i++)
        {
            nodes.push_back(new Node());
        }

        createLinks(nodes);


        //parse input file to determine amount of nodes in each room
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

        for (int i = 0; i < roomValues.size(); i++)
        {
            std::vector<Node*> roomNodes = createRoomNodes(i, roomValues[i].size());

            std::vector<Node*> nodeLinks = { nodes[connectionIndices[i]], roomNodes[0] };
            createLinks(nodeLinks);

            nodes[connectionIndices[i]]->connectsToRoom = true;

            nodes.insert(nodes.end(), roomNodes.begin(), roomNodes.end());
        }

        std::vector<Unit> map(nodes.size());
        for (int i = 0; i < nodes.size(); i++) 
        {
            nodes[i]->id = i;

            map[i] = Unit(-1);
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

    std::vector<Node*> getRoomNodes(const std::vector<Node*>& nodes, int roomValue) 
    {
        if (roomMap.find(roomValue) != roomMap.end()) 
        {
            return roomMap[roomValue];
        }

        return {};
    }

    bool isRoomComplete(const std::vector<Node*>& roomNodes, const std::vector<Unit>& map)
    {
        for (const auto& node : roomNodes)
        {
            //check if the node contains a unit with the same roomValue as the node
            Unit unit = map[node->id];
            if (!isValidUnit(unit)) return false;

            if (unit.roomValue != node->roomValue) return false;
        }

        return true;
    }

    bool isDestinationValid(const Node* activeNode, const Node* targetNode, const std::vector<Node*>& nodes, const std::vector<Unit>& map)
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

        //detect whether the target node is in a room
        if (unit.roomValue == targetNode->roomValue) 
        {
            std::vector<Node*> roomNodes = getRoomNodes(nodes, targetNode->roomValue);

            //if the room is already complete, don't process the move
            if (isRoomComplete(roomNodes, map)) return false;

            //corner position validation
            if (activeNode->id == roomNodes[roomNodes.size() - 1]->id) return false;

            //if the room is not complete yet, the move might be considered, but depends on the following checks:
            //- if the target position is valid, but it blocks another unit that needs to leave the room: INVALID
            //- if the target position is valid, the active unit is already in the room, it can only move to the 'deepest' node
            //- if the active unit is already at it's 'deepest' position, it shouldn't move again
            
            //nodes are added sequentially, so the next room node always has an ID of (previousID + 1)
            
            //loop backwards, for each iteration check:
            //- is the current node NOT empty and is the unit of that node NOT in the room right, return false
            int endIndex = targetNode->id - roomNodes[0]->id;
            for (int i = roomNodes.size() - 1; i >= endIndex; i--)
            {
                Unit unit = map[roomNodes[i]->id];
                if (isValidUnit(unit))
                {
                    if (unit.roomValue != roomNodes[i]->roomValue) return false;
                }

                //if the roomNode id is lower than the active node id, it moves up, which isn't allowed in a room
                if (roomNodes[i]->id < activeNode->id) return false;
            }
        }
        
        //don't move from one room directly into another, first has to go to the hallway
        if (activeNode->roomValue != -1 && targetNode->roomValue != -1) 
        {
            if (activeNode->roomValue != targetNode->roomValue) return false;
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

    std::vector<Move> getValidatedMoves(const Node* activeNode, const std::vector<Node*>& nodes, const std::vector<Move>& moves, const std::vector<Unit>& map)
    {
        std::vector<Move> validatedMoves;

        //loop through all moves; intermediate steps can also be ending locations
        for (const auto& move : moves)
        {
            //check if the move is valid
            bool isValid = isDestinationValid(activeNode, move.target, nodes, map);
            if (isValid)
            {
                validatedMoves.push_back(move);
            }
        }

        return validatedMoves;
    }

    //TODO: 
    //IDEA: instead of storing the full configuration, 
    // what if you only store the steps to get to that configuration?
    // a couple of from-to actions are stored and that should be about it 
    // I guess that is easier to backtrack
    // and how would that go with memory?
    // something to think about
    struct MoveSet 
    {
        int from;
        int to;

        MoveSet(int from, int to)
            : from(from), to(to)
        {
            
        }
    };
    struct MoveEntry 
    {
        std::vector<Unit> map;
        std::vector<MoveSet> moves;
    };
    std::vector<MoveEntry> getNextStartingConfigurations(const Node* activeNode, const std::vector<Node*>& nodes, const MoveEntry& map)
    {
        //basically you want to find all possible moves that the active node can go to
        //for each of these moves, we create a new set of nodes and apply the moved node
        std::vector<Move> moves;
        getPossibleMoves(activeNode, moves, map.map, 1);

        std::vector<Move> validatedMoves = getValidatedMoves(activeNode, nodes, moves, map.map);

        std::vector<MoveEntry> configurations(validatedMoves.size());

        //create a copy of each configuration, but then replace the activeNode with the validatedNode
        //and set all properties as necessary
        for (int i = 0; i < validatedMoves.size(); i++) 
        {
            MoveEntry entry = map;

            //get the active node iterator
            int activeIndex = activeNode->id; 
            int targetIndex = validatedMoves[i].target->id;

            //apply unit to target, and remove the original index unit
            Unit unit = map.map[activeIndex];
            unit.hasMovedToHallway = true;
            unit.totalDistance += validatedMoves[i].distance;

            entry.map[targetIndex] = unit;
            entry.map[activeIndex] = Unit(-1);

            entry.moves.push_back(MoveSet(activeIndex, targetIndex));

            configurations[i] = entry;
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

    bool hasBetterConfiguration(int cost, const std::vector<Unit>& map) 
    {
        //check if the current configuration has already been executed once
        std::string key = mapToKey(map);
        if (history.find(key) == history.end())
        {
            history[key] = cost;
        }
        else
        {
            //if it has, check the cost of that configuration
            //if the current cost is lower, overwrite the cost and continue
            //otherwise return, because a more cheaper option is already considered
            if (cost >= history[key])
            {
                return true;
            }
            else
            {
                history[key] = cost;
            }
        }

        return false;
    }

    void printMap(const std::vector<Unit>& map)
    {
        const int HALLWAY_LENGTH = 11;
        std::vector<int> connections = { 2,4,6,8 };

        std::cout << "\n" << std::endl;

        std::string hallway;
        for (int i = 0; i < HALLWAY_LENGTH; i++)
        {
            Unit unit = map[i];
            hallway += IDToRoomName(unit.roomValue);
        }
        std::cout << hallway << std::endl;

        int nodesPerRoom = (map.size() - HALLWAY_LENGTH) / 4;

        for (int n = 0; n < nodesPerRoom; n++) 
        {
            std::string roomLine(HALLWAY_LENGTH, '#');
            for (int i = 0; i < HALLWAY_LENGTH; i++)
            {
                //overwrite indices in roomline with unit values
                for (int c = 0; c < connections.size(); c++) 
                {
                    if (i == connections[c])
                    {
                        int index = HALLWAY_LENGTH + c * nodesPerRoom + n;
                        Unit unit = map[index];
                        roomLine[i] = IDToRoomName(unit.roomValue)[0];
                    }
                }
            }
            std::cout << roomLine << std::endl;
        }

        std::cout << "\n" << std::endl;
    }

    void printProgress(const std::vector<MoveSet>& moves, std::vector<Unit> originalMap) 
    {
        for (const auto& move : moves) 
        {
            //apply unit to target, and remove the original index unit
            Unit unit = originalMap[move.from];

            originalMap[move.to]    = unit;
            originalMap[move.from]  = Unit(-1);

            printMap(originalMap);
        }
    }

    void processConfiguration(const std::vector<Node*> nodes, const MoveEntry& entry, int& lowestCost)
    {
        int cost = getCost(entry.map);

        if (hasBetterConfiguration(cost, entry.map)) return;
        if (cost >= lowestCost) return;

        //check if the configuration is complete
        if (isConfigurationComplete(nodes, entry.map))
        {
            if (cost < lowestCost)
            {
                lowestCost = cost;

                std::cout << "Lowest cost so far: " << lowestCost << std::endl;
            }

            return;
        }

        //retrieve all nodes that have units on them
        std::vector<Node*> nodesWithUnits = getNodesWithUnits(nodes, entry.map);

        //for each node, find all configurations that have processed one of the 
        //available moves for that node
        for (const auto& node : nodesWithUnits)
        {
            auto configs = getNextStartingConfigurations(node, nodes, entry);

            //if a configuration is 'invalid', meaning it can't progress, it will simply
            //not regenerate any new starting configurations and will therefore terminate
            for(const auto& config : configs)
            {
                processConfiguration(nodes, config, lowestCost);
            }
        }
    }

    int getLowestCostConfiguration(const Input& input) 
    {
        int lowestCost = INT_MAX;

        MoveEntry entry;
        entry.map = input.map;
        entry.moves = {};

        processConfiguration(input.nodes, entry, lowestCost);

        return lowestCost;
    }

    std::string getSolutionPart1()
    {
        int result = 0;

        result = getLowestCostConfiguration(data);

        return std::to_string(result);
    }

    std::string getSolutionPart2()
    {
        int result = 0;

        result = getLowestCostConfiguration(data);

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
        return "23/input_2";
    }
};