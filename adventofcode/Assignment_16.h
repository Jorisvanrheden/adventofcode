#pragma once

#include "Assignment.h"
#include <algorithm>
#include <map>

class Assignment_16 : public Assignment
{
private:

    struct LiteralPacket
    {
        int version;
        int typeID;

        std::string binary;
        unsigned long long value;

        std::vector<LiteralPacket> packets = {};

        LiteralPacket(int version, int typeID, const std::string& binary, unsigned long long value, const std::vector<LiteralPacket>& packets)
            : version(version), typeID(typeID), binary(binary), value(value), packets(packets)
        {
            
        }
    };

    struct IScoreProcessor
    {
        virtual unsigned long long process(const std::vector<LiteralPacket>& packets) = 0;

        static IScoreProcessor* getNodeScoreByType(std::vector<LiteralPacket> packets, int type)
        {
            switch (type)
            {
            case 0:
                return new ScoreSum();
            case 1:
                return new ScoreProduct();
            case 2:
                return new ScoreMin();
            case 3:
                return new ScoreMax();
            case 4:
                return NULL;
            case 5:
                return new ScoreGreaterThan();
            case 6:
                return new ScoreLessThan();
            case 7:
                return new ScoreEqual();
            }

            return NULL;
        }

        static unsigned long long getPacketValue(const LiteralPacket& packet) 
        {
            if (packet.packets.size() > 0) 
            {
                IScoreProcessor* processor = getNodeScoreByType(packet.packets, packet.typeID);
                return processor->process(packet.packets);
            }
            else 
            {
                return packet.value;
            }
        }
    };

    struct ScoreSum : IScoreProcessor
    {
        unsigned long long process(const std::vector<LiteralPacket>& packets)
        {
            unsigned long long value = 0;
            for (const auto& packet : packets)
            {
                value += getPacketValue(packet);
            }
            return value;
        }
    };

    struct ScoreProduct : IScoreProcessor
    {
        unsigned long long process(const std::vector<LiteralPacket>& packets)
        {
            unsigned long long value = getPacketValue(packets[0]);
            for (int i = 1; i < packets.size(); i++)
            {
                value *= getPacketValue(packets[i]);
            }
            return value;
        }
    };

    struct ScoreMin : IScoreProcessor
    {
        unsigned long long process(const std::vector<LiteralPacket>& packets)
        {
            std::vector<unsigned long long> values;
            for (const auto& packet : packets) values.push_back(getPacketValue(packet));
            std::sort(values.begin(), values.end());
            return values[0];
        }
    };

    struct ScoreMax : IScoreProcessor
    {
        unsigned long long process(const std::vector<LiteralPacket>& packets)
        {
            std::vector<unsigned long long> values;
            for (const auto& packet : packets) values.push_back(getPacketValue(packet));
            std::sort(values.begin(), values.end());
            return values[values.size() - 1];
        }
    };

    struct ScoreGreaterThan : IScoreProcessor
    {
        unsigned long long process(const std::vector<LiteralPacket>& packets)
        {
            if (getPacketValue(packets[0]) > getPacketValue(packets[1])) return 1;
            else return 0;
        }
    };

    struct ScoreLessThan : IScoreProcessor
    {
        unsigned long long process(const std::vector<LiteralPacket>& packets)
        {
            if (getPacketValue(packets[0]) < getPacketValue(packets[1])) return 1;
            else return 0;
        }
    };

    struct ScoreEqual : IScoreProcessor
    {
        unsigned long long process(const std::vector<LiteralPacket>& packets)
        {
            if (getPacketValue(packets[0]) == getPacketValue(packets[1])) return 1;
            else return 0;
        }
    };

    std::map<char, std::string> hexToBinaryMap = 
    {
        {'0', "0000"},
        {'1', "0001"},
        {'2', "0010"},
        {'3', "0011"},
        {'4', "0100"},
        {'5', "0101"},
        {'6', "0110"},
        {'7', "0111"},
        {'8', "1000"},
        {'9', "1001"},
        {'A', "1010"},
        {'B', "1011"},
        {'C', "1100"},
        {'D', "1101"},
        {'E', "1110"},
        {'F', "1111"}
    };

    std::string hexToBinary(const std::string& hex) 
    {
        std::string binary;

        for (const auto& c : hex) 
        {
            if (hexToBinaryMap.find(c) != hexToBinaryMap.end()) 
            {
                binary += hexToBinaryMap.at(c);
            }
        }

        return binary;
    }

    unsigned long long binaryToDecimal(const std::string& binary) 
    {
        return std::stoll(binary, 0, 2);
    }

    std::string readNextBits(const std::string& input, int& index, int length)
    {
        std::string binary = input.substr(index, length);

        //increment the pointer
        index += length;

        return binary;
    }

    std::string parseLiteral(const std::string& binary, int& index) 
    {
        std::string literalValue;

        while (true)
        {
            std::string chunk = readNextBits(binary, index, 5);

            if (chunk.size() > 0)
            {
                literalValue += chunk.substr(1, chunk.size() - 1);

                if (chunk[0] == '0') break;
            }
            else break;
        }

        return literalValue;
    }

   std::vector<LiteralPacket> processOperatorPacket(const std::string& binary, int& index)
    {
        //The first bit of an operator packet indicates the 'length type ID'
        //There are two types of length type ID:
        //- 0
        //- 1
        std::string lengthTypeID = readNextBits(binary, index, 1);

        std::vector<LiteralPacket> packages;

        if (lengthTypeID == "0")
        {
            unsigned long long bits = binaryToDecimal(readNextBits(binary, index, 15));

            int cachedIndex = index;

            //parse new packages until the assigned amount of bits run out
            while (abs(index - cachedIndex) < bits)
            {
                LiteralPacket p = parsePackets(binary, index);
                packages.push_back(p);
            }

        }
        else if (lengthTypeID == "1")
        {
            unsigned long long packetCount = binaryToDecimal(readNextBits(binary, index, 11));

            for (int i = 0; i < packetCount; i++)
            {
                LiteralPacket p = parsePackets(binary, index);
                packages.push_back(p);
            }
        }

        return packages;
    }

    LiteralPacket parsePackets(const std::string& binary, int& index)
    {
        //first three bits of each packet is the packet version
        unsigned long long version = binaryToDecimal(readNextBits(binary, index, 3));
        unsigned long long typeID = binaryToDecimal(readNextBits(binary, index, 3));

        if (typeID == 4) 
        {
            std::string literalValue = parseLiteral(binary, index);

            //std::cout << binaryToDecimal(literalValue) << std::endl;
            return LiteralPacket(version, typeID, literalValue, binaryToDecimal(literalValue), {});
        }
        else 
        {
            std::vector <LiteralPacket> children = processOperatorPacket(binary, index);

            return LiteralPacket(version, typeID, "", 0, children);
        }
    }

    int getNodeVersionScore(LiteralPacket packet) 
    {
        int total = 0;

        total += packet.version;
        for (int i = 0; i < packet.packets.size(); i++) 
        {
            total += getNodeVersionScore(packet.packets[i]);
        }

        return total;
    }

    std::string getSolutionPart1()
    {
        //keep track of a index pointer to traverse through the string
        int index = 0;
        std::string binary = hexToBinary(data[0]);

        //store all packets
        LiteralPacket mainPacket = parsePackets(binary, index);

        int result = getNodeVersionScore(mainPacket);

        return std::to_string(result);
    }

    std::string getSolutionPart2()
    {
        //keep track of a index pointer to traverse through the string
        int index = 0;
        std::string binary = hexToBinary(data[0]);

        //store all packets
        LiteralPacket mainPacket = parsePackets(binary, index);

        unsigned long long result = IScoreProcessor::getPacketValue(mainPacket);

        return std::to_string(result);
    }

    std::vector<std::string> data;

public:
    void initialize()
    {
        data = Utilities::readFile("../16/input");   
    }
};