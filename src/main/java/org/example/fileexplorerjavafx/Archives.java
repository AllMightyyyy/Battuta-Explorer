package org.example.fileexplorerjavafx;

import java.util.HashSet;
import java.util.Set;

public enum Archives {
    zip, rar;

    private static final Set<String> archiveExtensions = new HashSet<>();

    static {
        for (Archives archive : Archives.values()) {
            archiveExtensions.add(archive.name());
        }
    }

    /**
     * Checks if the given file extension is a valid archive type.
     */
    public static boolean isArchive(String extension) {
        return archiveExtensions.contains(extension);
    }
}
