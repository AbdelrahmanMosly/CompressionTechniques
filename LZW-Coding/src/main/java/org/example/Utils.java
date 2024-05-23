package org.example;

import java.io.*;

public class Utils{
    static byte[] readFile(String filePath) throws IOException {
        File file = new File(filePath);
        try (BufferedInputStream inputStream = new BufferedInputStream(new FileInputStream(file))) {
            byte[] data = new byte[(int) file.length()];
            inputStream.read(data);
            return data;
        }
    }

    static int binaryStringToInt(String s) {
        int result = 0;
        for (int i = 0; i < s.length(); i++) {
            result *= 2;
            if (s.charAt(i) == '1')
                result++;
        }
        return result;
    }
    static int byteToInt(Byte b) {
        int result = b;
        if (result < 0) {
            result += 256;
        }
        return result;
    }

    static String intToBinary(int input, int bitSize) {
        StringBuilder result = new StringBuilder();
        StringBuilder reversed = new StringBuilder();
        if (input == 0)
            result = new StringBuilder("0");
        int i;
        while (input != 0) {
            if ((input % 2) == 1)
                result.append("1");
            else
                result.append("0");
            input /= 2;
        }
        for (i = result.length() - 1; i >= 0; i--) {
            reversed.append(result.charAt(i));
        }
        while (reversed.length() != bitSize) {
            reversed.insert(0, "0");
        }
        return reversed.toString();
    }

    static Byte stringToByte(String input) {
        int i, n = input.length();
        byte result = 0;
        for (i = 0; i < n; i++) {
            result *= (byte) 2.;
            if (input.charAt(i) == '1')
                result++;
        }
        for (; n < 8; n++)
            result *= (byte) 2.;
        return result;
    }

}
