#include <iostream>
#include <fstream>
#include <vector>
#include <algorithm>
#include <string.h>

struct Entry
{
    int value;
    bool checked;

    Entry(){}
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
        for(int i=0;i<rows;i++)
        {
            for(int j=0;j<columns;j++)
            {
                output += std::to_string(matrix[i][j].value);
                output += "\t";
            }
            
            output += "\n";
        }
        std::cout<<output<<std::endl;
    }

    void printStatus()
    {
        std::string output;
        for(int i=0;i<rows;i++)
        {
            for(int j=0;j<columns;j++)
            {
                output += std::to_string(matrix[i][j].checked);
                output += "\t";
            }
            
            output += "\n";
        }
        std::cout<<output<<std::endl;
    }
};

struct Output
{
    int index;
    int number;

    Output(int index, int number)
        : index(index), number(number)
    {

    }
};

struct Input
{
    std::vector<int> numbers;
    std::vector<Board> boards;

    Input(const std::vector<int>& numbers, const std::vector<Board>& boards)
        : numbers(numbers), boards(boards)
    {

    }
};

std::vector<std::string> substring(const std::string& line, const std::string& delimiter)
{
    std::string mutableLine = std::string(line);

    std::vector<std::string> substrings;

    size_t position = 0;

    //while another instance of the delimiter has been found
    while(mutableLine.find(delimiter) != std::string::npos)
    {
        position = mutableLine.find(delimiter);

        std::string token = mutableLine.substr(0, position);

        if(!token.empty())
        {
            substrings.push_back(token);
        }

        mutableLine.erase(0, position + delimiter.length());
    }

    substrings.push_back(mutableLine);

    return substrings;
}

std::vector<std::string> getBlock(const std::vector<std::string>& lines, int& index)
{
    std::vector<std::string> block;

    for(int i=index;i<lines.size();i++)
    {
        if(lines[i].empty()) break;

        block.push_back(lines[i]);

        index++;
    }

    return block;
}

std::vector<std::string> getLines(const std::string& filepath)
{
    std::vector<std::string> lines;

    std::ifstream file(filepath);

    std::string line;
    while(std::getline(file, line))
    {
        lines.push_back(line);
    }

    return lines;
}

std::vector<int> parseNumbers(std::string line)
{
    std::vector<int> numbers;

    std::vector<std::string> parts = substring(line, ",");
    for(int i=0;i<parts.size();i++)
    {
        try
        {
            int number = std::stoi(parts[i]);
            numbers.push_back(number);
        }
        catch(std::exception& e)
        {
            continue;
        }
    }

    return numbers;
}

Board parseBoard(std::vector<std::string> block)
{
    std::vector<std::vector<Entry>> values(block.size());

    for(int i=0;i<block.size();i++)
    {
        std::vector<std::string> columns = substring(block[i], " ");
        std::vector<Entry> row(columns.size());
        for(int j=0;j<columns.size();j++)
        {
            int value = 0;

            try
            {
                value = std::stoi(columns[j]);
            }
            catch(std::exception& e)
            {

            }
            
            row[j] = Entry(value, false);
        }

        values[i] = row;
    }

    return Board(values.size(), values[0].size(), values);
}

Input parseInput(const std::string& filepath)
{
    std::vector<int> numbers;
    std::vector<Board> boards;

    //Read all input into a vector (with known length)
    std::vector<std::string> lines = getLines(filepath);

    std::vector<std::vector<std::string>> blocks;

    //Retrieve blocks (index can easily be mutated)
    for(int i=0;i<lines.size();i++)
    {
        blocks.push_back(getBlock(lines, i));
    }
    
    for(int i=0;i<blocks.size();i++)
    {
        if(blocks[i].size() == 1)
        {
            //process input
            numbers = parseNumbers(blocks[i][0]);
        }
        else if(blocks[i].size() == 5)
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
    for(int i=0;i<board.rows;i++)
    {
        for(int j=0;j<board.columns;j++)
        {
            //update matrix checked value if necessary
            if(board.getValueAt(i,j) == number)
            {
                board.setStatusAt(i,j, true);
            }
        } 
    }
}

bool isConsistentCollection(const std::vector<int>& input, int value)
{
    for(int i=0;i<input.size();i++)
    {
        if(input[i] != value) return false;
    }

    return true;
}

bool hasBingo(const Board& board)
{
    //check rows
    for(int i=0;i<board.rows;i++)
    {     
        std::string pr;

        std::vector<int> row(board.columns);
        for(int j=0;j<board.columns;j++)
        {
            row[j] = board.getStatusAt(i,j);

            pr += std::to_string(row[j]);
            pr += "\t";
        }   

        std::cout<<pr<<std::endl;

        if(isConsistentCollection(row, 1)) return true;
    }

    //check columns
    for(int i=0;i<board.columns;i++)
    {        
        //create collection of the column values
        std::vector<int> column(board.rows);
        for(int j=0;j<board.rows;j++)
        {
            column[j] = board.getStatusAt(j, i);
        }

        if(isConsistentCollection(column, 1)) return true;
    }

    return false;
}

Output getBingoBoard(Input& input)
{
    //loop through numbers
    for(const auto& number : input.numbers)
    {
        std::cout<<"Processing number: "<<number<<std::endl;

        //for each number, apply it to the board
        for(int i=0;i<input.boards.size();i++)
        {
            std::cout<<" --- BOARD NUMBER "<<i<<std::endl;
            
            applyNumberToBoard(input.boards[i], number);  
            input.boards[i].printValues();   
            input.boards[i].printStatus();

            if(hasBingo(input.boards[i]))
            {
                return Output(i, number);
            }  
        }
    }

    return Output(-1, -1);
}

int getUnmarkedBoardScore(const Input& input, int boardIndex)
{
    Board board = input.boards[boardIndex];
    std::vector<int> sequence = input.numbers;

    int score = 0;
    
    //loop through all inputs of the selected board
    //check if they're included in the input sequence
    //if not, add score to total
    for(int i=0;i<board.rows;i++)
    {
       for(int j=0;j<board.columns;j++)
       {
            //if the value is not part of the sequence
            if(!board.getStatusAt(i,j))
            {
                score += board.getValueAt(i,j);
            }
       } 
    }

    return score;
}

int main()
{
    Input input = parseInput("./input");

    Output output = getBingoBoard(input);
    int result = getUnmarkedBoardScore(input, output.index) * output.number;

    std::cout<<"--- ANSWER IS ---"<<std::endl;
    std::cout<<result<<std::endl;

    return 0;
}