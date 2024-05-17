package org.example.encoding;
import java.io.*;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.PriorityQueue;

public class HuffmanEncoding {

    static class Node implements Comparable<Node> {
        byte character;
        int frequency;
        Node left, right;

        Node(byte character, int frequency) {
            this.character = character;
            this.frequency = frequency;
        }

        @Override
        public int compareTo(Node other) {
            return this.frequency - other.frequency;
        }
    }

    public static HashMap<Byte, String> buildHuffmanTree(byte[] text) {
        HashMap<Byte, Integer> frequency = new HashMap<>();
        for (byte b : text) {
            frequency.put(b, frequency.getOrDefault(b, 0) + 1);
        }
        System.out.println(frequency);

        return generateHuffmanCodes(frequency);
    }

    public static HashMap<Byte, String> generateHuffmanCodes(HashMap<Byte, Integer> frequency) {
        HashMap<Byte, String> codes = new HashMap<>();
        if (frequency.size() == 1) {
            byte character = frequency.keySet().iterator().next();
            codes.put(character, "0");
            return codes;
        }

        PriorityQueue<Node> pq = new PriorityQueue<>();
        for (byte character : frequency.keySet()) {
            pq.add(new Node(character, frequency.get(character)));
        }

        Node root = buildTree(pq);

        generateCodes(root, "", codes);

        System.out.println(codes);
        return codes;
    }

    private static Node buildTree(PriorityQueue<Node> pq) {
        while (pq.size() > 1) {
            Node node1 = pq.poll();
            Node node2 = pq.poll();

            Node mergedNode = new Node((byte) 0, node1.frequency + node2.frequency);
            mergedNode.left = node1;
            mergedNode.right = node2;
            pq.add(mergedNode);
        }
        return pq.poll(); // Return the root node
    }

    private static void generateCodes(Node node, String code, HashMap<Byte, String> codes) {
        if (node == null) {
            return;
        }
        if (node.left == null && node.right == null) {
            codes.put(node.character, code);
            return;
        }
        generateCodes(node.left, code + "0", codes);
        generateCodes(node.right, code + "1", codes);
    }

    public static String serializeCodeTable(HashMap<Byte, String> codes) {
        StringBuilder serializedCodes = new StringBuilder();
        for (byte character : codes.keySet()) {
            serializedCodes.append(character).append("\t").append(codes.get(character)).append("\n");
        }
        return serializedCodes.toString();
    }
    public static byte[] convertBinaryStringToByteArray(StringBuilder binaryString) {
        int length = binaryString.length();
        int numOfBytes = (length + 7) / 8; // Calculate the number of bytes needed

        // Pad the binary string on the right with '0' characters if needed
        int paddingLength = numOfBytes * 8 - length;
        for (int i = 0; i < paddingLength; i++) {
            binaryString.append('0');
        }

        byte[] byteArray = new byte[numOfBytes];
        int byteIndex = 0;
        int bitIndex = 0;

        // Convert each 8-bit substring into a byte
        for (int i = 0; i < numOfBytes * 8; i++) {
            char bit = binaryString.charAt(i);
            if (bit == '1') {
                byteArray[byteIndex] |= (1 << (7 - bitIndex)); // Set the bit to 1
            }
            bitIndex++;
            if (bitIndex == 8) {
                byteIndex++;
                bitIndex = 0;
            }
        }

        return byteArray;
    }


    public static void main(String[] args) {
        String inputFileName = "input.txt";
        try (FileInputStream inputStream = new FileInputStream(inputFileName)) {
            byte[] text = inputStream.readAllBytes();

            HashMap<Byte, String> codes = buildHuffmanTree(text);

            StringBuilder encodedText = new StringBuilder();
            for (byte b : text) {
                encodedText.append(codes.get(b));
            }

            String metadata = serializeCodeTable(codes);

            String outputFileName = inputFileName + ".encoded";
            try (FileOutputStream outputStream = new FileOutputStream(outputFileName)) {
                // Write metadata length as 4-byte integer
                byte[] metadataLengthBytes = ByteBuffer.allocate(4).putInt(metadata.length()).array();
                outputStream.write(metadataLengthBytes);

                // Write metadata as UTF-8 encoded string
                outputStream.write(metadata.getBytes("UTF-8"));

                // Write encoded text length as 4-byte integer
                byte[] encodedTextLengthBytes = ByteBuffer.allocate(4).putInt(encodedText.length()).array();
                outputStream.write(encodedTextLengthBytes);
                System.out.println(encodedText.length());
                // Write encoded text as bytes
                byte[] encodedBytes = convertBinaryStringToByteArray(encodedText);
                outputStream.write(encodedBytes);
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("Compression successful. Encoded file saved to: " + outputFileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
