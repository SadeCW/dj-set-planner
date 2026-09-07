package com.djapp.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileScanner {

    // List of supported audio file extensions
    private static final String[] SUPPORTED_EXTENSIONS = { ".wav", ".flac", ".m4a" };

    /**
     * Recursively scans a directory and returns all audio files (.wav, .flac, .m4a)
     * 
     * @param rootPath The root folder to scan
     * @return List of File objects for all audio files found
     */
    public List<File> scan(String rootPath) {
        List<File> audioFiles = new ArrayList<>();
        File root = new File(rootPath);

        if (!root.exists() || !root.isDirectory()) {
            System.err.println("❌ Invalid directory: " + rootPath);
            return audioFiles;
        }

        scanRecursive(root, audioFiles);
        return audioFiles;
    }

    /**
     * Recursive helper method to traverse folders
     */
    private void scanRecursive(File folder, List<File> audioFiles) {
        File[] files = folder.listFiles();
        if (files == null)
            return;

        for (File file : files) {
            if (file.isDirectory()) {
                // Recursively scan subfolders
                scanRecursive(file, audioFiles);
            } else {
                // Check if it's a supported audio file
                String name = file.getName().toLowerCase();
                for (String ext : SUPPORTED_EXTENSIONS) {
                    if (name.endsWith(ext)) {
                        audioFiles.add(file);
                        break;
                    }
                }
            }
        }
    }

    /**
     * Counts how many audio files are in the library (without scanning them all)
     */
    public int countAudioFiles(String rootPath) {
        return scan(rootPath).size();
    }
}