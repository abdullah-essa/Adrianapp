package com.hizyaz.adrianapp.utils;

import android.util.Log;
import android.util.Patterns;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AppHelper {

    public static boolean isEmailValid(String email){
        Pattern pattern = Patterns.EMAIL_ADDRESS;
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
    public static boolean allowedFileSize(String filePath) {
        File file = new File(filePath);
        int file_size = Integer.parseInt(String.valueOf(file.length() / 1024));
        Log.e("Size", String.valueOf(file_size));
        return file_size <= 5000;
    }

//AppHelper.getFileDataFromDrawable(getBaseContext(), mCoverImage.getDrawable())
//    public static byte[] getBytesFromPath(Context c, String filePath) throws IOException {
//    public static byte[] getBytesFromPath(String filePath) throws IOException {
//        byte[] bytes;
//        File file = new File(filePath);
//        if (file.isFile()) {
////            InputStream inputStream = new FileInputStream(file);
//            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
////            byte[] b = new byte[1024*5];
////            int bytesRead =0;
////            while ((bytesRead = inputStream.read(b)) != -1)
////            {
////                byteArrayOutputStream.write(b, 0, bytesRead);
////            }
//
//            bytes = byteArrayOutputStream.toByteArray();
//
////            bytes = byteArrayOutputStream.toByteArray();
//
//        } else {
//            throw new FileNotFoundException(filePath + " doesn't exist. ");
//        }
//        return bytes;
//    }
}