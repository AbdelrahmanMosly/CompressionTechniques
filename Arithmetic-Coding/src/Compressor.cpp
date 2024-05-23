#include <cmath>

#include "Compressor.h"


const int alphabetNum = 257; // the 256 possible byte values + EOF Character

Compressor :: Compressor(vector<uint32_t> cummFreqs, string filePath){
    cummulativeFreqs = cummFreqs;
    low = 0;
    high = ~0;
    totalFreqs = cummFreqs[alphabetNum]; // to minimize #times we access the vector 
    underflowCount = 0;
    
    virtualBitStream = 0; 
    streamRemainingPlaces = 32;
    inputFile.open(filePath, ios::binary);
    outputFile.open(filePath + ".myZip", ios::binary);
    if (!inputFile.is_open()) {
        cerr << "Error While Openning Input File!!\n";
        exit(1);
    }
    if (!outputFile.is_open()) {
        cerr << "Error while Opening Output File!!\n";
        exit(1);
    }
}

void writeCummulativeFreqsToFile(vector<uint32_t> cummulativeFreqs, ofstream &outputFile){
    for(int i = 0; i <= alphabetNum; i++){
        char buffer[4]; 
        buffer[0] = (cummulativeFreqs[i] >> 24) & 0xFF; 
        buffer[1] = (cummulativeFreqs[i] >> 16) & 0xFF;
        buffer[2] = (cummulativeFreqs[i] >> 8) & 0xFF; 
        buffer[3] = cummulativeFreqs[i] & 0xFF;
        outputFile.write(buffer, sizeof(buffer));
    }
}

void Compressor :: compress(){
    writeCummulativeFreqsToFile(cummulativeFreqs, outputFile);
    char byte;
    while (inputFile.get(byte)) {
        calculateNewLowHigh(int(byte) & 0xFF);
        bool handled = true;
        while(handled)handled = handleLowHighMatch() || handleLowHighUnderflow(); // Short Circuit if matching occurs
    }
    // handle EOF Byte
    calculateNewLowHigh(alphabetNum - 1);
    bool handled = true;
    while(handled) handled = handleLowHighMatch() || handleLowHighUnderflow(); // Short Circuit if matching occurs
    writeRepresentative();
    inputFile.close(); outputFile.close();
}

void Compressor :: calculateNewLowHigh(int symbol){
    // we will use uint64_t just to have no overflow in intermediate results,
    // however final results are guaranteed to be in uint32_t.
    uint64_t low64 = low & 0xFFFFFFFF , high64 = high & 0xFFFFFFFF;
    uint64_t width = (high64 + 1) - low64;
    high64 = low64 + (width * cummulativeFreqs[symbol + 1]) / totalFreqs - 1;
    low64 = low64 + (width * cummulativeFreqs[symbol]) / totalFreqs;
    low = static_cast<uint32_t>(low64 & 0xFFFFFFFF);
    high = static_cast<uint32_t>(high64 & 0xFFFFFFFF);
}

bool Compressor :: handleLowHighMatch(){
    uint8_t lowMSBit = static_cast<uint8_t>(low >> 31);
    uint8_t highMSBit = static_cast<uint8_t>(high >> 31);
    if(lowMSBit == highMSBit){
        writeToBitStream(lowMSBit, 1);
        writeUnderFlow(lowMSBit);
        low = low << 1;
        high = (high << 1) | 1;
        return true;
    }
    return false;
}

bool Compressor :: handleLowHighUnderflow(){
    uint8_t lowSecMSBit = static_cast<uint8_t>(low >> 30) & 1;
    uint8_t highSecMSBit = static_cast<uint8_t>(high >> 30) & 1;
    if(lowSecMSBit == 1 && highSecMSBit == 0){
        low = (low << 2) >> 1;
        high = (1 << 31) | ((high << 2) >> 1) | 1;
        underflowCount++;
        return true;
    }
    return false;
}

void Compressor :: writeToBitStream(uint32_t toAdd, int toAddLength){
    while(toAddLength){
        int minLengthToAdd = min(toAddLength, streamRemainingPlaces);
        int shiftAmt = toAddLength - minLengthToAdd;
        uint32_t actualToAdd = toAdd >> shiftAmt;
        virtualBitStream = (virtualBitStream << minLengthToAdd) | actualToAdd;

        if(shiftAmt){ // we still have some bits to add
            uint32_t firstIgnoredBit = 1 << (shiftAmt - 1);
            toAdd = toAdd & (firstIgnoredBit | (firstIgnoredBit - 1)); // this is the part that is not still added
        }
        
        toAddLength -= minLengthToAdd;
        streamRemainingPlaces -= minLengthToAdd;
        writeToFile(false);
    }
}

void Compressor :: writeToFile(bool force){
    if(!force && streamRemainingPlaces != 0) return; // we write only if we have no remaining places in virtual stream
    // To Write in Big-Endian 
    char buffer[4]; 
    buffer[0] = (virtualBitStream >> 24) & 0xFF; 
    buffer[1] = (virtualBitStream >> 16) & 0xFF;
    buffer[2] = (virtualBitStream >> 8) & 0xFF; 
    buffer[3] = virtualBitStream & 0xFF;
    outputFile.write(buffer, sizeof(buffer));
    // Resetting every thing
    virtualBitStream = 0;
    streamRemainingPlaces = 32;
}

void Compressor :: writeUnderFlow(uint8_t lowMSBit) {
    if(underflowCount){
        uint32_t underFlowBits = (1 - lowMSBit) << (underflowCount - 1);
        if(!lowMSBit) underFlowBits |= (underFlowBits - 1); // we set all prev bits to 1 iff msbit was 0 
        writeToBitStream(underFlowBits, underflowCount);
        underflowCount = 0;
    }
}

void Compressor :: writeRepresentative(){
    writeToBitStream(1, 2);
    if(streamRemainingPlaces){
        virtualBitStream = virtualBitStream << streamRemainingPlaces;
        virtualBitStream |= (virtualBitStream - 1);
        writeToFile(true);
    }
}