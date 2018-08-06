package com.hizyaz.adrianapp.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;

public class AppHelper {

    public static boolean allowedFileSize(String filePath) {
        File file = new File(filePath);
        int file_size = Integer.parseInt(String.valueOf(file.length() / 1024));
        return file_size <= 5000;
    }

    public static byte[] getBytesFromPath(String filePath) throws FileNotFoundException {
        byte[] bytes;
        File file = new File(filePath);
        if (file.isFile()) {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bytes = byteArrayOutputStream.toByteArray();
        } else {
            throw new FileNotFoundException(filePath + " doesn't exist. ");
        }
        return bytes;
    }
}