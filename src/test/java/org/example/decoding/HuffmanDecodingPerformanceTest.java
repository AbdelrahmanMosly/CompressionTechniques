package org.example.decoding;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;

import org.example.checker.FileHashComparison;
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

        // Decode the file
        HuffmanDecoding.main(new String[]{encodedFileName});

        // Check if the decoded file was created
        assertTrue(Files.exists(Paths.get(decodedFileName)));

        // Compare SHA256 hashes
        FileHashComparison.compareShA(INPUT_FILE_PREFIX + multiplier + ".txt", decodedFileName);
    }

    @Test
    public void testDecodingPerformanceForVariousSizes() throws IOException {
        for (int multiplier : new int[]{1, 10, 100, 1000}) {
            testDecodingPerformance(multiplier);
        }
    }
}
