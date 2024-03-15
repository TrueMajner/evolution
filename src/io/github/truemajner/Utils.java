package io.github.truemajner;

public class Utils {
    private static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_RESET = "\u001B[0m";
    public static int getRandomInteger(int min, int max) {
        return (int) ((Math.random() * (max - min)) + min);
    }

    public static byte getRandomByte(int min, int max) {
        return (byte) ((Math.random() * (max - min)) + min);
    }

    public static boolean isInteger(String string) {
        try {
            Integer.parseInt(string);
        } catch(NumberFormatException | NullPointerException e) {
            return false;
        }
        return true;
    }

    public static void printWarning(String string) {
        System.out.println(ANSI_RED + " WARNING: " + string + ANSI_RESET);
    }

    public static String join(byte[] arr, String delimiter) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < arr.length; i++) {
            sb.append(arr[i]);
            if (i < arr.length - 1) {
                sb.append(delimiter);
            }
        }
        return sb.toString();
    }
}
