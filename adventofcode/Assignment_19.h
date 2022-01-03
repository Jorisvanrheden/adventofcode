#pragma once

#include "Assignment.h"
#include <math.h>
#include "Vector3D.h"

class Assignment_19 : public Assignment
{
private:
    struct Scanner 
    {
        std::vector<Vector3D> coordinates;

        Scanner(const std::vector<Vector3D>& coordinates)
            :coordinates(coordinates)
        {
        }
    };

    Scanner parseScanner(const std::vector<std::string>& input, int& index)
    {
        std::vector<Vector3D> coordinates;

        while (index < input.size()) 
        {
            if(input[index].empty()) break;
       
            std::vector<std::string> parts = Utilities::splitString(input[index], ",");
            int x = std::stoi(parts[0]);
            int y = std::stoi(parts[1]);
            int z = std::stoi(parts[2]);
            coordinates.push_back(Vector3D(x, y, z));

            index++;
        }

        return Scanner(coordinates);
    }

    std::vector<Scanner> parseInput(const std::vector<std::string>& input)
    {
        std::vector<Scanner> data;

        int index = 0;
        while (index < input.size()) 
        {
            if (input[index].find("scanner") != std::string::npos)
            {
                //increment the index so the same line isn't processed twice
                index++;

                Scanner scanner = parseScanner(input, index);
                data.push_back(scanner);
            }

            index++;
        }      

        return data;
    }

    void printSet(const std::vector<Vector3D>& coordinates)
    {
        std::cout << "Begin printing set..." << std::endl;
        for (const auto& coordinate : coordinates) 
        {
            std::cout << std::to_string(coordinate.x) << "," << std::to_string(coordinate.y) << "," << std::to_string(coordinate.z) << std::endl;
        }
        std::cout << "\n" << std::endl;
    }

    int getCos(int angle) 
    {
        if (angle < 0) angle += 360;

        if (angle == 0) return 1;
        else if (angle == 90) return 0;
        else if (angle == 180) return -1;
        else if (angle == 270) return 0;

        return INT_MIN;
    }
    int getSin(int angle) 
    {
        if (angle < 0) angle += 360;

        if (angle == 0) return 0;
        else if (angle == 90) return 1;
        else if (angle == 180) return 0;
        else if (angle == 270) return -1;

        return INT_MIN;
    }

    Vector3D rotateX(const Vector3D& coordinate, int angle)
    {
        int cos = getCos(angle);
        int sin = getSin(angle);

        int x = coordinate.x;
        int y = coordinate.y * cos - coordinate.z * sin;
        int z = coordinate.z * cos + coordinate.y * sin;

        return Vector3D(x, y, z);
    }

    Vector3D rotateY(const Vector3D& coordinate, int angle)
    {
        int cos = getCos(angle);
        int sin = getSin(angle);

        int x = coordinate.x * cos - coordinate.z * sin;
        int y = coordinate.y;
        int z = coordinate.z * cos + coordinate.x * sin;

        return Vector3D(x, y, z);
    }

    Vector3D rotateZ(const Vector3D& coordinate, int angle)
    {
        int cos = getCos(angle);
        int sin = getSin(angle);

        int x = coordinate.x * cos - coordinate.y * sin;
        int y = coordinate.y * cos + coordinate.x * sin;
        int z = coordinate.z;

        return Vector3D(x, y, z);
    }

    void appendYRotations(std::vector<Vector3D>& coordinates, Vector3D original) 
    {
        //For all of these rotations, the forward-, or up-vector has four options
        //In each new position, rotation over the y-axis four times
        for (int i = 0; i < 4; i++)
        {
            original = rotateY(original, 90);

            coordinates.push_back(original);
        }
    }

    std::vector<std::vector<Vector3D>> getTransformations(const std::vector<Vector3D>& coordinates) 
    {
        std::vector<std::vector<Vector3D>> totalTransformations(24);

        for (int i = 0; i < coordinates.size(); i++)
        {
            Vector3D coordinate = coordinates[i];

            std::vector<Vector3D> transformations;

            //forward
            //x-axis (90)
            Vector3D forward = rotateX(coordinate, 90);

            //backward
            //x-axis (-90)
            Vector3D backward = rotateX(coordinate, -90);

            //left
            //z-axis (90)
            Vector3D left = rotateZ(coordinate, 90);

            //right
            //z-axis (-90)
            Vector3D right = rotateZ(coordinate, 90);

            //straight up
            //no modifications
            Vector3D up = rotateX(coordinate, 0);

            //straight down (twice forward)
            //x-axis (180)
            Vector3D down = rotateX(coordinate, 180);

            appendYRotations(transformations, forward);
            appendYRotations(transformations, backward);
            appendYRotations(transformations, left);
            appendYRotations(transformations, right);
            appendYRotations(transformations, up);
            appendYRotations(transformations, down);

            for (int j = 0; j < totalTransformations.size(); j++) 
            {
                totalTransformations[j].push_back(transformations[j]);
            }
        }

        for (const auto& c : totalTransformations) 
        {
            printSet(c);
        }

        return totalTransformations;
    }

    std::string getSolutionPart1()
    {
        int result = 0;

        getTransformations(data[0].coordinates);

        return std::to_string(result);
    }

    std::string getSolutionPart2()
    {
        int result = 0;

        return std::to_string(result);
    }

    std::vector<Scanner> data;

public:
    void initialize()
    {
        std::vector<std::string> input = Utilities::readFile("../19/input_example");
        data = parseInput(input);
    }
};