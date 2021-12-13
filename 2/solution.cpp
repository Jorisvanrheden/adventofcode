#include <iostream>
#include <fstream>

#include <vector>

//Options are:
//- forward (horizontal position)
//- up (vertical position)
//- down (vertical position)
struct Direction
{
    int x;
    int y;
    int magnitude;

    Direction()
    {
        x = 0;
        y = 0;
        magnitude = 0;   
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
        substrings.push_back(token);

        mutableLine.erase(0, position + delimiter.length());
    }

    substrings.push_back(mutableLine);

    return substrings;
}

bool parseDimension(Direction* direction, const std::string& dimension)
{
    if(dimension == "forward")
    {
        direction->x = 1;
    }
    else if(dimension == "up")
    {
        direction->y = -1;
    }
    else if(dimension == "down")
    {
        direction->y = 1;
    }
    else
    {
        return false;
    }
    
    return true;
}

bool parseMagnitude(Direction* direction, const std::string& magnitude)
{
    try
    {
        int value = std::stoi(magnitude);
        direction->magnitude = value;

        return true;
    }
    catch(std::exception& e)
    {
        return false;
    }
}

Direction* createDirection(const std::vector<std::string>& substrings)
{
    //validate that the input string has 2 space-delimited substrings
    if(substrings.size() != 2) return NULL;

    Direction* direction = new Direction();

    if(!parseDimension(direction, substrings[0])) return NULL;
    if(!parseMagnitude(direction, substrings[1])) return NULL;
    
    return direction;
}

std::vector<Direction*> parseInput(const std::string& filepath)
{
    std::vector<Direction*> input;

    std::ifstream file(filepath);

    std::string line;
    while(std::getline(file, line))
    {
        std::vector<std::string> substrings = substring(line, " ");

        Direction* direction = createDirection(substrings);

        if(direction)
        {
            input.push_back(direction);       
        }
    }

    return input;
}

int computeTotalDistance(const std::vector<Direction*>& directions)
{
    int distanceHorizontal = 0;
    int distanceVertical = 0;

    for(const auto& direction : directions)
    {
        distanceHorizontal += direction->x * direction->magnitude;
        distanceVertical   += direction->y * direction->magnitude;
    }

    return distanceHorizontal * distanceVertical;
}

int computeTotalDistanceWithAim(const std::vector<Direction*>& directions)
{
    int distance = 0;
    int depth = 0;
    int aim = 0;

    for(const auto& direction : directions)
    {
        aim      += direction->y * direction->magnitude;
        distance += direction->x * direction->magnitude;

        //process depth only if the forward vector is valid
        if(direction->x != 0)
        {
            depth += aim * direction->magnitude;
        }
    }

    return distance * depth;
}

int main()
{
    std::vector<Direction*> input = parseInput("./input");

    int result = computeTotalDistanceWithAim(input);

    std::cout<<"--- ANSWER IS ---"<<std::endl;
    std::cout<<result<<std::endl;

    return 0;
}