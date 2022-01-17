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

        Player(){}
        Player(int position, int score)
            :position(position), score(score)
        {

        }

        void updateScore() 
        {
            if (position > 10)
            {
                while (position > 10) position -= 10;
            }

            score += position;
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

    struct Configuration
    {
        bool hasBeenPlayed = false;
        std::string key;
        std::vector<unsigned long long> results;

        Configuration(const std::string& key, const std::vector<unsigned long long>& results)
            : key(key), results(results), hasBeenPlayed(true)
        {

        }
        Configuration(bool hasBeenPlayed)
            : hasBeenPlayed(hasBeenPlayed)
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
            
        }
        player.updateScore();
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
    
    std::vector<Player> retrieveNextRoundPlayers(const Player& activePlayer) 
    {
        std::vector<Player> players(27);

        int count = 0;
        for (int i = 1; i <= 3; i++)
        {
            for (int j = 1; j <= 3; j++)
            {
                for (int k = 1; k <= 3; k++)
                {
                    //create a copy of the active player
                    Player player = activePlayer;

                    //override the position
                    player.position += i + j + k;

                    //process the score given the position of the player
                    player.updateScore();

                    //add the player to the list
                    players[count] = player;

                    count++;
                }
            }
        }
        return players;
    }

    int getLosingScore(const std::vector<Player>& players, const Die& die) 
    {
        for (const auto& player : players) 
        {
            if (player.score < 1000) return player.score * die.rollCount;
        }

        return 0;
    }

    unsigned long long getHighestWinningCount(const std::vector<unsigned long long>& results) 
    {
        unsigned long long highestResult = 0;
        for (int i = 0; i < results.size(); i++) 
        {
            if (results[i] > highestResult) highestResult = results[i];
        }
        return highestResult;
    }

    std::string createKeyFromPlayer(const Player& player)
    {
        std::string key;

        key += "[" + std::to_string(player.position) + "," + std::to_string(player.score) + "]";

        return key;
    }

    std::string createKey(const std::vector<Player>& players, int playerIndex) 
    {
        std::string key;

        for (const auto& player : players) 
        {
            key += createKeyFromPlayer(player);
        }

        key += std::to_string(playerIndex);

        return key;
    }

    std::string createValue(std::vector<unsigned long long>& winnerCounts)
    {
        std::string value;

        for (int i = 0; i < winnerCounts.size(); i++)
        {
            value += std::to_string(winnerCounts[i]);
            if (i < winnerCounts.size() - 1) 
            {
                value += ",";
            }
        }

        return value;
    }

    std::vector<unsigned long long> parseValue(const std::string& value) 
    {
        std::vector<unsigned long long> values;

        std::vector<std::string> parts = Utilities::splitString(value, ",");
        for (int i = 0; i < parts.size(); i++) 
        {
            values.push_back(std::stoll(parts[i]));
        }

        return values;
    }    

    //we have to find out how many games are played and the result given a certain start configuration
    //with start configuration, we mean:
    //- player 1 position
    //- player 1 score
    //- player 2 position
    //- player 2 score

    //then the result should be in the shape of:
    //- amount games won by player 1
    //- amount games won by player 2
    Configuration getConfiguration(std::vector<Player>& players, int playerIndex, std::map<std::string, std::string>& map)
    {
        /* check if this 'game' configuration has been player before
        if it has, simply add the results to the winnerCounts instead of playing it out again*/
        std::string key = createKey(players, playerIndex);
        if (map.find(key) != map.end())
        {
            std::string value = map.at(key);
            std::vector<unsigned long long> values = parseValue(value);

            return Configuration(value, values);
        }
        
        return Configuration(false);
    }

    //make a copy to have initialized values, but that they don't overwrite other 'universes'
    std::vector<unsigned long long> playQuantumGame(std::vector<Player>& players, int playerIndex, std::map<std::string, std::string>& map, int maxScore)
    {
        //first check if this specific configuration has already been executed once before
        //if it has, use the results from that stored configuration
        Configuration config = getConfiguration(players, playerIndex, map);
        if (config.hasBeenPlayed)
        {
            return config.results;
        }

        //otherwise, continue executing the current configuration
        std::vector<unsigned long long> results(players.size(), 0);

        std::vector<Player> quantumPlayers = retrieveNextRoundPlayers(players[playerIndex]);
        for (const auto& quantumPlayer : quantumPlayers)
        {
            if (quantumPlayer.score >= maxScore)
            {
                results[playerIndex]++;

                continue;
            }

            //create a copy of players, and modify the active player
            std::vector<Player> playersCopy = players;
            playersCopy[playerIndex] = quantumPlayer;  

            //mod the playerIndex:
            // - the index needs to be in bounds for the 'players' list
            // - active players need to be rotated, since we're not looping through the players list
            int nextPlayerIndex = (playerIndex + 1) % players.size();
            
            //recurse into the next layer of games that should be played
            auto updatedCounts = playQuantumGame(playersCopy, nextPlayerIndex, map, maxScore);

            //add the updated counts to the local results variable
            //using this method, all combined results from the 'children' are stored in the parent variable
            for (int i = 0; i < updatedCounts.size(); i++)
            {
                results[i] += updatedCounts[i];
            }
        }      

        //results are in, can be stored in the map now:
        //- create a key/value pair for the current configuration
        std::string key = createKey(players, playerIndex);
        std::string value = createValue(results);

        //map the results 
        map[key] = value;

        return results;
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
        unsigned long long result = 0;

        std::map<std::string, std::string> map;
        auto results = playQuantumGame(data, 0, map, 21);

        result = getHighestWinningCount(results);

        return std::to_string(result);
    }

    std::vector<Player> data;

public:
    void initialize(const std::vector<std::string>& input)
    {
        data = parseInput(input);
    }

    std::string getInput()
    {
        return "21/input";
    }
};