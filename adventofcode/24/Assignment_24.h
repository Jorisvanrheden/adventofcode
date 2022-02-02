#pragma once

#include "Assignment.h"

class Assignment_24 : public Assignment
{
private:
    struct ALU
    {
        unsigned long long w = 0;
        unsigned long long x = 0;
        unsigned long long y = 0;
        unsigned long long z = 0;

        unsigned long long& getParam(int type)
        {
            if      (type == 0) return w;
            else if (type == 1) return x;
            else if (type == 2) return y;
            else if (type == 3) return z;
        }
    };

    struct IValueGetter 
    {
        virtual unsigned long long& getValue(ALU& alu) = 0;
    };

    struct ParamValueGetter : IValueGetter
    {
        int type;

        ParamValueGetter(int type)
            : type(type)
        {

        }

        unsigned long long& getValue(ALU& alu)
        {
            return alu.getParam(type);
        }
    };

    struct DefaultValueGetter : IValueGetter
    {
        unsigned long long value;

        DefaultValueGetter(unsigned long long value)
            : value(value)
        {
        
        }

        unsigned long long& getValue(ALU& alu)
        {
            return value;
        }
    };

    struct IInstruction 
    {
        virtual void process(ALU& alu, unsigned long long value, int& index) = 0;
    };

    struct InputInstruction : IInstruction
    {
        int type;

        InputInstruction(int type)
            : type(type)
        {
        }

        void process(ALU& alu, unsigned long long value, int& index)
        {
            unsigned long long& param = alu.getParam(type);
            param = value;

            index++;
        }
    };

    struct AddInstruction : IInstruction
    {
        IValueGetter* getter1;
        IValueGetter* getter2;

        AddInstruction(IValueGetter* getter1, IValueGetter* getter2)
            : getter1(getter1), getter2(getter2)
        {
        }

        void process(ALU& alu, unsigned long long value, int& index)
        {
            unsigned long long& param1 = getter1->getValue(alu);
            unsigned long long& param2 = getter2->getValue(alu);

            param1 += param2;
        }
    };

    struct MultiplyInstruction : IInstruction
    {
        IValueGetter* getter1;
        IValueGetter* getter2;

        MultiplyInstruction(IValueGetter* getter1, IValueGetter* getter2)
            : getter1(getter1), getter2(getter2)
        {
        }

        void process(ALU& alu, unsigned long long value, int& index)
        {
            unsigned long long& param1 = getter1->getValue(alu);
            unsigned long long& param2 = getter2->getValue(alu);

            param1 *= param2;
        }
    };

    struct ModInstruction : IInstruction
    {
        IValueGetter* getter1;
        IValueGetter* getter2;

        ModInstruction(IValueGetter* getter1, IValueGetter* getter2)
            : getter1(getter1), getter2(getter2)
        {
        }

        void process(ALU& alu, unsigned long long value, int& index)
        {
            unsigned long long& param1 = getter1->getValue(alu);
            unsigned long long& param2 = getter2->getValue(alu);

            if (param1 < 0)
            {
                std::cout << "Modulo operation error A" << std::endl;
            }
            if (param2 <= 0)
            {
                std::cout << "Modulo operation error B" << std::endl;
            }

            param1 = param1 % param2;
        }
    };

    struct DivInstruction : IInstruction
    {
        IValueGetter* getter1;
        IValueGetter* getter2;

        DivInstruction(IValueGetter* getter1, IValueGetter* getter2)
            : getter1(getter1), getter2(getter2)
        {
        }

        void process(ALU& alu, unsigned long long value, int& index)
        {
            unsigned long long& param1 = getter1->getValue(alu);
            unsigned long long& param2 = getter2->getValue(alu);
            
            if (param2 == 0) 
            {
                std::cout << "Can't divide by 0" << std::endl;
            }

            param1 = trunc((float)param1 / (float)param2);
        }
    };

    struct EqualsInstruction : IInstruction
    {
        IValueGetter* getter1;
        IValueGetter* getter2;

        EqualsInstruction(IValueGetter* getter1, IValueGetter* getter2)
            : getter1(getter1), getter2(getter2)
        {
        }

        void process(ALU& alu, unsigned long long value, int& index)
        {
            unsigned long long& param1 = getter1->getValue(alu);
            unsigned long long& param2 = getter2->getValue(alu);

            param1 = (param1 == param2);
        }
    };

    int typeToParam(const std::string& type) 
    {
        if      (type == "w") return 0;
        else if (type == "x") return 1;
        else if (type == "y") return 2;
        else if (type == "z") return 3;

        return -1;
    }

    std::vector<IInstruction*> parseInput(const std::vector<std::string>& input)
    {
        std::vector<IInstruction*> data;

        for (const auto& entry : input)
        {
            std::vector<std::string> parts = Utilities::splitString(entry, " ");

            //process an input instruction
            if (parts.size() == 2) 
            {
                int param = typeToParam(parts[1]);
                data.push_back(new InputInstruction(param));
            }
            //process a mul/add/mod/div instruction
            if (parts.size() == 3) 
            {
                std::string type = parts[0];

                int param1 = typeToParam(parts[1]);
                int param2 = typeToParam(parts[2]);

                IValueGetter* valueGetter1 = new ParamValueGetter(param1);
                IValueGetter* valueGetter2;

                //check if the instruction has a var or value for the second param
                if (param2 == -1) 
                {
                    valueGetter2 = new DefaultValueGetter(std::stoi(parts[2]));
                }
                else 
                {
                    valueGetter2 = new ParamValueGetter(param2);
                }

                if      (type == "add") data.push_back(new AddInstruction(valueGetter1, valueGetter2));
                else if (type == "mul") data.push_back(new MultiplyInstruction(valueGetter1, valueGetter2));
                else if (type == "div") data.push_back(new DivInstruction(valueGetter1, valueGetter2));
                else if (type == "mod") data.push_back(new ModInstruction(valueGetter1, valueGetter2));
                else if (type == "eql") data.push_back(new EqualsInstruction(valueGetter1, valueGetter2));
            }
        }

        return data;
    }

    void printAlu(const ALU& alu) 
    {
        std::cout << alu.w << ", " << alu.x << ", " << alu.y << ", " << alu.z << std::endl;
    }

    ALU processModelNumber(const std::string modelNumber, std::vector<IInstruction*>& instructions)
    {
        int index = 0;

        ALU alu;

        //process all instructions
        for (auto& instruction : instructions) 
        {
            int modelInput = Utilities::charToInt(modelNumber[index]);

            instruction->process(alu, modelInput, index);
        }

        return alu;
    }

    char findBestNumberAtIndex(int index, std::string input) 
    {
        unsigned long long score = LLONG_MAX;
        char bestChar = input[index];

        for (int j = 1; j <= 9; j++)
        {
            char tempChar = Utilities::intToChar(j);
            input[index] = tempChar;

            unsigned long long newScore = processModelNumber(input, data).z;
            if (newScore < score)
            {
                score = newScore;
                bestChar = tempChar;
            }
        }
        return bestChar;
    }

    std::string getModelNumber(std::string input) 
    {
        //for each of the indices, iterate the number and check the z-score
        for (int i = 0; i < input.size(); i++)
        {
            char bestNumber = findBestNumberAtIndex(i, input);

            input[i] = bestNumber;
        }

        return input;
    }

    std::string getSolutionPart1()
    {
        int result = 0;

        std::string digit = "11111111111111";

        while (true) 
        {
            for (int i = 0; i < digit.size(); i++)
            {
                digit[i] = findBestNumberAtIndex(i, digit);

                std::cout << digit << ": ";
                printAlu(processModelNumber(digit, data));
            }
        }

        

        //TODO
        //- set requirements for when each next input starts
        //- backtrack (start from the last index)

        //Findings:
        //- every block always starts with;
        // inp w
        // mul x 0
        // add x z
        // mod x 26

        return std::to_string(result);
    }

    std::string getSolutionPart2()
    {
        int result = 0;

        return std::to_string(result);
    }

    std::vector<IInstruction*> data;

public:
    void initialize(const std::vector<std::string>& input)
    {
        data = parseInput(input);
    }

    std::string getInput()
    {
        return "24/input";
    }
};