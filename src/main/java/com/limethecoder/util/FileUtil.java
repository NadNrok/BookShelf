package com.limethecoder.util;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileUtil {
    private static final Logger logger = LoggerFactory
            .getLogger(FileUtil.class);
    private final static String FILE_DIR = "files";
    private final static String ROOT_PATH = System.getProperty("catalina.home") +
            File.separator + FILE_DIR;

    public static void saveFile(MultipartFile file, String filename) {
        if(file != null && !file.isEmpty()) {
            File directory = new File(ROOT_PATH);

            if(!directory.exists()) {
                directory.mkdirs();
            }

            String path = getFullPath(filename);

            removeFileIfExists(path);

            try {
                file.transferTo(new File(path));
                logger.info("File saved: " + path);

            } catch (IOException e) {
                logger.error("Cannot save file " + e.getMessage());
            }
        }
    }

    public static byte[] loadImage(String filename) {
        String path = getFullPath(filename);
        Path p = Paths.get(path);
        byte[] bytes = null;

        try {
            bytes = Files.readAllBytes(p);
        } catch (IOException e) {
            logger.error("Cannot load file " + e.getMessage());
        }

        return bytes;
    }

    public static String getFullPath(String filename) {
        return ROOT_PATH + File.separator + filename;
    }

    public static boolean removeFileIfExists(String filename) {
        if(filename == null || filename.isEmpty()) {
            return false;
        }

        File file = new File(getFullPath(filename));

        return file.exists() && file.delete();
    }

    public static boolean isExists(String filename) {
        if(filename == null || filename.isEmpty()) {
            return false;
        }

        File file = new File(getFullPath(filename));

        return file.exists();
    }
}
