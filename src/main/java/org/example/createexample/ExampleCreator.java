package org.example.createexample;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class ExampleCreator {
    public static void main(String[] args) {
        // Sample text
        String sampleText = """
                Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.
                Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat.
                    Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur.
                    Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.
                """.repeat(10);

        // Write sample text to input file
        String inputFileName = "input.txt";
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(inputFileName))) {
            writer.write(sampleText);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
