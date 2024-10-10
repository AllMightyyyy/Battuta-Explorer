package org.example.fileexplorerjavafx;

/**
 * Enum representing different file types (Documents, Videos, Music, etc.)
 */
public enum FileType {
    Images,
    Documents,
    Videos,
    Apps,
    Music,
    Archives;

    /**
     * Returns a human-readable string representation of the file type.
     */
    @Override
    public String toString() {
        switch (this) {
            case Images:
                return "Images";
            case Documents:
                return "Documents";
            case Videos:
                return "Videos";
            case Apps:
                return "Applications";
            case Music:
                return "Music";
            case Archives:
                return "Archives";
            default:
                return "Unknown";
        }
    }

    /**
     * Converts a string to a corresponding FileType enum.
     * Useful if you need to parse user input or other sources.
     */
    public static FileType fromString(String type) {
        switch (type.toLowerCase()) {
            case "images":
                return Images;
            case "documents":
                return Documents;
            case "videos":
                return Videos;
            case "applications":
            case "apps":
                return Apps;
            case "music":
                return Music;
            case "archives":
                return Archives;
            default:
                throw new IllegalArgumentException("Unknown file type: " + type);
        }
    }
}
