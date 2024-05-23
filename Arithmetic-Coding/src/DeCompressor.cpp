#include "DeCompressor.h"


const int alphabetNum = 257; // the 256 possible byte values + EOF Character

DeCompressor :: DeCompressor(string filePath){
    string extension = filePath.substr(filePath.size() - 6);
    if(extension != ".myZip"){
        cerr << "This file cannot be compressed with this tool" << endl;
        exit(0);
    }
    inputFile.open(filePath, ios::binary);
    outputFile.open(filePath.substr(0, filePath.size() - 6) + ".decompressed", ios::binary);
    if (!inputFile.is_open()) {
        cerr << "Error While Openning Input File!!\n";
        exit(1);
    }
    if (!outputFile.is_open()) {
        cerr << "Error while Opening Output File!!\n";
        exit(1);
    }

    cummulativeFreqs = vector<uint32_t>(alphabetNum + 1);
    readCumulativeFreqsFromFile(inputFile);
    // to minimize #times we access the vector 
    totalFreqs = cummulativeFreqs[alphabetNum];

    low = 0;
    encoded = 0;
    high = ~0;


    streamRemainingBits = 0;
    virtualBitStream = 0;
}

DeCompressor::DeCompressor(vector<uint32_t> cummFreqs, string filePath)
{
    cummulativeFreqs = cummFreqs;
    low = 0;
    high = ~0;
    totalFreqs = cummFreqs[alphabetNum]; // to minimize #times we access the vector

    virtualBitStream = 0;
    streamRemainingBits = 32;
    inputFile.open(filePath, ios::binary);
    outputFile.open(filePath + ".myZip", ios::binary);
    if (!inputFile.is_open())
    {
        cerr << "Error While Openning Input File!!\n";
        exit(1);
    }
    if (!outputFile.is_open())
    {
        cerr << "Error while Opening Output File!!\n";
        exit(1);
    }
}

void DeCompressor :: readCumulativeFreqsFromFile(ifstream &inputFile){
    for(int i = 0; i <= alphabetNum; i++){
        uint32_t freq = 0;
        char buffer[4];
        inputFile.read(buffer, sizeof(buffer));
        freq = (buffer[0] & 0xFF) << 24;
        freq |= (buffer[1] & 0xFF) << 16;
        freq |= (buffer[2] & 0xFF) << 8;
        freq |= buffer[3] & 0xFF;
        cummulativeFreqs[i] = freq;
    }
}

void DeCompressor :: deCompress(){
    for(int i = 0; i < 32; i++) encoded = (encoded << 1) | read1Bit();
    while(true){
        int symbol = recover();
        if(symbol == alphabetNum - 1) break;
        bool handled = true;
        while(handled) handled = handleLowHighMatch() || handleLowHighUnderflow(); // Short Circuit if matching occurs
    }
    inputFile.close(); outputFile.close();
} 



int DeCompressor :: recover(){
    // we will use uint64_t just to have no overflow in intermediate results,
    // however final results are guaranteed to be in uint32_t.
    uint64_t low64 = low & 0xFFFFFFFF, high64 = high & 0xFFFFFFFF, encoded64 = encoded & 0xFFFFFFFF;
    uint64_t width = (high64 + 1) - low64;
    // scale the encoded and recover symbol
    encoded64 = ((encoded64 - low64 + 1) * totalFreqs - 1) / width;
    for(int i = 0; i < alphabetNum; i++){
        if(cummulativeFreqs[i] <= encoded64 && encoded64 < cummulativeFreqs[i + 1]){
            if(i < alphabetNum - 1) {
                char symbol = (char) i;
                outputFile.write(reinterpret_cast<const char*>(&symbol), sizeof(symbol));
                calculateNewLowAndHigh(i, low64, width, high64);
            }
            return i;
        }
    }
    return -1;
}

void DeCompressor :: calculateNewLowAndHigh(int symbolIdx, uint64_t low64, uint64_t width, uint64_t high64){
    high64 = low64 + (width * cummulativeFreqs[symbolIdx + 1]) / totalFreqs - 1;
    low64 = low64 + (width * cummulativeFreqs[symbolIdx]) / totalFreqs;
    low = static_cast<uint32_t>(low64 & 0xFFFFFFFF);
    high = static_cast<uint32_t>(high64 & 0xFFFFFFFF);
}

uint8_t DeCompressor :: read1Bit(){
    if(streamRemainingBits == 0){ // first, read 32 bits into the stream if there is no.
        char byte;
        for(int i = 0; i < 4; i++){
           if (inputFile.get(byte)) virtualBitStream = (virtualBitStream << 8) | (int(byte) & 0xFF);
           else return 1; // we have infinite 1s behind the last set bit.
        }
        streamRemainingBits = 32;
    }
    uint8_t bit = virtualBitStream >> 31;
    virtualBitStream <<= 1;
    streamRemainingBits--;
    return bit;
}

bool DeCompressor :: handleLowHighMatch(){
    uint8_t lowMSBit = static_cast<uint8_t>(low >> 31);
    uint8_t highMSBit = static_cast<uint8_t>(high >> 31);
    if(lowMSBit == highMSBit){
        low = low << 1;
        encoded = (encoded << 1) | read1Bit();
        high = (high << 1) | 1;
        return true;
    }
    return false;
}

bool DeCompressor :: handleLowHighUnderflow(){
    uint8_t lowSecMSBit = static_cast<uint8_t>(low >> 30) & 1;
    uint8_t highSecMSBit = static_cast<uint8_t>(high >> 30) & 1;
    if(lowSecMSBit == 1 && highSecMSBit == 0){
        low = (low << 2) >> 1;
        high = (1 << 31) | ((high << 2) >> 1) | 1;

        uint32_t encodedMSB = encoded & (1 << 31);
        encoded = encodedMSB | ((encoded << 2) >> 1) | read1Bit();
        return true;
    }
    return false;
}