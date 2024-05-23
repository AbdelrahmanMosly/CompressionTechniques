package org.example;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.example.Utils.*;

public class LZWDecompression {

    public static int bitSize;
    public static String[] byteToBinaryString = new String[256];
    public static String bitString;

    public static void startDecompression(String inputFile, String extension) {
        bitString = "";
        bitSize = 0;
        precalculateByteToBinaryString();
        decompress(inputFile, extension);
        bitString = "";
        bitSize = 0;
    }
    private static void precalculateByteToBinaryString() {
        StringBuilder reversed;
        for (int i = 0; i < 256; i++) {
            reversed = new StringBuilder();
            byteToBinaryString[i] = "";
            int j = i;
            while (j != 0) {
                if ((j % 2) == 1)
                    byteToBinaryString[i] += "1";
                else
                    byteToBinaryString[i] += "0";
                j /= 2;
            }
            for (j = byteToBinaryString[i].length() - 1; j >= 0; j--) {
                reversed.append(byteToBinaryString[i].charAt(j));
            }
            while (reversed.length() < 8) {
                reversed.insert(0, "0");
            }
            byteToBinaryString[i] = reversed.toString();
        }
    }

    private static void decompress(String inputFile, String extension) {
        String outputFile = getOutputFileName(inputFile, extension);
        Map<Integer, String> dictionary = initializeDictionary();

        try (FileInputStream fileInputStream = new FileInputStream(new File(inputFile));
             DataInputStream dataInputStream = new DataInputStream(fileInputStream);
             FileOutputStream fileOutputStream = new FileOutputStream(new File(outputFile));
             DataOutputStream dataOutputStream = new DataOutputStream(fileOutputStream)) {

            bitSize = dataInputStream.readInt();
            readInitialBits(dataInputStream);

            if (bitString.length() >= bitSize) {
                int k = binaryStringToInt(bitString.substring(0, bitSize));
                bitString = bitString.substring(bitSize);
                String w = "" + (char) k;
                dataOutputStream.writeBytes(w);
                decompressFile(dataInputStream, dataOutputStream, dictionary, w);
            }
        } catch (IOException e) {
            System.out.println("IO exception = " + e);
        }
    }

    private static String getOutputFileName(String inputFile, String extension) {
        return inputFile.substring(0, inputFile.lastIndexOf(".")) + "_dec" + extension;
    }

    private static Map<Integer, String> initializeDictionary() {
        Map<Integer, String> dictionary = new HashMap<>();
        for (int i = 0; i < 256; i++) {
            dictionary.put(i, "" + (char) i);
        }
        return dictionary;
    }

    private static void readInitialBits(DataInputStream dataInputStream) throws IOException {
        while (true) {
            try {
                byte c = dataInputStream.readByte();
                bitString += byteToBinaryString[byteToInt(c)];
                if (bitString.length() >= bitSize) break;
            } catch (EOFException eof) {
                System.out.println("End of File");
                break;
            }
        }
    }

    private static void decompressFile(DataInputStream dataInputStream, DataOutputStream dataOutputStream, Map<Integer, String> dictionary, String w) throws IOException {
        int dictSize = 256;
        int maxPacketSize = 256;

        while (true) {
            try {
                while (bitString.length() < bitSize) {
                    byte c = dataInputStream.readByte();
                    bitString += byteToBinaryString[byteToInt(c)];
                }
                int k = binaryStringToInt(bitString.substring(0, bitSize));
                bitString = bitString.substring(bitSize);

                String entry = getDictionaryEntry(dictionary, k, dictSize, w);
                dataOutputStream.writeBytes(entry);

                if (maxPacketSize < 100000) {
                    String temp = w + entry.charAt(0);
                    dictionary.put(dictSize++, temp);
                    maxPacketSize += temp.length();
                }
                w = entry;
            } catch (EOFException eof) {
                System.out.println("End of File");
                break;
            }
        }
    }

    private static String getDictionaryEntry(Map<Integer, String> dictionary, int k, int dictSize, String w) {
        if (dictionary.containsKey(k)) {
            return dictionary.get(k);
        } else if (k == dictSize) {
            return w + w.charAt(0);
        }
        return "";
    }
}