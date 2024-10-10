package org.example.fileexplorerjavafx;

import java.util.HashSet;
import java.util.Set;

public enum Documents {
    pdf, docx, doc, xlsx, csv, xls, txt, odt, ods, ppt, pptx;

    private static final Set<String> documentExtensions = new HashSet<>();

    static {
        for (Documents doc : Documents.values()) {
            documentExtensions.add(doc.name());
        }
    }

    /**
     * Checks if the given file extension is a valid document type.
     */
    public static boolean isDocument(String extension) {
        return documentExtensions.contains(extension);
    }
}
