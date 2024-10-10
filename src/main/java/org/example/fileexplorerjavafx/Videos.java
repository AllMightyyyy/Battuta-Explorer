package org.example.fileexplorerjavafx;

import java.util.HashSet;
import java.util.Set;

public enum Videos {
    mp4, webm, mpg, mp2, mpeg, mpe, mpv, ogg, m4p, m4v, avi, wmv, mov, qt, flv, swf, avchd, mkv;

    private static final Set<String> videoExtensions = new HashSet<>();

    static {
        for (Videos video : Videos.values()) {
            videoExtensions.add(video.name());
        }
    }

    /**
     * Checks if the given file extension is a valid video type.
     */
    public static boolean isVideo(String extension) {
        return videoExtensions.contains(extension);
    }
}
