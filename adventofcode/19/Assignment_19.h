#pragma once

#include "Assignment.h"
#include <math.h>
#include "Vector3D.h"

class Assignment_19 : public Assignment
{
private:

    //Have a data structure that stores the distances to all other beacons
    //These distances can be used to identify coupled beacons from other transformations
    struct Connection
    {
        Vector3D center;
        std::vector<Vector3D> distances;

        Connection(const Vector3D& center, const std::vector<Vector3D>& distances)
            :center(center), distances(distances)
        {

        }
    };

    struct OverlayData
    {
        bool hasData;

        Vector3D relativePosition = Vector3D(0, 0, 0);
        std::vector<Vector3D> transformedCoordinates;

        OverlayData(bool hasData)
            : hasData(hasData)
        {

        }

        OverlayData(bool hasData, const Vector3D& relativePosition, const std::vector<Vector3D>& transformedCoordinates)
            : hasData(hasData), relativePosition(relativePosition), transformedCoordinates(transformedCoordinates)
        {

        }
    };

    struct Scanner 
    {
        std::vector<Vector3D> coordinates;
        int id;
        bool hasCorrectOrientation = false;

        Vector3D position = Vector3D(0, 0, 0);

        Scanner(const std::vector<Vector3D>& coordinates, int id)
            :coordinates(coordinates), id(id)
        {
        }

        void overwriteOrientation(const std::vector<Vector3D>& coordinates) 
        {
            this->coordinates = coordinates;

            hasCorrectOrientation = true;
        }
    };

    int parsePart(const std::vector<std::string>& parts, int index) 
    {
        if (index >= parts.size()) return 0;

        return std::stoi(parts[index]);
    }

    std::vector<Vector3D> parseCoordinates(const std::vector<std::string>& input, int& index)
    {
        std::vector<Vector3D> coordinates;

        while (index < input.size()) 
        {
            if(input[index].empty()) break;
       
            std::vector<std::string> parts = Utilities::splitString(input[index], ",");

            int x = parsePart(parts, 0);
            int y = parsePart(parts, 1);
            int z = parsePart(parts, 2);
            coordinates.push_back(Vector3D(x, y, z));

            index++;
        }

        return coordinates;
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

                std::vector<Vector3D> coordinates = parseCoordinates(input, index);
                data.push_back(Scanner(coordinates, data.size()));
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

    std::string vector3DToString(const Vector3D& vector)
    {
        std::string key;
        key += std::to_string(vector.x);
        key += ",";
        key += std::to_string(vector.y);
        key += ",";
        key += std::to_string(vector.z);
        return key;
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
            Vector3D right = rotateZ(coordinate, -90);

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

        return totalTransformations;
    }    

    std::vector<Connection> createConnection(const std::vector<Vector3D>& coordinates)
    {
        std::vector<Connection> connections;

        for (int i = 0; i < coordinates.size(); i++) 
        {
            std::vector<Vector3D> distances;

            for (int j = 0; j < coordinates.size(); j++) 
            {
                if (i == j) continue;

                int distanceX = abs(coordinates[j].x - coordinates[i].x);
                int distanceY = abs(coordinates[j].y - coordinates[i].y);
                int distanceZ = abs(coordinates[j].z - coordinates[i].z);

                distances.push_back(Vector3D(distanceX, distanceY, distanceZ));
            }

            connections.push_back(Connection(coordinates[i], distances));
        }

        return connections;
    }

    std::string getMostOccurringKey(const std::map<std::string, int>& map) 
    {
        std::string mostOccurringKey;
        int count = 0;

        for (const auto& pair : map)
        {
            if (pair.second > count)
            {
                mostOccurringKey = pair.first;
                count = pair.second;
            }
        }

        return mostOccurringKey;
    }

    OverlayData getOverlayData(const Scanner& activeScanner, const std::vector<Vector3D> transformedCoordinates, int minimumRequiredOverlapCount)
    {
        std::vector<Connection> connectionsA = createConnection(activeScanner.coordinates);
        std::vector<Connection> connectionsB = createConnection(transformedCoordinates);

        std::vector<Vector3D> beacons;

        std::map<std::string, int> map;

        for (int i = 0; i < connectionsA.size(); i++) 
        {
            int total = 0;

            //for each distance, check if a distance like that also exists in the other collection
            for (const auto& distanceA : connectionsA[i].distances)
            {
                //find all distances in collection B
                for (int j = 0; j < connectionsB.size(); j++) 
                {
                    for (const auto& distanceB : connectionsB[j].distances)
                    {
                        if (distanceA.x == distanceB.x &&
                            distanceA.y == distanceB.y &&
                            distanceA.z == distanceB.z)
                        {
                            int diffX = connectionsA[i].center.x - connectionsB[j].center.x;
                            int diffY = connectionsA[i].center.y - connectionsB[j].center.y;
                            int diffZ = connectionsA[i].center.z - connectionsB[j].center.z;

                            std::string key = vector3DToString(Vector3D(diffX, diffY, diffZ));

                            if (map.find(key) == map.end()) 
                            {
                                map[key] = 0;
                            }

                            map[key]++;

                            total++;
                        }
                    }
                }
            }

            if (total >= minimumRequiredOverlapCount)
            {
                beacons.push_back(connectionsA[i].center);
            }
        }

        if (beacons.size() >= minimumRequiredOverlapCount)
        {
            std::string mostOccurringKey = getMostOccurringKey(map);
            int bestValue = map.at(mostOccurringKey);

            //store the value of the best key
            //then remove the best key entry from the map, just to make sure that the second-best is not equally good
            map.erase(mostOccurringKey);

            std::string secondbestKey = getMostOccurringKey(map);
            int secondBestValue = map.at(secondbestKey);

            if (bestValue == secondBestValue) return false;

            std::vector<std::string> parts = Utilities::splitString(mostOccurringKey, ",");

            int posX = std::stoi(parts[0]) + activeScanner.position.x;
            int posY = std::stoi(parts[1]) + activeScanner.position.y;
            int posZ = std::stoi(parts[2]) + activeScanner.position.z;

            return OverlayData(true, Vector3D(posX, posY, posZ), transformedCoordinates);
        }

        return OverlayData(false);
    }

    OverlayData findCorrectOrientation(const Scanner& activeScanner, const Scanner& scannerToCompare, int minimumRequiredOverlapCount)
    {
        //for the active scanner, find all overlapping coordinates another scanner

        //to do so:
        //- for the scanner to compare with, get a list of possible transformations
        //- compare the active scanner coordinates with the transformation
        //- if there is an overlap, the orientation needs to be stored as the 'correct' orientation

        auto transformations = getTransformations(scannerToCompare.coordinates);

        for (const auto& transformation : transformations)
        {
            OverlayData data = getOverlayData(activeScanner, transformation, minimumRequiredOverlapCount);
            if (data.hasData)
            {
                return data;
            }
        }

        //if no overlapping set has been found, then the two scanners don't match
        return OverlayData(false);
    }

    std::vector<Scanner> generateProcessedScanners(std::vector<Scanner> scanners, int minimumRequiredOverlapCount)
    {
        if (scanners.size() == 0)return {};

        //use the orientation of the first scanner as the 'correct' orientation
        //any index can be used, and the result will be the same
        scanners[0].overwriteOrientation(scanners[0].coordinates);

        std::vector<Scanner> queue;
        queue.push_back(scanners[0]);

        while (queue.size() > 0)
        {
            Scanner activeScanner = queue[0];
            queue.erase(queue.begin());

            for (auto& scanner : scanners)
            {
                if (scanner.hasCorrectOrientation) continue;

                OverlayData overlayData = findCorrectOrientation(activeScanner, scanner, minimumRequiredOverlapCount);
                if (overlayData.hasData)
                {
                    std::cout << "Scanner " << scanner.id << " used scanner " << activeScanner.id << " to calibrate." << std::endl;

                    scanner.overwriteOrientation(overlayData.transformedCoordinates);
                    scanner.position = overlayData.relativePosition;

                    queue.push_back(scanner);
                }
            }
        }

        return scanners;
    }

    std::vector<Vector3D> getUniqueBeacons(const std::vector<Scanner>& scanners) 
    {
        std::map<std::string, int> map;

        std::vector<Vector3D> beacons;

        for (const auto& scanner : scanners) 
        {
            for (const auto& coordinate : scanner.coordinates) 
            {
                int x = coordinate.x + scanner.position.x;
                int y = coordinate.y + scanner.position.y;
                int z = coordinate.z + scanner.position.z;

                Vector3D beacon = Vector3D(x, y, z);

                std::string key = vector3DToString(beacon);
                if (map.find(key) != map.end()) continue;

                map[key] = 0;

                beacons.push_back(beacon);
            }
        }

        return beacons;
    }

    int findLargestDistance(const std::vector<Scanner>& scanners) 
    {
        int largestDistance = INT_MIN;

        for (int i = 0; i < scanners.size(); i++) 
        {
            for (int j = 0; j < scanners.size(); j++) 
            {
                int distanceX = abs(scanners[i].position.x - scanners[j].position.x);
                int distanceY = abs(scanners[i].position.y - scanners[j].position.y);
                int distanceZ = abs(scanners[i].position.z - scanners[j].position.z);

                int totalDistance = distanceX + distanceY + distanceZ;
                if (totalDistance > largestDistance)largestDistance = totalDistance;
            }
        }

        return largestDistance;
    }

    std::string getSolutionPart1()
    {
        int result = 0;

        std::vector<Scanner> scanners = generateProcessedScanners(data, 12);

        std::vector<Vector3D> beacons = getUniqueBeacons(scanners);

        return std::to_string(beacons.size());
    }

    std::string getSolutionPart2()
    {
        int result = 0;

        std::vector<Scanner> scanners = generateProcessedScanners(data, 12);

        result = findLargestDistance(scanners);

        return std::to_string(result);
    }

    std::vector<Scanner> data;

public:
    void initialize(const std::vector<std::string>& input)
    {
        data = parseInput(input);
    }

    std::string getInput()
    {
        return "19/input";
    }
};