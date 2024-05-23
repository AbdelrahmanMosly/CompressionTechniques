#ifndef DECOMPRESSOR_H
#define DECOMPRESSOR_H

#include <vector>
#include <cstdint> // for int32_t
#include <iostream>
#include <fstream>

using namespace std;


class DeCompressor{
private:
    vector<uint32_t> cummulativeFreqs;
    uint32_t low;
    uint32_t encoded;
    uint32_t high;
    uint32_t totalFreqs;

    uint32_t virtualBitStream; // Note that the #bytes written by compressor is a multiple of 32 bits
    uint8_t streamRemainingBits;
    
    ifstream inputFile;
    ofstream outputFile;

    int recover();
    void calculateNewLowAndHigh(int symbolIdx, uint64_t low64, uint64_t width, uint64_t high64);
    uint8_t read1Bit();

    bool handleLowHighMatch();
    bool handleLowHighUnderflow();
    void readCumulativeFreqsFromFile(ifstream &inputFile);

public:
    DeCompressor(string filePath);
    DeCompressor(vector<uint32_t> cummFreqs, string filePath);

    void deCompress();
};

#endif // DECOMPRESSOR_H