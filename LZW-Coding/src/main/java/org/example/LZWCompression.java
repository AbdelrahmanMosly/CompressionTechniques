package org.example;
import java.io.*;
import java.util.HashMap;
import java.util.Map;
import static org.example.Utils.*;

public class LZWCompression {

    private static int bitSize;
    private static String bitBuffer;

    public static String startCompression(String filePath) {
        bitSize = 0;
        bitBuffer = "";
        precalculateBitSize(filePath);
        String compressedFilePath = compressFile(filePath);
        bitSize = 0;
        bitBuffer = "";
        return compressedFilePath;
    }

    // Read file and calculate bit size required for encoding through simulating the whole encoding process
    private static void precalculateBitSize(String filePath) {
        Map<String, Integer> dictionary = initializeDictionary();
        int dictSize = 256;
        int maxPacketSize = 256;
        String w = "";

        try (FileInputStream fileInputStream = new FileInputStream(filePath);
             DataInputStream dataInputStream = new DataInputStream(fileInputStream)) {

            while (true) {
                try {
                    byte c = dataInputStream.readByte();
                    int ch = byteToInt(c);
                    String wc = w + (char) ch;
                    if (dictionary.containsKey(wc)) {
                        w = wc;
                    } else {
                        if (maxPacketSize < 100000) {
                            dictionary.put(wc, dictSize++);
                            maxPacketSize += wc.length();
                        }
                        w = "" + (char) ch;
                    }
                } catch (EOFException eof) {
                    System.out.println("End of File");
                    break;
                }
            }
        } catch (IOException e) {
            System.out.println("IO exception = " + e);
        }

        computeBitSize(dictSize);
    }

    // Convert the input file to a compressed file using LZW algorithm
    private static String compressFile(String filePath) {
        Map<String, Integer> dictionary = initializeDictionary();
        int dictSize = 256;
        bitBuffer = "";
        int maxPacketSize = 256;
        String w = "";

        String outputFilePath = filePath.substring(0, filePath.lastIndexOf(".")) + ".lzw";

        try (FileInputStream fileInputStream = new FileInputStream(filePath);
             DataInputStream dataInputStream = new DataInputStream(fileInputStream);
             FileOutputStream fileOutputStream = new FileOutputStream(outputFilePath);
             DataOutputStream dataOutputStream = new DataOutputStream(fileOutputStream)) {

            dataOutputStream.writeInt(bitSize);

            while (true) {
                try {
                    byte c = dataInputStream.readByte();
                    int ch = byteToInt(c);

                    String wc = w + (char) ch;
                    if (dictionary.containsKey(wc)) {
                        w = wc;
                    } else {
                        writeBits(dataOutputStream, dictionary.get(w));

                        if (maxPacketSize < 100000) {
                            dictionary.put(wc, dictSize++);
                            maxPacketSize += wc.length();
                        }
                        w = "" + (char) ch;
                    }
                } catch (EOFException eof) {
                    System.out.println("End of File");
                    break;
                }
            }

            if (!w.isEmpty()) {
                writeBits(dataOutputStream, dictionary.get(w));
                flushBits(dataOutputStream);
            }

        } catch (IOException e) {
            System.out.println("IO exception = " + e);
        }

        return outputFilePath;
    }

    // Precompute the necessary bit size for the dictionary
    private static void computeBitSize(int dictSize) {
        if (dictSize <= 1) {
            bitSize = 1;
        } else {
            bitSize = 0;
            long i = 1;
            while (i < dictSize) {
                i *= 2;
                bitSize++;
            }
        }
    }

    // Initialize dictionary with single character strings
    private static Map<String, Integer> initializeDictionary() {
        Map<String, Integer> dictionary = new HashMap<>();
        for (int i = 0; i < 256; i++) {
            dictionary.put("" + (char) i, i);
        }
        return dictionary;
    }

    // Write bits to the output stream
    private static void writeBits(DataOutputStream out, int value) throws IOException {
        bitBuffer += intToBinary(value, bitSize);
        while (bitBuffer.length() >= 8) {
            out.write(stringToByte(bitBuffer.substring(0, 8)));
            bitBuffer = bitBuffer.substring(8);
        }
    }

    // Flush remaining bits
    private static void flushBits(DataOutputStream out) throws IOException {
        if (!bitBuffer.isEmpty()) {
            out.write(stringToByte(bitBuffer));
        }
    }
}
