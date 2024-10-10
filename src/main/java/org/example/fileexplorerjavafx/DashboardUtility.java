package org.example.fileexplorerjavafx;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javax.swing.filechooser.FileSystemView;
import java.io.File;
import java.util.*;
import java.util.function.Consumer;

public class DashboardUtility {

    private ObservableList<FileEnhanced> fileEnhanceds;

    public DashboardUtility() {
        fileEnhanceds = FXCollections.observableArrayList();
    }

    /**
     * Retrieves all drives available on the system.
     */
    public static List<Drive> getAllDrives() {
        List<Drive> drives = new ArrayList<>();
        File[] rootDirectories = File.listRoots();

        for (File root : rootDirectories) {
            Drive drive = new Drive(root);
            drives.add(drive);
        }

        return drives;
    }

    /**
     * Generalized method to retrieve all files of a specific type from the given drive.
     */
    public ObservableList<FileEnhanced> recursiveGetAllFiles(File rootFolder, FileType fileType) {
        ObservableList<FileEnhanced> fileEnhanceds = FXCollections.observableArrayList();
        System.out.println("Searching for " + fileType.name() + " files in folder: " + rootFolder.getAbsolutePath());

        if (rootFolder.isDirectory()) {
            File[] files = rootFolder.listFiles();

            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        fileEnhanceds.addAll(recursiveGetAllFiles(file, fileType));
                    } else {
                        if (isValidFileType(file, fileType)) {
                            FileEnhanced enhancedFile = new FileEnhanced(file, fileType);
                            fileEnhanceds.add(enhancedFile);

                            System.out.println("Found " + fileType.name() + " file: " + file.getAbsolutePath());
                        }
                    }
                }
            } else {
                System.out.println("No files found or cannot access directory: " + rootFolder.getAbsolutePath());
            }
        }

        System.out.println("Found " + fileEnhanceds.size() + " " + fileType.name() + " files in: " + rootFolder.getAbsolutePath());

        return fileEnhanceds;
    }

    /**
     * Recursive method to search for files of a given file type.
     */
    private void recursiveFileSearch(File[] files, FileType fileType) {
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    recursiveFileSearch(file.listFiles(), fileType);
                } else {
                    if (isValidFileType(file, fileType)) {
                        FileEnhanced fileEnhanced = new FileEnhanced();
                        fileEnhanced.setFile(file);
                        fileEnhanced.setFileType(fileType);
                        fileEnhanceds.add(fileEnhanced);
                    }
                }
            }
        }
    }

    public void searchFilesInBatches(File rootDir, FileType fileType, Consumer<List<FileEnhanced>> batchConsumer) {
        Set<String> visitedPaths = new HashSet<>();
        Queue<File> queue = new LinkedList<>();
        queue.add(rootDir);

        while (!queue.isEmpty()) {
            File currentDir = queue.poll();
            if (visitedPaths.contains(currentDir.getAbsolutePath())) {
                continue;
            }
            visitedPaths.add(currentDir.getAbsolutePath());

            File[] files = currentDir.listFiles();
            if (files != null) {
                List<FileEnhanced> batch = new ArrayList<>();
                for (File file : files) {
                    if (file.isDirectory()) {
                        queue.add(file);
                    } else if (isValidFileType(file, fileType)) {
                        batch.add(new FileEnhanced(file, fileType));
                    }
                }
                if (!batch.isEmpty()) {
                    batchConsumer.accept(batch);
                }
            }
        }
    }

    private void searchDirectory(File directory, FileType fileType, ObservableList<FileEnhanced> batch, Consumer<ObservableList<FileEnhanced>> batchCallback, int batchSize) {
        if (directory == null || !directory.isDirectory()) return;

        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    searchDirectory(file, fileType, batch, batchCallback, batchSize);
                } else if (isValidFileType(file, fileType)) {
                    batch.add(new FileEnhanced(file, fileType));

                    if (batch.size() >= batchSize) {
                        batchCallback.accept(FXCollections.observableArrayList(batch));
                        batch.clear();
                    }
                }
            }
        }

        if (!batch.isEmpty()) {
            batchCallback.accept(FXCollections.observableArrayList(batch));
        }
    }

    private void searchFilesRecursively(File folder, FileType fileType, ObservableList<FileEnhanced> batch, Consumer<ObservableList<FileEnhanced>> batchConsumer) {
        if (!folder.isDirectory()) {
            return;
        }

        File[] files = folder.listFiles();

        if (files == null) {
            return;
        }

        for (File file : files) {
            if (file.isDirectory()) {
                searchFilesRecursively(file, fileType, batch, batchConsumer);
            } else {
                if (isValidFileType(file, fileType)) {
                    FileEnhanced enhancedFile = new FileEnhanced(file, fileType);
                    batch.add(enhancedFile);

                    if (batch.size() >= 100) {
                        batchConsumer.accept(FXCollections.observableArrayList(batch));
                        batch.clear();
                    }
                }
            }
        }

        if (!batch.isEmpty()) {
            batchConsumer.accept(FXCollections.observableArrayList(batch));
        }
    }

    /**
     * Checks if a file matches the given file type based on its extension.
     */
    private boolean isValidFileType(File file, FileType fileType) {
        String extension = getFileExtension(file.getName()).toLowerCase();
        switch (fileType) {
            case Documents:
                return Documents.isDocument(extension);
            case Images:
                return Images.isImage(extension);
            case Videos:
                return Videos.isVideo(extension);
            case Music:
                return Music.isMusic(extension);
            case Archives:
                return Archives.isArchive(extension);
            case Apps:
                return Applications.isApplication(extension);
            default:
                return false;
        }
    }

    /**
     * Extracts the file extension from a file name.
     */
    private String getFileExtension(String fileName) {
        int lastDotIndex = fileName.lastIndexOf('.');
        return (lastDotIndex == -1) ? "" : fileName.substring(lastDotIndex + 1);
    }

    /**
     * Formats file size in bytes to a human-readable format (GB).
     */
    private static String formatSize(long sizeInBytes) {
        return String.format("%.2f GB", (double) sizeInBytes / (1024 * 1024 * 1024));
    }

    /**
     * Resets all cached data.
     */
    public void reset() {
        fileEnhanceds.clear();
    }
}
