package com.elghobaty.moviest.utility;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileHelper {

    private static final Pattern remotePathPattern = Pattern.compile("\\w+?://");

    public static boolean isRemotePath(String path) {
        Matcher matcher = remotePathPattern.matcher(path);
        return matcher.find();
    }

    public static boolean isLocalPath(String path) {
        return !isRemotePath(path);
    }

    public static String getFileExtension(String posterPath) {
        int lastDot = posterPath.lastIndexOf('.');
        if (lastDot != -1 && lastDot < posterPath.length() - 1) {
            return posterPath.substring(lastDot + 1);
        }
        return null;
    }
}
