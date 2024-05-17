package org.example.encoding;

class HuffmanNode {
    int frequency;
    char character;
    HuffmanNode left, right;

    HuffmanNode(int frequency, char character) {
        this.frequency = frequency;
        this.character = character;
        left = right = null;
    }
}
