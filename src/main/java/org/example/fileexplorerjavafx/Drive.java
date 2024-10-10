package org.example.fileexplorerjavafx;

import java.io.File;

/**
 * Represents a storage drive in the system.
 */
public class Drive {
    private String driveName;
    private File file;
    private double usedPercentage;
    private double freePercentage;
    private String totalSpace;
    private String usedSpace;
    private String freeSpace;

    private long totalSpaceBytes;
    private long usedSpaceBytes;
    private long freeSpaceBytes;

    public Drive(File file) {
        this.file = file;
        this.driveName = file.getAbsolutePath();
        updateSpaceData();
    }

    private void updateSpaceData() {
        totalSpaceBytes = file.getTotalSpace();
        freeSpaceBytes = file.getFreeSpace();
        usedSpaceBytes = totalSpaceBytes - freeSpaceBytes;

        totalSpace = formatSize(totalSpaceBytes);
        usedSpace = formatSize(usedSpaceBytes);
        freeSpace = formatSize(freeSpaceBytes);
    }

    /**
     * Helper method to format size in bytes into a readable format (GB, MB, etc.).
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

    /**
     * Gets the name of the drive (e.g., "C:").
     */
    public String getDriveName() {
        return driveName;
    }

    /**
     * Sets the name of the drive (e.g., "C:").
     */
    public void setDriveName(String driveName) {
        this.driveName = driveName;
    }

    /**
     * Gets the root file representing the drive.
     */
    public File getFile() {
        return file;
    }

    /**
     * Sets the root file representing the drive.
     */
    public void setFile(File file) {
        this.file = file;
    }

    /**
     * Gets the total space on the drive in human-readable format.
     */
    public String getTotalSpace() {
        return totalSpace;
    }

    /**
     * Sets the total space on the drive in human-readable format.
     */
    public void setTotalSpace(String totalSpace) {
        this.totalSpace = totalSpace;
    }

    /**
     * Gets the used space on the drive in human-readable format.
     */
    public String getUsedSpace() {
        return usedSpace;
    }

    /**
     * Sets the used space on the drive in human-readable format.
     */
    public void setUsedSpace(String usedSpace) {
        this.usedSpace = usedSpace;
    }

    /**
     * Gets the free space on the drive in human-readable format.
     */
    public String getFreeSpace() {
        return freeSpace;
    }

    /**
     * Sets the free space on the drive in human-readable format.
     */
    public void setFreeSpace(String freeSpace) {
        this.freeSpace = freeSpace;
    }

    /**
     * Gets the percentage of used space.
     */
    public double getUsedPercentage() {
        return usedPercentage;
    }

    /**
     * Sets the percentage of used space.
     */
    public void setUsedPercentage(double usedPercentage) {
        this.usedPercentage = usedPercentage;
    }

    /**
     * Gets the percentage of free space.
     */
    public double getFreePercentage() {
        return freePercentage;
    }

    /**
     * Sets the percentage of free space.
     */
    public void setFreePercentage(double freePercentage) {
        this.freePercentage = freePercentage;
    }

    /**
     * Gets the total space in bytes.
     */
    public long getTotalSpaceBytes() {
        return totalSpaceBytes;
    }

    /**
     * Sets the total space in bytes.
     */
    public void setTotalSpaceBytes(long totalSpaceBytes) {
        this.totalSpaceBytes = totalSpaceBytes;
    }

    /**
     * Gets the used space in bytes.
     */
    public long getUsedSpaceBytes() {
        return usedSpaceBytes;
    }

    /**
     * Sets the used space in bytes.
     */
    public void setUsedSpaceBytes(long usedSpaceBytes) {
        this.usedSpaceBytes = usedSpaceBytes;
    }

    /**
     * Gets the free space in bytes.
     */
    public long getFreeSpaceBytes() {
        return freeSpaceBytes;
    }

    /**
     * Sets the free space in bytes.
     */
    public void setFreeSpaceBytes(long freeSpaceBytes) {
        this.freeSpaceBytes = freeSpaceBytes;
    }

    /**
     * Provides a string representation of the drive for debugging or logging.
     */
    @Override
    public String toString() {
        return String.format("%s (Total: %s, Used: %s, Free: %s, Used %%: %.2f%%)",
                driveName, totalSpace, usedSpace, freeSpace, usedPercentage);
    }
}
