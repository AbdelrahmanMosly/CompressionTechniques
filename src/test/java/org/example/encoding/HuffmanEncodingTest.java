package org.example.encoding;

import static org.junit.jupiter.api.Assertions.*;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Random;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class HuffmanEncodingTest {

    private static final String INPUT_FILE = "test_input.txt";
    private static final String ENCODED_FILE = "test_input.txt.encoded";

    @BeforeEach
    public void setUp() throws IOException {
        // Generate a test input file
        String content = "this is an example for huffman encoding";
        Files.write(Paths.get(INPUT_FILE), content.getBytes());
    }

    @Test
    public void testBuildHuffmanTree() throws IOException {
        byte[] text = Files.readAllBytes(Paths.get(INPUT_FILE));
        HashMap<Byte, String> codes = HuffmanEncoding.buildHuffmanTree(text);
        assertNotNull(codes);
        assertFalse(codes.isEmpty());
    }

    @Test
    public void testGenerateHuffmanCodes() {
        HashMap<Byte, Integer> frequency = new HashMap<>();
        frequency.put((byte) 'a', 5);
        frequency.put((byte) 'b', 9);
        frequency.put((byte) 'c', 12);
        frequency.put((byte) 'd', 13);
        frequency.put((byte) 'e', 16);
        frequency.put((byte) 'f', 45);

        HashMap<Byte, String> codes = HuffmanEncoding.generateHuffmanCodes(frequency);
        assertNotNull(codes);
        assertEquals(6, codes.size());
    }

    @Test
    public void testConvertBinaryStringToByteArray() {
        StringBuilder binaryString = new StringBuilder("101010001110");
        byte[] byteArray = HuffmanEncoding.convertBinaryStringToByteArray(binaryString);
        assertNotNull(byteArray);
        assertTrue(byteArray.length > 0);
    }

    @Test
    public void testMain() throws IOException {
        // Run the encoding process
        HuffmanEncoding.main(new String[]{INPUT_FILE});

        // Check if the encoded file was created
        assertTrue(Files.exists(Paths.get(ENCODED_FILE)));
    }
}
