#pragma once

#include "Assignment.h"
#include <algorithm>
#include <map>

class Assignment_20 : public Assignment
{
private:

    struct Input 
    {
        std::vector<int> algorithm;
        std::vector<std::vector<int>> pixels;

        Input() {}
        Input(const std::vector<int>& algorithm, const std::vector<std::vector<int>>& pixels)
            : algorithm(algorithm), pixels(pixels)
        {

        }
    };

    int symbolToDecimal(char symbol) 
    {
        if (symbol == '#') return 1;
        else return 0;
    }

    Input parseInput(const std::vector<std::string>& input)
    {
        std::vector<int> algorithm;
        std::vector<std::vector<int>> pixels;

        if (input.size() > 0) 
        {
            algorithm = std::vector<int>(input[0].size());
            for (int i = 0; i < input[0].size(); i++)
            {
                algorithm[i] = symbolToDecimal(input[0][i]);
            }
        }
        
        for (int i = 1; i < input.size(); i++)
        {
            if (input[i].empty()) continue;
           
            std::vector<int> row(input[i].size());
            for (int j = 0; j < input[i].size(); j++) 
            {
                row[j] = symbolToDecimal(input[i][j]);
            }

            pixels.push_back(row);
        }

        return Input(algorithm, pixels);
    }

    bool isValidCoordinate(const std::vector<std::vector<int>>& pixels, const Vector2D& coordinate)
    {
        if (coordinate.x < 0) return false;
        if (coordinate.y < 0) return false;

        if (coordinate.x >= pixels.size()) return false;
        if (coordinate.y >= pixels[coordinate.x].size()) return false;

        return true;
    }

    std::string pixelsToBinary(const std::vector<int> pixels)
    {
        std::string binary;

        for (const auto& pixel : pixels)
        {
            binary += std::to_string(pixel);
        }

        return binary;
    }

    std::vector<int> getSurroundingPixels(const std::vector<std::vector<int>>& pixels, const Vector2D& center, int range)
    {
        std::vector<int> values;

        int startX = center.x - floor((float)range / 2);
        int startY = center.y - floor((float)range / 2);

        //double nested loop to get the surrounding 'block' of pixels from the top left to the bottom right
        for (int i = 0; i < range; i++)
        {
            for (int j = 0; j < range; j++)
            {
                int x = startX + i;
                int y = startY + j;

                if (isValidCoordinate(pixels, Vector2D(x, y)))
                {
                    values.push_back(pixels[x][y]);
                }
                else 
                {
                    //to support the 'infinite' nature of the situation, always extend with 0 if the pixel is out of bounds
                    values.push_back(0);
                }
            }
        }

        return values;
    }

    int getEnhancedPixel(const std::vector<int>& algorithm, const std::vector<std::vector<int>>& pixels, const Vector2D& coordinate)
    {
        //get the surrounding pixels from the active pixel (3x3 grid)
        std::vector<int> surroundingPixels = getSurroundingPixels(pixels, coordinate, 3);
        
        //transform the surrounding pixel list to a binary value
        std::string binary = pixelsToBinary(surroundingPixels);

        //transform the binary to a decimal value
        int algorithmIndex = Utilities::binaryToDecimal(binary);

        //retrieve the active symbol from the 'algorithm' using the decimal value as index
        if (algorithmIndex >= algorithm.size()) 
        {
            std::cout << "The algorithm index is out of bounds" << std::endl;
        }

        return algorithm[algorithmIndex];
    }

    std::vector<std::vector<int>> getEnhancedImage(const std::vector<int>& algorithm, const std::vector<std::vector<int>>& pixels, int infinityValue)
    {
        //copy the original matrix
        std::vector<std::vector<int>> copy = pixels;

        const int ADDITION = 2;

        //we can't just add zeroes, but we need to calculate what the 'infinite' value was after x amount of turns, given that the start value is 0
        //expanding the matrix to support 'infinite growth'
        //for each row, add one 'infinityValue' before and after the existing row
        for (int i = 0; i < copy.size(); i++) 
        {
            for (int j = 0; j < ADDITION; j++)
            {
                copy[i].insert(copy[i].begin(), infinityValue);
                copy[i].insert(copy[i].end(), infinityValue);
            }
        }

        //insert rows top and bottom with rowSize, and fill them with all zeroes
        for (int j = 0; j < ADDITION; j++) 
        {
            copy.insert(copy.begin(), std::vector<int>(copy[0].size(), infinityValue));
            copy.insert(copy.end(), std::vector<int>(copy[0].size(), infinityValue));
        }

        std::vector<std::vector<int>> copyOriginal = copy;

        for (int i = 0; i < copyOriginal.size(); i++)
        {
            for (int j = 0; j < copyOriginal[i].size(); j++)
            {
                //get the new 'enhanced' pixel
                int pixel = getEnhancedPixel(algorithm, copyOriginal, Vector2D(i, j));
                
                //overwrite the pixel in the matrix, but only in the copy
                //the enhanced pixels are based on the original matrix
                copy[i][j] = pixel;
            }
        }

        //cut off the first and last row
        copy.erase(copy.begin());
        copy.erase(copy.begin() + copy.size() - 1);

        //cut off the first and last column
        for (int i = 0; i < copy.size(); i++)
        {
            copy[i].erase(copy[i].begin());
            copy[i].erase(copy[i].begin() + copy[i].size() - 1);
        }


        return copy;
    }

    int getInfiniteValue(const std::vector<int>& algorithm, int turn) 
    {
        std::string binary = "000000000";

        //uneven turns
        if (turn % 2 == 0) 
        {
            //find what the binary is for even values
            int evenValue = algorithm[Utilities::binaryToDecimal(binary)];
            std::vector<int> values(9, evenValue);
            
            std::string evenBinary = pixelsToBinary(values);
            int evenAlgorithmIndex = Utilities::binaryToDecimal(evenBinary);

            return algorithm[evenAlgorithmIndex];
        }
        else 
        {
            //transform the binary to a decimal value
            int algorithmIndex = Utilities::binaryToDecimal(binary);

            return algorithm[algorithmIndex];
        }
    }

    void printImage(const std::vector<std::vector<int>>& pixels) 
    {
        std::string output;

        for (int i = 0; i < pixels.size(); i++) 
        {
            for (int j = 0; j < pixels[i].size(); j++) 
            {
                if (pixels[i][j]) output += "#";
                else output += '.';
            }
            output += "\n";
        }

        std::cout << output << std::endl;
    }

    int getImageLightCount(const std::vector<std::vector<int>>& pixels) 
    {
        int count = 0;

        for (int i = 0; i < pixels.size(); i++)
        {
            for (int j = 0; j < pixels[i].size(); j++)
            {
                if (pixels[i][j] == 1) count++;
            }
        }

        return count;
    }

    std::string getSolutionPart1()
    {
        int result = 0;

        std::vector<std::vector<int>> enhancedImage = data.pixels;

        for (int i = 0; i < 2; i++) 
        {
            enhancedImage = getEnhancedImage(data.algorithm, enhancedImage, getInfiniteValue(data.algorithm, i));
        }

        result = getImageLightCount(enhancedImage);

        return std::to_string(result);
    }

    std::string getSolutionPart2()
    {
        int result = 0;

        std::vector<std::vector<int>> enhancedImage = data.pixels;

        for (int i = 0; i < 50; i++)
        {
            enhancedImage = getEnhancedImage(data.algorithm, enhancedImage, getInfiniteValue(data.algorithm, i));
        }

        result = getImageLightCount(enhancedImage);

        return std::to_string(result);
    }

    Input data;

public:
    void initialize(const std::vector<std::string>& input)
    {
        data = parseInput(input);
    }

    std::string getInput()
    {
        return "20/input";
    }
};