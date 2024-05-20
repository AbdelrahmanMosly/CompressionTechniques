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

public class HuffmanDecodingTest {

    private static final String ENCODED_FILE = "test_input.txt.encoded";
    private static final String DECODED_FILE = "test_input_uncompressed.txt";

    @BeforeEach
    public void setUp() throws IOException {
        // Generate a test encoded file
        String content = "this is an example for huffman encoding";
        Files.write(Paths.get(DECODED_FILE), content.getBytes());
        HuffmanEncoding.main(new String[]{DECODED_FILE});
    }

    @Test
    public void testDeserializeCodeTable() throws IOException {
        byte[] metadataBytes = Files.readAllBytes(Paths.get(ENCODED_FILE));
        String metadata = new String(metadataBytes, "UTF-8").split("\n")[1];
        HashMap<String, Byte> codes = HuffmanDecoding.deserializeCodeTable(metadata);
        assertNotNull(codes);
        assertFalse(codes.isEmpty());
    }

    @Test
    public void testHuffmanDecode() throws IOException {
        try (FileInputStream inputStream = new FileInputStream(ENCODED_FILE)) {
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

            byte[] decodedText = HuffmanDecoding.huffmanDecode(encodedText, codeTable, encodedTextLength);
            assertNotNull(decodedText);
            assertTrue(decodedText.length > 0);
        }
    }

    @Test
    public void testMain() throws IOException {
        // Run the decoding process
        HuffmanDecoding.main(new String[]{ENCODED_FILE});

        // Check if the decoded file was created
        assertTrue(Files.exists(Paths.get(DECODED_FILE)));
    }
}
