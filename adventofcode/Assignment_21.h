#pragma once

#include "Assignment.h"
#include <algorithm>
#include <map>

class Assignment_21 : public Assignment
{
private:

    struct Player
    {
        int position;
        int score;

        Player(int position, int score)
            :position(position), score(score)
        {

        }
    };

    struct Die 
    {
        int rollCount = 0;
        int value;

        Die(int value)
            : value(value)
        {
        }
    };

    std::vector<Player> parseInput(const std::vector<std::string>& input)
    {
        std::vector<Player> data;

        for (const auto& entry : input)
        {
            std::vector<std::string> parts = Utilities::splitString(entry, ": ");

            int value = std::stoi(parts[1]);
            data.push_back(Player(value, 0));
        }

        return data;
    }

    int rollDie(Die& die) 
    {
        die.value++;

        if (die.value > 100)
        {
            die.value = 1;
        }

        

        die.rollCount++;

        if (die.rollCount == 993)
        {
            std::cout << "here" << std::endl;
        }


        return die.value;
    }

    void processRound(Player& player, Die& die)
    {
        for (int i = 0; i < 3; i++)
        {
            player.position += rollDie(die);

            if (player.position > 10) 
            {
                while (player.position > 10) player.position -= 10;
            }
        }

        player.score += player.position;
    }

    void playGame(std::vector<Player>& players, Die& die)
    {
        while (true)
        {
            for (auto& player : data)
            {
                processRound(player, die);

                if (player.score >= 1000)
                {
                    return;
                }
            }
        }
    }

    int getLosingScore(const std::vector<Player>& players, const Die& die) 
    {
        for (const auto& player : players) 
        {
            if (player.score < 1000) return player.score * die.rollCount;
        }

        return 0;
    }

    std::string getSolutionPart1()
    {
        int result = 0;

        Die die(0);
        playGame(data, die);

        result = getLosingScore(data, die);

        return std::to_string(result);
    }

    std::string getSolutionPart2()
    {
        int result = 0;

        return std::to_string(result);
    }

    std::vector<Player> data;

public:
    void initialize()
    {
        std::vector<std::string> input = Utilities::readFile("../21/input");
        data = parseInput(input);
    }
};