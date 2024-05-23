#include <fstream>
#include <iostream>

#include "FreqTableBuilder.h"


const int alphabetNum = 257; // the 256 possible byte values + EOF Character

FreqTableBuilder :: FreqTableBuilder(): frequencyTable(alphabetNum, 0) {} 

bool FreqTableBuilder :: buildFrequencyTable(string filePath){
    ifstream file(filePath, ios::binary);
    if (!file.is_open()) return false; 
    
    char byte;
    while (file.get(byte)) frequencyTable[uint32_t(byte) & 0xFF]++;
    frequencyTable[alphabetNum - 1] = 1; // EOF happened only once per file.
    
    file.close();
    return true;
}

vector<uint32_t> FreqTableBuilder :: getCumFreqTable(){
    vector<uint32_t> cumulativeFreqTable(alphabetNum + 1, 0); // CF_low for each character and CF_high for EOF
    for(int i = 1; i <= alphabetNum; i++) cumulativeFreqTable[i] = cumulativeFreqTable[i - 1] + frequencyTable[i - 1];
    return cumulativeFreqTable;
}
