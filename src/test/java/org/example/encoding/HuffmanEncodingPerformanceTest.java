package org.example.encoding;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class HuffmanEncodingPerformanceTest {

    private static final String BASE_CONTENT = "this is an example for huffman encoding";
    private static final String INPUT_FILE_PREFIX = "test_input_";
    private static final String ENCODED_FILE_SUFFIX = ".encoded";

    @BeforeEach
    public void setUp() throws IOException {
        // Generate test input files with different multipliers
        for (int multiplier : new int[]{1, 10, 100, 1000}) {
            String content = BASE_CONTENT.repeat(multiplier);
            Files.write(Paths.get(INPUT_FILE_PREFIX + multiplier + ".txt"), content.getBytes());
        }
    }

    private void testEncodingPerformance(int multiplier) throws IOException {
        String inputFileName = INPUT_FILE_PREFIX + multiplier + ".txt";
        String encodedFileName = inputFileName + ENCODED_FILE_SUFFIX;

        byte[] text = Files.readAllBytes(Paths.get(inputFileName));
        long startTime = System.nanoTime();
        HashMap<Byte, String> codes = HuffmanEncoding.buildHuffmanTree(text);
        long endTime = System.nanoTime();
        long duration = (endTime - startTime) / 1_000_000; // Convert to milliseconds
        System.out.println("Huffman tree building time for multiplier " + multiplier + ": " + duration + " ms");

        assertNotNull(codes);
        assertFalse(codes.isEmpty());

        startTime = System.nanoTime();
        HuffmanEncoding.main(new String[]{inputFileName});
        endTime = System.nanoTime();
        duration = (endTime - startTime) / 1_000_000; // Convert to milliseconds
        System.out.println("Encoding process time for multiplier " + multiplier + ": " + duration + " ms");

        assertTrue(Files.exists(Paths.get(encodedFileName)));
    }

    @Test
    public void testEncodingPerformanceForVariousSizes() throws IOException {
        for (int multiplier : new int[]{1, 10, 100, 1000}) {
            testEncodingPerformance(multiplier);
        }
    }
}
