package org.example.fileexplorerjavafx;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

public class FileExplorer extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(FileExplorer.class.getResource("FileExplorer.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Battuta Explorer ");
        stage.getIcons().add(new Image(getClass().getResourceAsStream("battutaExplorer.png")));
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}