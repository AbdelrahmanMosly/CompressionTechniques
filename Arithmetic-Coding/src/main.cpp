#include <iostream>
#include <chrono> // for std::chrono
#include <thread> // for std::this_thread::sleep_for
#include <fstream>

#include "FreqTableBuilder.h"
#include "Compressor.h"
#include "DeCompressor.h"

using namespace std;

int main(int argc, char **argv){
    string help = "Usage: ./main [options]\nOptions:\n-h, --help\t\t\tShow this message\n-c, --compress filename\t\tCompress the file\n-d, --decompress filename\tDecompress the file\n";
    if(argc == 1){
        cout << help;
        exit(1);
    }else if(argc == 2){
        if(string(argv[1]) == "-h" || string(argv[1]) == "--help"){
            cout << help;
            exit(1);
        }else{
            cerr << "Invalid Arguments\n";
            exit(1);
        }
    }else if(argc == 3){
        if(string(argv[1]) == "-c" || string(argv[1]) == "--compress"){
            FreqTableBuilder freqBuilder = FreqTableBuilder();
            freqBuilder.buildFrequencyTable(argv[2]);
            vector<uint32_t> cumFreqTable = freqBuilder.getCumFreqTable();
            Compressor compressor = Compressor(cumFreqTable, argv[2]);
            long tic = chrono::duration_cast<chrono::milliseconds>(chrono::system_clock::now().time_since_epoch()).count();
            compressor.compress();
            long toc = chrono::duration_cast<chrono::milliseconds>(chrono::system_clock::now().time_since_epoch()).count();
            cout << "Compressed Successfully (" << toc - tic << "ms)\n";
        }else if(string(argv[1]) == "-d" || string(argv[1]) == "--decompress"){
            DeCompressor deCompressor = DeCompressor(argv[2]);
            long tic = chrono::duration_cast<chrono::milliseconds>(chrono::system_clock::now().time_since_epoch()).count();
            deCompressor.deCompress();
            long toc = chrono::duration_cast<chrono::milliseconds>(chrono::system_clock::now().time_since_epoch()).count();
            cout << "DeCompressed Successfully (" << toc - tic << "ms)\n";
        }else{
            cerr << "Invalid Arguments\n";
            exit(1);
        }
    }else{
        cerr << "Invalid Arguments\n";
        exit(1);
    }
    return 0;
}