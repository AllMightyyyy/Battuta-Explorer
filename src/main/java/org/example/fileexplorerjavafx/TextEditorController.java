package org.example.fileexplorerjavafx;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class TextEditorController {

    @FXML
    private TextArea textArea;

    @FXML
    private Button saveButton;

    private Path filePath;

    public void initializeEditor(Path filePath) {
        this.filePath = filePath;

        try {
            String content = Files.readString(filePath);
            textArea.setText(content);
        } catch (IOException e) {
            e.printStackTrace();
            showError("Error", "Unable to load the file.");
        }
    }

    @FXML
    private void handleSave() {
        try {
            Files.writeString(filePath, textArea.getText());
            showInfo("File Saved", "The file was saved successfully.");
        } catch (IOException e) {
            e.printStackTrace();
            showError("Save Error", "An error occurred while saving the file.");
        }
    }

    private void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
