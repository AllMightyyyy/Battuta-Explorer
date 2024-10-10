package org.example.fileexplorerjavafx;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;

import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.awt.image.BufferedImage;
import java.io.File;

public class IconUtil {

    public static Image getFileIcon(File file) {
        try {
            FileSystemView fileSystemView = FileSystemView.getFileSystemView();
            Icon icon = fileSystemView.getSystemIcon(file);

            if (icon instanceof ImageIcon) {
                java.awt.Image awtImage = ((ImageIcon) icon).getImage();

                BufferedImage bufferedImage = new BufferedImage(
                        awtImage.getWidth(null),
                        awtImage.getHeight(null),
                        BufferedImage.TYPE_INT_ARGB
                );
                java.awt.Graphics g = bufferedImage.createGraphics();
                g.drawImage(awtImage, 0, 0, null);
                g.dispose();

                return SwingFXUtils.toFXImage(bufferedImage, null);
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
