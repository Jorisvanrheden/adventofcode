#pragma once

#include "Assignment.h"
#include <algorithm>

struct Entry
{
    int value;
    bool checked;

    Entry() {}
    Entry(int value, bool checked)
        : value(value), checked(checked)
    {

    }
};

struct Board
{
    std::vector<std::vector<Entry>> matrix;
    std::vector<std::vector<bool>> used;

    int rows;
    int columns;

    Board() {}
    Board(int rows, int columns, std::vector<std::vector<Entry>> matrix)
        : rows(rows), columns(columns), matrix(matrix)
    {

    }

    int getValueAt(int x, int y) const
    {
        return matrix[x][y].value;
    }

    int getStatusAt(int x, int y) const
    {
        return matrix[x][y].checked;
    }

    void setStatusAt(int x, int y, bool value)
    {
        matrix[x][y].checked = value;
    }

    void printValues()
    {
        std::string output;
        for (int i = 0; i < rows; i++)
        {
            for (int j = 0; j < columns; j++)
            {
                output += std::to_string(matrix[i][j].value);
                output += "\t";
            }

            output += "\n";
        }
        std::cout << output << std::endl;
    }

    void printStatus()
    {
        std::string output;
        for (int i = 0; i < rows; i++)
        {
            for (int j = 0; j < columns; j++)
            {
                output += std::to_string(matrix[i][j].checked);
                output += "\t";
            }

            output += "\n";
        }
        std::cout << output << std::endl;
    }
};

struct Output
{
    int index;
    int number;

    Output() {}
    Output(int index, int number)
        : index(index), number(number)
    {

    }
};

struct Input
{
    std::vector<int> numbers;
    std::vector<Board> boards;

    Input() {}
    Input(const std::vector<int>& numbers, const std::vector<Board>& boards)
        : numbers(numbers), boards(boards)
    {

    }
};

class Assignment_4 : public Assignment
{
private:

    std::vector<int> parseNumbers(const std::string& line)
    {
        std::vector<int> numbers;

        std::vector<std::string> parts = Utilities::splitString(line, ",");
        for (int i = 0; i < parts.size(); i++)
        {
            try
            {
                int number = std::stoi(parts[i]);
                numbers.push_back(number);
            }
            catch (std::exception& e)
            {
                continue;
            }
        }

        return numbers;
    }

    Board parseBoard(const std::vector<std::string>& block)
    {
        std::vector<std::vector<Entry>> values(block.size());

        for (int i = 0; i < block.size(); i++)
        {
            std::vector<std::string> columns = Utilities::splitString(block[i], " ");
            std::vector<Entry> row(columns.size());
            for (int j = 0; j < columns.size(); j++)
            {
                int value = 0;

                try
                {
                    value = std::stoi(columns[j]);
                }
                catch (std::exception& e)
                {

                }

                row[j] = Entry(value, false);
            }

            values[i] = row;
        }

        return Board(values.size(), values[0].size(), values);
    }

    std::vector<std::string> getBlock(const std::vector<std::string>& lines, int& index)
    {
        std::vector<std::string> block;

        for (int i = index; i < lines.size(); i++)
        {
            if (lines[i].empty()) break;

            block.push_back(lines[i]);

            index++;
        }

        return block;
    }

    Input parseInput(const std::vector<std::string>& input)
    {
        std::vector<int> numbers;
        std::vector<Board> boards;

        std::vector<std::vector<std::string>> blocks;

        //Retrieve blocks (index can easily be mutated)
        for (int i = 0; i < input.size(); i++)
        {
            blocks.push_back(getBlock(input, i));
        }

        for (int i = 0; i < blocks.size(); i++)
        {
            if (blocks[i].size() == 1)
            {
                //process input
                numbers = parseNumbers(blocks[i][0]);
            }
            else if (blocks[i].size() == 5)
            {
                //process board
                boards.push_back(parseBoard(blocks[i]));
            }
        }

        return Input(numbers, boards);
    }

    void applyNumberToBoard(Board& board, int number)
    {
        //loop through the matrix, check if the number exists
        for (int i = 0; i < board.rows; i++)
        {
            for (int j = 0; j < board.columns; j++)
            {
                //update matrix checked value if necessary
                if (board.getValueAt(i, j) == number)
                {
                    board.setStatusAt(i, j, true);
                }
            }
        }
    }

    bool isConsistentCollection(const std::vector<int>& input, int value)
    {
        for (int i = 0; i < input.size(); i++)
        {
            if (input[i] != value) return false;
        }

        return true;
    }

    bool hasBingo(const Board& board)
    {
        //check rows
        for (int i = 0; i < board.rows; i++)
        {
            std::vector<int> row(board.columns);
            for (int j = 0; j < board.columns; j++)
            {
                row[j] = board.getStatusAt(i, j);
            }

            if (isConsistentCollection(row, 1)) return true;
        }

        //check columns
        for (int i = 0; i < board.columns; i++)
        {
            //create collection of the column values
            std::vector<int> column(board.rows);
            for (int j = 0; j < board.rows; j++)
            {
                column[j] = board.getStatusAt(j, i);
            }

            if (isConsistentCollection(column, 1)) return true;
        }

        return false;
    }

    std::vector<Output> processGames(Input& input)
    {
        std::vector<Output> output;

        //cache a data structure that keeps track of the finished games
        std::vector<bool> completed(input.boards.size(), false);

        //loop through numbers
        for (const auto& number : input.numbers)
        {
            //for each number, apply it to the board
            for (int i = 0; i < input.boards.size(); i++)
            {
                if (completed[i]) continue;

                applyNumberToBoard(input.boards[i], number);

                if (hasBingo(input.boards[i]))
                {
                    //store the active run information
                    output.push_back(Output(i, number));

                    //also label the current board as finished
                    completed[i] = true;
                }
            }
        }

        return output;
    }

    int getUnmarkedBoardScore(const Input& input, int boardIndex)
    {
        Board board = input.boards[boardIndex];
        std::vector<int> sequence = input.numbers;

        int score = 0;

        //loop through all inputs of the selected board
        //check if they're included in the input sequence
        //if not, add score to total
        for (int i = 0; i < board.rows; i++)
        {
            for (int j = 0; j < board.columns; j++)
            {
                //if the value is not part of the sequence
                if (!board.getStatusAt(i, j))
                {
                    score += board.getValueAt(i, j);
                }
            }
        }

        return score;
    }
    
    std::string getSolutionPart1()
    {
        int result = getUnmarkedBoardScore(data, output[0].index) * output[0].number;
        return std::to_string(result);
    }

    std::string getSolutionPart2()
    {
        int result = getUnmarkedBoardScore(data, output[output.size() - 1].index) * output[output.size() - 1].number;
        return std::to_string(result);
    }

    Input data;
    std::vector<Output> output;

public:
    void initialize()
    {
        std::vector<std::string> input = Utilities::readFile("../4/input");

        data = parseInput(input);
        output = processGames(data);
    }
};