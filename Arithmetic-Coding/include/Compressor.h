#ifndef COMPRESSOR_H
#define COMPRESSOR_H

#include <vector>
#include <cstdint> // for int32_t
#include <iostream>
#include <fstream>

using namespace std;


class Compressor {
private:
    vector<uint32_t> cummulativeFreqs;
    uint32_t low;
    uint32_t high;
    uint32_t totalFreqs;
    uint32_t underflowCount;
    
    uint32_t virtualBitStream; 
    int streamRemainingPlaces;

    ifstream inputFile;
    ofstream outputFile;

    void calculateNewLowHigh(int symbol);
    bool canReadNextByte();
    bool handleLowHighMatch();
    bool handleLowHighUnderflow();

    void writeUnderFlow(uint8_t lowMSBit);
    void writeRepresentative();

    void writeToBitStream(uint32_t toAdd, int toAddLength);
    void writeToFile(bool force);

public:    
    Compressor(vector<uint32_t> cummFreqs, string filePath);
    void compress();
};

#endif // COMPRESSOR_H