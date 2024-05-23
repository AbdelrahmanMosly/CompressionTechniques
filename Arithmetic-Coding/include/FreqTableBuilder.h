#ifndef FREQTABLEBUILDER_H
#define FREQTABLEBUILDER_H

#include <vector>
#include <cstdint>
#include <iostream>

using namespace std;


class FreqTableBuilder {
private:
    vector<uint32_t> frequencyTable;

public:
    FreqTableBuilder();
    bool buildFrequencyTable(string filePath);
    vector<uint32_t> getCumFreqTable();
};

#endif // FREQTABLEBUILDER_H