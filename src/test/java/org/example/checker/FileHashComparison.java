package org.example.checker;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import static org.junit.jupiter.api.Assertions.*;
public class FileHashComparison {

    // Function to compute the SHA256 hash of a file
    public static String sha256OfFile(String filePath) throws IOException, NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        try (InputStream inputStream = Files.newInputStream(Paths.get(filePath))) {
            byte[] buffer = new byte[65536];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                digest.update(buffer, 0, bytesRead);
            }
        }
        byte[] hashBytes = digest.digest();
        StringBuilder hexString = new StringBuilder();
        for (byte hashByte : hashBytes) {
            String hex = Integer.toHexString(0xff & hashByte);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }
    public static double comparessionRatio(String inputFilePath, String outputFilePath) {
        String encodedFilePath = inputFilePath + ".encoded";
        double compressionRatio = 0;
        try {
            // Get the size of the input file
            long inputFileSize = Files.size(Paths.get(inputFilePath));

            // Get the size of the output file
            long outputFileSize = Files.size(Paths.get(encodedFilePath));

            // Calculate compression ratio
            compressionRatio = (double) outputFileSize / inputFileSize;
            System.out.println("Compression ratio: " + compressionRatio);
            System.out.println("input file size in bytes: " + inputFileSize);
            System.out.println("encoded file size in bytes: " + compressionRatio);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return compressionRatio;
    }
    public static void compareShA(String inputFilePath, String outputFilePath) {
        try {
            // Compute the SHA256 hashes of the input and output files
            String inputHash = sha256OfFile(inputFilePath);
            String outputHash = sha256OfFile(outputFilePath);

            assertTrue(inputHash.equals(outputHash), "SHA256 hashes of the input and output files do not match");

        } catch (IOException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }
}
