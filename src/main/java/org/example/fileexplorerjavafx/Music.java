package org.example.fileexplorerjavafx;

import java.util.HashSet;
import java.util.Set;

public enum Music {
    mp3, m4a, flac, wav, wma, aac;

    private static final Set<String> musicExtensions = new HashSet<>();

    static {
        for (Music music : Music.values()) {
            musicExtensions.add(music.name());
        }
    }

    /**
     * Checks if the given file extension is a valid music type.
     */
    public static boolean isMusic(String extension) {
        return musicExtensions.contains(extension);
    }
}
