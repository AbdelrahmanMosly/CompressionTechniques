package org.example.decoding;

import java.io.*;
import java.nio.ByteBuffer;
import java.util.HashMap;

public class HuffmanDecoding {

    public static byte[] huffmanDecode(byte[] encodedText, HashMap<String, Byte> codeTable, int encodedTextLength) {
        ByteArrayOutputStream decodedStream = new ByteArrayOutputStream();
        StringBuilder currentCode = new StringBuilder();
        int bitCount = 0;
        for (byte encodedByte : encodedText) {
            for (int i = 7; i >= 0; i--) {
                boolean bit = ((encodedByte >> i) & 1) == 1;
                currentCode.append(bit ? '1' : '0');
                bitCount++;
                if (codeTable.containsKey(currentCode.toString())) {
                    decodedStream.write(codeTable.get(currentCode.toString()));
                    currentCode.setLength(0); // Reset the current code
                }

                if (bitCount == encodedTextLength) {
                    break;
                }
            }
            if (bitCount == encodedTextLength) {
                break;
            }
        }
        return decodedStream.toByteArray();
    }

    public static HashMap<String, Byte> deserializeCodeTable(String serializedCodes) {
        HashMap<String, Byte> codes = new HashMap<>();
        String[] lines = serializedCodes.split("\n");
        for (String line : lines) {
            if (!line.isEmpty()) {
                if (line.contains("\t")) {
                    String[] parts = line.split("\t");
                    codes.put(parts[1], Byte.parseByte(parts[0]));
                }
            }
        }
        return codes;
    }

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: java HuffmanDecoding <input file> <output file>");
            return;
        }
        String inputFileName = args[0];
        String outputFileName = null;
        if (!inputFileName.endsWith(".encoded")) {
            System.out.println("Input file must have a .encoded extension");
            return;
        }
        File file = new File(inputFileName);
        try (FileInputStream inputStream = new FileInputStream(file)) {
            // Read metadata length as 4-byte integer
            byte[] metadataLengthBytes = new byte[4];
            inputStream.read(metadataLengthBytes);
            int metadataLength = ByteBuffer.wrap(metadataLengthBytes).getInt();

            // Read metadata bytes
            byte[] metadataBytes = new byte[metadataLength];
            inputStream.read(metadataBytes);
            String metadata = new String(metadataBytes, "UTF-8");

            // Deserialize Huffman code table
            HashMap<String, Byte> codeTable = deserializeCodeTable(metadata);
            // Read encoded text length
            byte[] encodedTextLengthBytes = new byte[4];
            inputStream.read(encodedTextLengthBytes);
            int encodedTextLength = ByteBuffer.wrap(encodedTextLengthBytes).getInt();
            System.out.println("Encoded text length: " + encodedTextLength); // Print encoded text length for debugging

            // Read encoded text
            byte[] encodedText = new byte[encodedTextLength];
            inputStream.read(encodedText);

            String fileName = file.getName().replace(".encoded", "");
            fileName = fileName.substring(0, fileName.lastIndexOf(".")) + "_uncompressed" + fileName.substring(fileName.lastIndexOf("."));
            outputFileName = fileName;

            byte[] decodedText = huffmanDecode(encodedText, codeTable, encodedTextLength);
            try (FileOutputStream outputStream = new FileOutputStream(fileName)) {
                outputStream.write(decodedText);
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("Decompression successful. Decoded file saved to: " + outputFileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
