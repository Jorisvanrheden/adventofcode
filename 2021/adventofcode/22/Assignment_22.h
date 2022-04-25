#pragma once

#include "Assignment.h"

class Assignment_22 : public Assignment
{
private:

    struct Range 
    {
        long long min;
        long long max;
        unsigned long long range;

        Range() {}
        Range(long long min, long long max)
            : min(min), max(max)
        {
            range = max - min + 1;
        }
    };

    struct Cuboid
    {
        int value;

        Range xRange;
        Range yRange;
        Range zRange;

        Cuboid() {}
        Cuboid(const Range& xRange, const Range& yRange, const Range& zRange, int value)
            : xRange(xRange), yRange(yRange), zRange(zRange), value(value)
        {

        }

        unsigned long long getVolume()
        {
            return xRange.range * yRange.range * zRange.range;
        }
    };

    std::vector<Cuboid> parseInput(const std::vector<std::string>& input)
    {
        std::vector<Cuboid> data;

        for (const auto& entry : input)
        {
            std::vector<std::string> parts = Utilities::splitString(entry, " ");

            int value = (parts[0] == "on") ? 1 : 0;
            Range rangeX, rangeY, rangeZ;

            std::vector<std::string> dimensions = Utilities::splitString(parts[1], ",");
            for (int i = 0; i < dimensions.size(); i++)
            {
                std::vector<std::string> ranges = Utilities::splitString(dimensions[i], "=");

                std::vector<std::string> minMax = Utilities::splitString(ranges[1], "..");

                int min = std::stoi(minMax[0]);
                int max = std::stoi(minMax[1]);

                if      (ranges[0] == "x") rangeX = Range(min, max);
                else if (ranges[0] == "y") rangeY = Range(min, max);
                else if (ranges[0] == "z") rangeZ = Range(min, max);
            }

            data.push_back(Cuboid(rangeX, rangeY, rangeZ, value));
        }

        return data;
    }

    bool hasOverlap(const Cuboid& a, const Cuboid& b) 
    {
        if (a.xRange.max < b.xRange.min || a.xRange.min > b.xRange.max) return false;
        if (a.yRange.max < b.yRange.min || a.yRange.min > b.yRange.max) return false;
        if (a.zRange.max < b.zRange.min || a.zRange.min > b.zRange.max) return false;

        return true;
    }

    Range getOverlappingRange(const Range& a, const Range& b) 
    {
        //create the overlapping area
        int min, max;

        if (a.min >= b.min && a.min <= b.max)
        {
            if (a.min == b.max) 
            {
                min = b.min;
                max = b.min;
            }
            else 
            {
                min = a.min;
                max = std::min(a.max, b.max);
            }
        }
        else if (a.max <= b.max && a.max >= b.min)
        {
            if (a.max == b.min) 
            {
                min = a.max;
                max = a.max;
            }
            else 
            {
                min = std::max(a.min, b.min);
                max = a.max;
            }
        }
        else 
        {
            //take the smallest
            if (a.range < b.range) return a;
            else return b;
        }

        return Range(min, max);
    }

    bool areEqualCuboids(const Cuboid& a, const Cuboid& b) 
    {
        if (a.xRange.min != b.xRange.min || a.xRange.max != b.xRange.max) return false;
        if (a.yRange.min != b.yRange.min || a.yRange.max != b.yRange.max) return false;
        if (a.zRange.min != b.zRange.min || a.zRange.max != b.zRange.max) return false;

        return true;
    }

    bool containsCuboid(const std::vector<Cuboid>& areas, const Cuboid& cuboid) 
    {
        for (const auto& area : areas) 
        {
            if (areEqualCuboids(area, cuboid)) return true;
        }
        return false;
    }

    void getBounds(const std::vector<long long>& coordinates, int index, long long& min, long long& max)
    { 
        //initialize at default values;
        min = coordinates[index];
        max = coordinates[index + 1];
       
        if (index == 0)
        {
            max -= 1;
        }
        
        if (index == coordinates.size() - 2)
        {
            min += 1;
        }
    }

    std::vector<Cuboid> splitAreaFromOverlap(const Cuboid& originalCuboid, const Cuboid& overlapCuboid) 
    {
        //for a 2D square, if it's split up, you get 3x3 = 9 new areas
        //for a 3D square, this is 3x3x3 = 27 new areas
        std::vector<Cuboid> areas;

        //store all unique x,y and z coordinates, so we can loop through them and easily compute the new areas
        std::vector<long long> xCoordinates = { originalCuboid.xRange.min, overlapCuboid.xRange.min, overlapCuboid.xRange.max, originalCuboid.xRange.max };
        std::vector<long long> yCoordinates = { originalCuboid.yRange.min, overlapCuboid.yRange.min, overlapCuboid.yRange.max, originalCuboid.yRange.max };
        std::vector<long long> zCoordinates = { originalCuboid.zRange.min, overlapCuboid.zRange.min, overlapCuboid.zRange.max, originalCuboid.zRange.max };

        for (int i = 0; i < xCoordinates.size() - 1; i++) 
        {
            long long xMin, xMax;
            getBounds(xCoordinates, i, xMin, xMax);

            if (xMin > xMax) continue;

            for (int j = 0; j < yCoordinates.size() - 1; j++) 
            {
                long long yMin, yMax;
                getBounds(yCoordinates, j, yMin, yMax);

                if (yMin > yMax) continue;

                for (int k = 0; k < zCoordinates.size() - 1; k++) 
                {
                    long long zMin, zMax;
                    getBounds(zCoordinates, k, zMin, zMax);

                    if (zMin > zMax) continue;

                    //validation to make sure the given coordinates are in range of the active area
                    if (xMin < originalCuboid.xRange.min || xMax > originalCuboid.xRange.max) continue;
                    if (yMin < originalCuboid.yRange.min || yMax > originalCuboid.yRange.max) continue;
                    if (zMin < originalCuboid.zRange.min || zMax > originalCuboid.zRange.max) continue;

                    Range xRange(xMin, xMax);
                    Range yRange(yMin, yMax);
                    Range zRange(zMin, zMax);

                    //this method only splits the existing area in smaller areas with the same value, so use create a new cuboid with the original value
                    Cuboid area(xRange, yRange, zRange, originalCuboid.value);

                    //if the dimensions are the same as the overlapping cuboid, skip it
                    if (areEqualCuboids(area, overlapCuboid)) 
                    {
                        continue;
                    }
                    else 
                    {
                        areas.push_back(area);
                    }
                }
            }
        }

        return areas;
    }

    std::vector<Cuboid> processOverlap(const Cuboid& overlap, Cuboid& original)
    {
        //create the overlapping area
        Range xRange = getOverlappingRange(overlap.xRange, original.xRange);
        Range yRange = getOverlappingRange(overlap.yRange, original.yRange);
        Range zRange = getOverlappingRange(overlap.zRange, original.zRange);

        //calculate the overlapping area
        Cuboid overlappingArea(xRange, yRange, zRange, overlap.value);

        //Only split up the original cube
        //The overlapping one keeps the original value
        return splitAreaFromOverlap(original, overlappingArea);
    }

    unsigned long long getEnabledCount(std::vector<Cuboid> cuboids)
    {
        unsigned long long total = 0;
        for (auto& cuboid : cuboids) 
        {
            total += cuboid.getVolume() * cuboid.value;
        }
        return total;
    }

    std::vector<Cuboid> getProcessedCuboids(std::vector<Cuboid> input)
    {
        //logic:
        //for each iteration, we have an area A that overlaps with area B
        //when the overlap occurs, we split area B into multiple parts, and delete the overlapping part
        //so that the addition of area A + area B results in non-repeating points

        //at the end of each iteration (going through all areas in the stack), the current overlapping area (input[i])
        //gets added to the processed list.
        //by adding the area to the list at the end, all processed cubes up until that points get impacted by the 
        //original size of the active overlapping cube

        //also, the overlapping does not need to be split up. If it gets added to the queue either way, any overlapping
        //areas that follow and overlap with this area would cause the area to split up anyway.
        //if it's the last area in the total list, no splitting up is required

        std::vector<Cuboid> processedCuboids;

        for (int i = 0; i < input.size(); i++)
        {
            //apply the current cuboid to all processed cuboids
            for (int j=0;j<processedCuboids.size();j++)
            {
                if (!hasOverlap(input[i], processedCuboids[j])) continue;     

                std::vector<Cuboid> originalSplitInAreas = processOverlap(input[i], processedCuboids[j]);

                //remove the cuboids[i], and add the split parts
                processedCuboids.erase(processedCuboids.begin() + j);
                processedCuboids.insert(processedCuboids.begin() + j, originalSplitInAreas.begin(), originalSplitInAreas.end());

                //increment j to point behind the latest addition
                j += originalSplitInAreas.size() - 1;
            }

            //add the current cuboid to the processed cuboids list
            processedCuboids.push_back(input[i]);
        }

        return processedCuboids;
    }

    bool hasSmallRange(const Cuboid& cuboid) 
    {
        if (cuboid.xRange.min < -50 && cuboid.xRange.max < -50) return false;
        if (cuboid.xRange.min > 50 && cuboid.xRange.max > 50) return false;

        if (cuboid.yRange.min < -50 && cuboid.yRange.max < -50) return false;
        if (cuboid.yRange.min > 50 && cuboid.yRange.max > 50)  return false;

        if (cuboid.zRange.min < -50 && cuboid.zRange.max < -50) return false;
        if (cuboid.zRange.min > 50 && cuboid.zRange.max > 50)  return false;

        return true;
    }

    std::vector<Cuboid> getSmallCuboids(const std::vector<Cuboid>& cuboids) 
    {
        std::vector<Cuboid> smallCuboids;

        for (int i = 0; i < cuboids.size(); i++) 
        {
            if (!hasSmallRange(cuboids[i])) continue;

            smallCuboids.push_back(cuboids[i]);
        }

        return smallCuboids;
    }

    std::string getSolutionPart1()
    {
        int result = 0;

        auto smallCubes = getSmallCuboids(data);

        std::vector<Cuboid> processedCuboids = getProcessedCuboids(smallCubes);
        
        result = getEnabledCount(processedCuboids);

        return std::to_string(result);
    }

    std::string getSolutionPart2()
    {
        unsigned long long result = 0;

        std::vector<Cuboid> processedCuboids = getProcessedCuboids(data);

        result = getEnabledCount(processedCuboids);

        return std::to_string(result);
    }

    std::vector<Cuboid> data;

public:
    void initialize(const std::vector<std::string>& input)
    {
        data = parseInput(input);
    }

    std::string getInput()
    {
        return "22/input";
    }
};