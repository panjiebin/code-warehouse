package com.code.common.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Pan Jiebin
 * @date 2021-03-02 16:01
 */
public class FileUtils {

    private FileUtils() {
    }

    public static List<File> getAllFiles(File dir) {
        List<File> allFiles = new ArrayList<>();
        File[] files = dir.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    allFiles.addAll(getAllFiles(file));
                } else {
                    allFiles.add(file);
                }
            }
        }
        return allFiles;
    }
}
