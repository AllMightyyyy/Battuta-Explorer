package org.example.fileexplorerjavafx;

import java.util.HashSet;
import java.util.Set;

public enum Applications {
    exe;

    private static final Set<String> applicationExtensions = new HashSet<>();

    static {
        for (Applications app : Applications.values()) {
            applicationExtensions.add(app.name());
        }
    }

    /**
     * Checks if the given file extension is a valid application type.
     */
    public static boolean isApplication(String extension) {
        return applicationExtensions.contains(extension);
    }
}
