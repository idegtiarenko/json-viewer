package com.github.idegtiarenko.json;

public class FileSystem {

    public static String sizeToString(long size) {
        if (size < 2L << 9) {
            return String.format("%d B", size);
        } else if (size < 2L << 19) {
            return String.format("%.1f KB", (double) size / (2L << 9));
        } else if (size < 2L << 29) {
            return String.format("%.1f MB", (double) size / (2L << 19));
        } else if (size < 2L << 39) {
            return String.format("%.1f GB", (double) size / (2L << 29));
        } else {
            return "a lot";
        }
    }
}
