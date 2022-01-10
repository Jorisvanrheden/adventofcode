#pragma once

#include "Assignment.h"
#include <algorithm>
#include <map>

class Assignment_22 : public Assignment
{
private:

    struct Range 
    {
        int min;
        int max;
        int range;

        Range() {}
        Range(int min, int max) 
            : min(min), max(max)
        {
            range = max - min;
        }
    };
    struct Cuboid
    {
        bool enabled;

        Range xRange;
        Range yRange;
        Range zRange;

        std::vector<std::vector<std::vector<int>>> matrix;
        bool isValid = false;

        Cuboid() {}
        Cuboid(bool enabled, const Range& xRange, const Range& yRange, const Range& zRange)
            : enabled(enabled), xRange(xRange), yRange(yRange), zRange(zRange)
        {
            isValid = validate();

            if (isValid) 
            {
                int deltaX = xRange.max - xRange.min;
                int deltaY = yRange.max - yRange.min;
                int deltaZ = zRange.max - zRange.min;

                matrix = std::vector<std::vector<std::vector<int>>>(deltaX);

                for (int i = 0; i < deltaX; i++) 
                {
                    std::vector<std::vector<int>> row(deltaY);

                    for (int j = 0; j < deltaY; j++) 
                    {
                        std::vector<int> column(deltaZ);
                        for (int k = 0; k < deltaZ; k++) 
                        {
                            column[k] = enabled;
                        }
                        row[j] = column;
                    }
                    matrix[i] = row;
                }
            }
        }

        bool validate() 
        {
            if (xRange.min < -50 && xRange.max < -50) return false;
            if (xRange.min > 50  && xRange.max > 50) return false;

            if (yRange.min < -50 && yRange.max < -50) return false;
            if (yRange.min > 50 &&  yRange.max > 50)  return false;

            if (zRange.min < -50 && zRange.max < -50) return false;
            if (zRange.min > 50 &&  zRange.max > 50)  return false;

            return true;
        }

        void setValue(int value, const Vector3D& coordinate) 
        {
            int localX = coordinate.x - xRange.min;
            int localY = coordinate.y - yRange.min;
            int localZ = coordinate.z - zRange.min;

            if (localX < 0 || localX >= xRange.range) return;
            if (localY < 0 || localY >= yRange.range) return;
            if (localZ < 0 || localZ >= zRange.range) return;

            matrix[localX][localY][localZ] = value;
        }

        int addCountToMap(std::map<std::string, int>& map)
        {
            if (!isValid) return 0;

            int deltaX = xRange.max - xRange.min;
            int deltaY = yRange.max - yRange.min;
            int deltaZ = zRange.max - zRange.min;
            
            int count = 0;

            for (int i = 0; i < deltaX; i++)
            {
                for (int j = 0; j < deltaY; j++)
                {
                    for (int k = 0; k < deltaZ; k++)
                    {
                        int value = matrix[i][j][k];

                        if (value) 
                        {
                            int x = xRange.min + i;
                            int y = yRange.min + j;
                            int z = zRange.min + k;

                            std::string key = std::to_string(x) + "," + std::to_string(y) + "," + std::to_string(z);
                            if (map.find(key) == map.end()) 
                            {
                                map[key] = 0;
                            }
                            map[key]++;
                        }
                    }
                }
            }

            return count;
        }
    };

    std::vector<Cuboid> parseInput(const std::vector<std::string>& input)
    {
        std::vector<Cuboid> data;

        for (const auto& entry : input)
        {
            std::vector<std::string> parts = Utilities::splitString(entry, " ");

            bool enabled = (parts[0] == "on") ? true : false;
            Range rangeX, rangeY, rangeZ;

            std::vector<std::string> dimensions = Utilities::splitString(parts[1], ",");
            for (int i = 0; i < dimensions.size(); i++)
            {
                std::vector<std::string> ranges = Utilities::splitString(dimensions[i], "=");

                std::vector<std::string> minMax = Utilities::splitString(ranges[1], "..");

                int min = std::stoi(minMax[0]);
                int max = std::stoi(minMax[1]);

                if      (ranges[0] == "x") rangeX = Range(min, max + 1);
                else if (ranges[0] == "y") rangeY = Range(min, max + 1);
                else if (ranges[0] == "z") rangeZ = Range(min, max + 1);
            }

            data.push_back(Cuboid(enabled, rangeX, rangeY, rangeZ));
        }

        return data;
    }

    bool hasOverlap(const Cuboid& a, const Cuboid& b) 
    {
        if (!a.isValid || !b.isValid) return false;

        if (a.xRange.max < b.xRange.min || a.xRange.min > b.xRange.max) return false;
        if (a.yRange.max < b.yRange.min || a.yRange.min > b.yRange.max) return false;
        if (a.zRange.max < b.zRange.min || a.zRange.min > b.zRange.max) return false;

        return true;
    }

    void processOverlap(Cuboid& a, const Cuboid& b) 
    {
        if (!hasOverlap(a, b)) return;

        for (int i = 0; i < b.xRange.range; i++)
        {
            for (int j = 0; j < b.yRange.range; j++)
            {
                for (int k = 0; k < b.zRange.range; k++)
                {
                    //check if the coordinate is within bounds
                    int x = b.xRange.min + i;
                    int y = b.yRange.min + j;
                    int z = b.zRange.min + k;

                    Vector3D coordinate(x, y, z);

                    a.setValue(b.enabled, coordinate);
                }
            }
        }
    }

    int getEnabledCount(std::vector<Cuboid>& cuboids) 
    {
        std::map<std::string, int> map;

        for (auto& cuboid : cuboids) 
        {
            cuboid.addCountToMap(map);
        }

        return map.size();
    }

    std::vector<Cuboid> getProcessedCuboids(std::vector<Cuboid>& cuboids)
    {
        //apply all cuboids from top to bottom
        std::vector<Cuboid> processedCuboids;
        for (int i = 0; i < data.size(); i++)
        {
            //apply the current cuboid to all processed cuboids
            for (auto& cuboid : processedCuboids)
            {
                processOverlap(cuboid, data[i]);
            }

            //add the current cuboid to the processed cuboids list
            processedCuboids.push_back(data[i]);
        }

        return processedCuboids;
    }

    std::string getSolutionPart1()
    {
        int result = 0;

        auto processedCuboids = getProcessedCuboids(data);

        result = getEnabledCount(processedCuboids);

        return std::to_string(result);
    }

    std::string getSolutionPart2()
    {
        int result = 0;

        return std::to_string(result);
    }

    std::vector<Cuboid> data;

public:
    void initialize()
    {
        std::vector<std::string> input = Utilities::readFile("../22/input");
        data = parseInput(input);
    }
};