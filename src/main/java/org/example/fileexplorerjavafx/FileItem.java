package org.example.fileexplorerjavafx;

import javafx.scene.image.Image;

import java.text.DecimalFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class FileItem {
    private String name;
    private String size;
    private String type;
    private String lastModified;
    private Image icon;

    private static final DecimalFormat SIZE_FORMAT = new DecimalFormat("#,##0.#");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

    public FileItem(String name, long sizeInBytes, String type, long lastModifiedTime, Image icon) {
        this.name = name;
        this.size = formatSize(sizeInBytes);
        this.type = type;
        this.lastModified = formatLastModifiedTime(lastModifiedTime);
        this.icon = icon;
    }

    private String formatSize(long sizeInBytes) {
        if (sizeInBytes < 1024) {
            return sizeInBytes + " B";
        } else if (sizeInBytes < 1024 * 1024) {
            return SIZE_FORMAT.format(sizeInBytes / 1024.0) + " KB";
        } else if (sizeInBytes < 1024 * 1024 * 1024) {
            return SIZE_FORMAT.format(sizeInBytes / (1024.0 * 1024)) + " MB";
        } else {
            return SIZE_FORMAT.format(sizeInBytes / (1024.0 * 1024 * 1024)) + " GB";
        }
    }

    private String formatLastModifiedTime(long lastModifiedTime) {
        LocalDateTime dateTime = Instant.ofEpochMilli(lastModifiedTime)
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
        return TIME_FORMATTER.format(dateTime);
    }

    public String getName() {
        return name;
    }

    public String getSize() {
        return size;
    }

    public String getType() {
        return type;
    }

    public String getLastModified() {
        return lastModified;
    }

    public Image getIcon() {
        return icon;
    }
}
