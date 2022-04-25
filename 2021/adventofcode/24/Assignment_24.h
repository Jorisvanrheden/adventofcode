#pragma once

#include "Assignment.h"

class Assignment_24 : public Assignment
{
private:
    struct ALU
    {
        long long w = 0;
        long long x = 0;
        long long y = 0;
        long long z = 0;

        long long& getParam(int type)
        {
            if      (type == 0) return w;
            else if (type == 1) return x;
            else if (type == 2) return y;
            else if (type == 3) return z;
        }
    };

    struct IValueGetter 
    {
        virtual long long& getValue(ALU& alu) = 0;
    };

    struct ParamValueGetter : IValueGetter
    {
        int type;

        ParamValueGetter(int type)
            : type(type)
        {

        }

        long long& getValue(ALU& alu)
        {
            return alu.getParam(type);
        }
    };

    struct DefaultValueGetter : IValueGetter
    {
        long long value;

        DefaultValueGetter(long long value)
            : value(value)
        {
        
        }

        long long& getValue(ALU& alu)
        {
            return value;
        }
    };

    struct IInstruction 
    {
        virtual void process(ALU& alu, long long value, int& index) = 0;
        virtual long long reverse(ALU& alu, long long input) { return input; };
    };

    struct InstructionGroup 
    {
        std::vector<IInstruction*> instructions;
    };

    struct InputInstruction : IInstruction
    {
        IValueGetter* getter;

        InputInstruction(IValueGetter* getter)
            : getter(getter)
        {
        }

        void process(ALU& alu, long long value, int& index)
        {
            long long& param = getter->getValue(alu);
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

        void process(ALU& alu, long long value, int& index)
        {
            long long& param1 = getter1->getValue(alu);
            long long& param2 = getter2->getValue(alu);

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

        void process(ALU& alu, long long value, int& index)
        {
            long long& param1 = getter1->getValue(alu);
            long long& param2 = getter2->getValue(alu);

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

        void process(ALU& alu, long long value, int& index)
        {
            long long& param1 = getter1->getValue(alu);
            long long& param2 = getter2->getValue(alu);

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

        void process(ALU& alu, long long value, int& index)
        {
            long long& param1 = getter1->getValue(alu);
            long long& param2 = getter2->getValue(alu);
            
            if (param2 == 0) 
            {
                std::cout << "Can't divide by 0" << std::endl;
            }

            param1 = trunc((float)param1 / (float)param2);
        }

        long long reverse(ALU& alu, long long input) 
        {
            return input * getter2->getValue(alu) + 1;
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

        void process(ALU& alu, long long value, int& index)
        {
            long long& param1 = getter1->getValue(alu);
            long long& param2 = getter2->getValue(alu);

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

    std::vector<InstructionGroup> parseInput(const std::vector<std::string>& input)
    {
        std::vector<InstructionGroup> data;

        std::vector<IInstruction*> instructions;

        for (int i=0;i<input.size();i++)
        {
            std::vector<std::string> parts = Utilities::splitString(input[i], " ");

            //process an input instruction
            if (parts.size() == 2) 
            {
                if (instructions.size() > 0) 
                {
                    InstructionGroup group;
                    group.instructions = instructions;
                    data.push_back(group);

                    instructions = {};
                }

                int param = typeToParam(parts[1]);
                instructions.push_back(new InputInstruction(new ParamValueGetter(param)));
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
                    valueGetter2 = new DefaultValueGetter(std::stoll(parts[2]));
                }
                else 
                {
                    valueGetter2 = new ParamValueGetter(param2);
                }

                if      (type == "add") instructions.push_back(new AddInstruction(valueGetter1, valueGetter2));
                else if (type == "mul") instructions.push_back(new MultiplyInstruction(valueGetter1, valueGetter2));
                else if (type == "div") instructions.push_back(new DivInstruction(valueGetter1, valueGetter2));
                else if (type == "mod") instructions.push_back(new ModInstruction(valueGetter1, valueGetter2));
                else if (type == "eql") instructions.push_back(new EqualsInstruction(valueGetter1, valueGetter2));
            }
        }

        if (instructions.size() > 0)
        {
            InstructionGroup group;
            group.instructions = instructions;
            data.push_back(group);
        }

        return data;
    }

    void printAlu(const ALU& alu) 
    {
        std::cout <<"\nw: " << alu.w << "\nx: " << alu.x <<"\ny: " << alu.y << "\nz: " << alu.z << std::endl;
    }

    void processModelNumber(const std::string& modelNumber, const std::vector<IInstruction*>& instructions, ALU& alu, int& index)
    {
        for (auto& instruction : instructions) 
        {
            int modelInput = Utilities::charToInt(modelNumber[index]);

            instruction->process(alu, modelInput, index);
        }
    }

    ALU processGroups(const std::string& modelNumber, const std::vector<InstructionGroup>& groups) 
    {
        ALU alu;

        int index = 0;

        for (const auto& group : groups)
        {
            processModelNumber(modelNumber, group.instructions, alu, index);
        }

        //printAlu(alu);

        return alu;
    }

    /*char findBestNumberAtIndex(int index, std::string input) 
    {
        long long score = LLONG_MAX;
        char bestChar = input[index];

        for (int j = 1; j <= 9; j++)
        {
            char tempChar = Utilities::intToChar(j);
            input[index] = tempChar;

            long long newScore = processModelNumber(input, data).z;
            if (newScore < score)
            {
                score = newScore;
                bestChar = tempChar;
            }
        }
        return bestChar;
    }*/

    //std::string getModelNumber(std::string input) 
    //{
    //    //for each of the indices, iterate the number and check the z-score
    //    for (int i = 0; i < input.size(); i++)
    //    {
    //        char bestNumber = findBestNumberAtIndex(i, input);

    //        input[i] = bestNumber;
    //    }

    //    return input;
    //}

    struct IRequirement 
    {
        virtual bool meetsRequirement(const ALU& alu) = 0;
    };
    struct ZRequirement : IRequirement 
    {
        bool meetsRequirement(const ALU& alu) 
        {
            return alu.z == 0;
        }
    };
    struct DefaultRequirement : IRequirement
    {
        std::vector<Vector2D> configs;
        DefaultRequirement(const std::vector<Vector2D>& configs)
            : configs(configs)
        {
        
        }

        bool meetsRequirement(const ALU& alu)
        {
            //should meet one of the requirements
            for (int i = 0; i < configs.size(); i++) 
            {
                if (alu.z == configs[i].x && alu.w == configs[i].y) return true;
            }

            return false;
        }
    };

    long long getZForW(int w, std::vector<IInstruction*> instructions, IRequirement* requirement)
    {
        long long z = 0;

        //keep incrementing for z until a key is found
        while (true)
        {
            ALU alu;
            //x and y will be initialized automatically

            alu.z = z;

            for (const auto& instruction : instructions)
            {
                int test;
                instruction->process(alu, w, test);

                printAlu(alu);
            }

            if (requirement->meetsRequirement(alu))
            {
                return z;
            }

            z++;
        }
    }

    std::vector<Vector2D> getInputForRequirement(const InstructionGroup& group, IRequirement* requirement, long long& previousZDiff)
    {
        std::vector<Vector2D> alus;

        //BRUTE FORCE
        long long z1 = getZForW(1, group.instructions, requirement);
        long long z2 = getZForW(2, group.instructions, requirement);

        //calculate the diff that will be applied to all remaining w values
        long long zDiff = z2 - z1;

        previousZDiff = zDiff;

        for (int w = 1; w <= 9; w++) 
        {
            long long z = z1 + (w - 1) * zDiff;

            alus.push_back(Vector2D(z, w));
        }
        
        return alus;
    }

    //Findings:         13          12
    //1.  inp w                          
    //2.  mul x 0                        
    //3.  add x z                        
    //4.  mod x 26                       
    //5.  ----          div z 26    div z 26
    //6.  ----          add x -2    add x -4
    //7.  eql x w                        
    //8.  eql x 0                        
    //9.  mul y 0                        
    //10. add y 25                       
    //11. mul y x                        
    //12. add y 1                        
    //13. mul z y                        
    //14. mul y 0                        
    //15. add y w                        
    //16. ----          add y 9     add y 4
    //17. mul y x                        
    //18. add z y     

    //you need to find out what these additional steps mean for the START pos of 
    //the next iteration

    //multiply by 26 (div)

    //x - (first special) should be 1 (for w == 1)

    std::string getSolutionPart1()
    {
        int result = 0;

        //std::vector<std::vector<Vector2D>> answersCollection(data.size());

        //std::vector<Vector2D> answers;
        //for (int i = 1; i <= 9; i++) 
        //{
        //    //creating all possible combinations for z and w
        //    answers.push_back(Vector2D(0, i));
        //}

        //long long prevZ = 0;

        //for (int i = data.size() - 1; i >= 0; i--) 
        //{
        //    answersCollection[i] = answers;

        //    answers = getInputForRequirement(data[i], new DefaultRequirement(answers), prevZ);

        //    std::cout << i << std::endl;
        //}

        const long long MIN = 11111111111111;
        const long long MAX = 99999999999999;

        long long iterations = MAX - MIN;

        int p = 0;

        for (long long i = 0; i < iterations; i++)
        {
            int resu = 0;

            int percentage = (int)(((double)i / (double)iterations) * 100);
            if (percentage != p) 
            {
                p = percentage;
                std::cout << p << std::endl;
            }

            /*std::string input = std::to_string(i + MIN);
            ALU alu = processGroups(input, data);

            std::cout << input<<" -> " <<alu.z << std::endl;*/
        }

       /* ALU alu = processModelNumber(digit, data);
        printAlu(alu);*/


        //TODO:
        //- find out for which Z each input-block in combination with a given input results in Z = 0

        //Findings:
        //- every block always starts with;
        //1.  inp w
        //2.  mul x 0
        //3.  add x z
        //4.  mod x 26
        //5.  ----
        //6.  ----
        //7.  eql x w
        //8.  eql x 0
        //9.  mul y 0
        //10. add y 25
        //11. mul y x
        //12. add y 1
        //13. mul z y
        //14. mul y 0
        //15. add y w
        //16. ----
        //17. mul y x
        //18. add z y

        return std::to_string(result);
    }

    std::string getSolutionPart2()
    {
        int result = 0;

        return std::to_string(result);
    }

    std::vector<InstructionGroup> data;

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