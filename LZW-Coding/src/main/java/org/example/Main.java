package org.example;
import java.io.IOException;
import static org.example.LZWCompression.*;
import static org.example.LZWDecompression.startDecompression;
import static org.example.Utils.readFile;

public class Main {
    public static void main(String[] args) throws IOException {
        // File path to compress
        String inputFilePath = "src/main/resources/contact_collection.rb";

        // Compress the file
        String compressedFilePath = startCompression(inputFilePath);

        // Decompress the file
        startDecompression(compressedFilePath, inputFilePath.substring(inputFilePath.lastIndexOf(".")));

        String outputFilePath = inputFilePath.substring(0, inputFilePath.lastIndexOf(".")) + inputFilePath.substring(inputFilePath.lastIndexOf("."));

        // Check if decompressed file matches the original
        assert new String(readFile(inputFilePath)).equals(new String(readFile(outputFilePath))) : "The decompressed file does not match the original!";

    }
}