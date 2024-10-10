package org.example.fileexplorerjavafx;

import java.io.File;

public class FileEnhanced {
    private FileType fileType;
    private File file;

    public FileEnhanced() {
    }

    public FileEnhanced(File file, FileType fileType) {
        this.file = file;
        this.fileType = fileType;
    }

    /**
     * Gets the file type (Documents, Images, etc.)
     */
    public FileType getFileType() {
        return fileType;
    }

    /**
     * Sets the file type (Documents, Images, etc.)
     */
    public void setFileType(FileType fileType) {
        this.fileType = fileType;
    }

    /**
     * Gets the actual file object.
     */
    public File getFile() {
        return file;
    }

    /**
     * Sets the actual file object.
     */
    public void setFile(File file) {
        this.file = file;
    }

    /**
     * Returns the file's name for display in the UI.
     */
    public String getFileName() {
        return file != null ? file.getName() : "";
    }

    /**
     * Returns the file's size in a human-readable format.
     */
    public String getFileSize() {
        if (file != null && file.isFile()) {
            long sizeInBytes = file.length();
            return formatSize(sizeInBytes);
        }
        return "Unknown";
    }

    /**
     * Formats the file size in bytes to a more readable format (KB, MB, GB).
     */
    private String formatSize(long sizeInBytes) {
        if (sizeInBytes >= 1024 * 1024 * 1024) {
            return String.format("%.2f GB", sizeInBytes / (1024.0 * 1024 * 1024));
        } else if (sizeInBytes >= 1024 * 1024) {
            return String.format("%.2f MB", sizeInBytes / (1024.0 * 1024));
        } else if (sizeInBytes >= 1024) {
            return String.format("%.2f KB", sizeInBytes / 1024.0);
        } else {
            return sizeInBytes + " B";
        }
    }

    @Override
    public String toString() {
        return getFileName();
    }

    /**
     * Equality is based on the file's absolute path.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof FileEnhanced)) return false;
        FileEnhanced other = (FileEnhanced) obj;
        return file.getAbsolutePath().equals(other.file.getAbsolutePath());
    }

    @Override
    public int hashCode() {
        return file.getAbsolutePath().hashCode();
    }
}
