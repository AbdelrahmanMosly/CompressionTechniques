package org.example.decoding;

import static org.junit.jupiter.api.Assertions.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;

import org.example.encoding.HuffmanEncoding;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class HuffmanDecodingPerformanceTest {

    private static final String BASE_CONTENT = "this is an example for huffman encoding";
    private static final String INPUT_FILE_PREFIX = "test_input_";
    private static final String ENCODED_FILE_SUFFIX = ".encoded";
    private static final String DECODED_FILE_SUFFIX = "_uncompressed.txt";

    @BeforeEach
    public void setUp() throws IOException {
        // Generate test input files with different multipliers
        for (int multiplier : new int[]{1, 10, 100, 1000}) {
            String content = BASE_CONTENT.repeat(multiplier);
            Files.write(Paths.get(INPUT_FILE_PREFIX + multiplier + ".txt"), content.getBytes());
            HuffmanEncoding.main(new String[]{INPUT_FILE_PREFIX + multiplier + ".txt"});
        }
    }

    private void testDecodingPerformance(int multiplier) throws IOException {
        String encodedFileName = INPUT_FILE_PREFIX + multiplier + ".txt" + ENCODED_FILE_SUFFIX;
        String decodedFileName = INPUT_FILE_PREFIX + multiplier + DECODED_FILE_SUFFIX;

        try (FileInputStream inputStream = new FileInputStream(encodedFileName)) {
            byte[] metadataLengthBytes = new byte[4];
            inputStream.read(metadataLengthBytes);
            int metadataLength = ByteBuffer.wrap(metadataLengthBytes).getInt();

            byte[] metadataBytes = new byte[metadataLength];
            inputStream.read(metadataBytes);
            String metadata = new String(metadataBytes, "UTF-8");

            HashMap<String, Byte> codeTable = HuffmanDecoding.deserializeCodeTable(metadata);
            byte[] encodedTextLengthBytes = new byte[4];
            inputStream.read(encodedTextLengthBytes);
            int encodedTextLength = ByteBuffer.wrap(encodedTextLengthBytes).getInt();

            byte[] encodedText = new byte[inputStream.available()];
            inputStream.read(encodedText);

            long startTime = System.nanoTime();
            byte[] decodedText = HuffmanDecoding.huffmanDecode(encodedText, codeTable, encodedTextLength);
            long endTime = System.nanoTime();
            long duration = (endTime - startTime) / 1_000_000; // Convert to milliseconds
            System.out.println("Decoding process time for multiplier " + multiplier + ": " + duration + " ms");

            assertNotNull(decodedText);
            assertTrue(decodedText.length > 0);

            Files.write(Paths.get(decodedFileName), decodedText);
        }

        assertTrue(Files.exists(Paths.get(decodedFileName)));
    }

    @Test
    public void testDecodingPerformanceForVariousSizes() throws IOException {
        for (int multiplier : new int[]{1, 10, 100, 1000}) {
            testDecodingPerformance(multiplier);
        }
    }
}
