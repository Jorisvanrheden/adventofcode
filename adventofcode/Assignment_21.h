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

        bool init = false;

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

    std::vector<Player> playGame(std::vector<Player> players, Die& die, int maxScore)
    {
        while (true)
        {
            for (auto& player : players)
            {
                processRound(player, die);

                if (player.score >= maxScore)
                {
                    return players;
                }
            }
        }

        return players;
    }

    void processQuantumRound(std::vector<Player>& players, int playerIndex, std::vector<unsigned long long>& winnerCounts, int maxScore)
    {
        //first process the score?
        if (players[playerIndex].position > 10)
        {
            while (players[playerIndex].position > 10) players[playerIndex].position -= 10;
        }

        if (players[playerIndex].init)
        {
            players[playerIndex].score += players[playerIndex].position;
        }
        else players[playerIndex].init = true;


        for (int i = 0; i < 3; i++)
        {
            //add 1, 2 or 3
            //but for each of the added options, create a new quantum game 
            //maybe initialize the player with the new value?
            for (int dimension = 1; dimension <= 3; dimension++) 
            {
                std::vector<Player> copy = players;
                copy[playerIndex].position += dimension;

                playQuantumGame(copy, winnerCounts, maxScore);
            }
        }
    }

    //make a copy to have initialized values, but that they don't overwrite other 'universes'
    void playQuantumGame(std::vector<Player> players, std::vector<unsigned long long>& winnerCounts, int maxScore)
    {
        //before playing, actually make sure there is no winner already
        for (auto& player : players) 
        {
            if (player.score >= maxScore) return;
        }

        while (true)
        {
            for (int i = 0; i < players.size(); i++) 
            {
                if (players[i].score >= maxScore)
                {
                    winnerCounts[i]++;
                    return;
                }

                processQuantumRound(players, i, winnerCounts, maxScore);
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
        std::vector<Player> players = playGame(data, die, 1000);

        result = getLosingScore(players, die);

        return std::to_string(result);
    }

    std::string getSolutionPart2()
    {
        int result = 0;

        std::vector<unsigned long long> winnerCounts(data.size(), 0);
        playQuantumGame(data, winnerCounts, 21);

        std::cout << std::to_string(winnerCounts[0]) << ", " << std::to_string(winnerCounts[1]) << std::endl;
        return std::to_string(result);
    }

    std::vector<Player> data;

public:
    void initialize()
    {
        std::vector<std::string> input = Utilities::readFile("../21/input_example");
        data = parseInput(input);
    }
};