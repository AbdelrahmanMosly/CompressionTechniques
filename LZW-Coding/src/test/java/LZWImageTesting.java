import org.example.LZWCompression;
import org.example.LZWDecompression;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
public class LZWImageTesting {
    private static final String TEST_INPUT_FILE = "src/main/resources/riddle.jpg";
    private static final String COMPRESSED_FILE = "src/main/resources/riddle.lzw";
    private static final String DECOMPRESSED_FILE = "src/main/resources/riddle_dec.jpg";

    @BeforeAll
    public static void setUp() throws IOException {
        // Compress the file using LZWCompression
        LZWCompression.startCompression(TEST_INPUT_FILE);
    }


    @Test
    public void testDecompression() throws IOException {
        // Decompress the file using LZWDecompression
        LZWDecompression.startDecompression(COMPRESSED_FILE, ".jpg");

        // Read the original and decompressed files
        byte[] originalBytes = Files.readAllBytes(Paths.get(TEST_INPUT_FILE));
        byte[] decompressedBytes = Files.readAllBytes(Paths.get(DECOMPRESSED_FILE));

        // Compare the original and decompressed file contents
        assertTrue(Arrays.equals(originalBytes, decompressedBytes), "Decompressed file content should match original file content");
    }

    @Test
    public void testFileCreation() {
        File compressedFile = new File(COMPRESSED_FILE);
        File decompressedFile = new File(DECOMPRESSED_FILE);

        // Check if the compressed and decompressed files are created
        assertTrue(compressedFile.exists(), "Compressed file should be created");
        assertTrue(decompressedFile.exists(), "Decompressed file should be created");
    }
}