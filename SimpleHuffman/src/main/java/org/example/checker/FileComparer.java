package org.example.checker;

import java.io.FileInputStream;
import java.io.IOException;

public class FileComparer {

    public static void compareFiles(String file1Path, String file2Path) throws IOException {
        try (FileInputStream file1Stream = new FileInputStream(file1Path);
             FileInputStream file2Stream = new FileInputStream(file2Path)) {
            int byteReadFrom1, byteReadFrom2;
            int position = 0;
            while ((byteReadFrom1 = file1Stream.read()) != -1 && (byteReadFrom2 = file2Stream.read()) != -1) {
                if (byteReadFrom1 != byteReadFrom2) {
                    System.out.println("Difference found at position " + position);
                    System.out.println("Byte in file1: " + byteReadFrom1);
                    System.out.println("Byte in file2: " + byteReadFrom2);
                    return;
                }
                position++;
            }
            // Check if one file is longer than the other
            if (file1Stream.read() != -1 || file2Stream.read() != -1) {
                System.out.println("Files have different lengths");
            } else {
                System.out.println("Files are identical");
            }
        }
    }

    public static void main(String[] args) {
        String file1Path = "input.txt";
        String file2Path = "input_uncompressed.txt";
        try {
            compareFiles(file1Path, file2Path);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
