package org.example.fileexplorerjavafx;

import java.util.HashSet;
import java.util.Set;

public enum Images {
    png, jpg, jpeg, gif, bmp;

    private static final Set<String> imageExtensions = new HashSet<>();

    static {
        for (Images img : Images.values()) {
            imageExtensions.add(img.name());
        }
    }

    public static boolean isImage(String extension) {
        return imageExtensions.contains(extension);
    }
}
