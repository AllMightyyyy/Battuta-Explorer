package org.example.fileexplorerjavafx;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class FileExplorerController {

    @FXML
    private TreeView<String> fileTreeView;

    @FXML
    private TableView<FileItem> fileTableView;

    @FXML
    private TableColumn<FileItem, String> nameColumn;

    @FXML
    private TableColumn<FileItem, String> sizeColumn;

    @FXML
    private TableColumn<FileItem, String> typeColumn;

    @FXML
    private TableColumn<FileItem, String> lastModifiedColumn;

    @FXML
    private Label statusLabel;

    @FXML
    private ImageView refreshIcon;

    private Path currentDirectory;

    private static final List<String> TEXT_FILE_EXTENSIONS = Arrays.asList("txt", "md", "java", "xml", "json", "html");

    @FXML
    public void initialize() {
        TableColumn<FileItem, Image> iconColumn = new TableColumn<>("Icon");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        sizeColumn.setCellValueFactory(new PropertyValueFactory<>("size"));
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        iconColumn.setCellValueFactory(new PropertyValueFactory<>("icon"));
        lastModifiedColumn.setCellValueFactory(new PropertyValueFactory<>("lastModified"));

        Path rootPath = FileSystems.getDefault().getPath(System.getProperty("user.home"));
        TreeItem<String> rootItem = new TreeItem<>(rootPath.toString());
        fileTreeView.setRoot(rootItem);

        loadDirectory(rootPath, rootItem);

        fileTreeView.setOnMouseClicked(event -> handleDirectorySelection(event));

        refreshIcon.setImage(new Image(getClass().getResourceAsStream("/icons/refresh.png")));

        iconColumn.setCellFactory(column -> new TableCell<FileItem, Image>() {
            private final ImageView imageView = new ImageView();

            @Override
            protected void updateItem(Image icon, boolean empty) {
                super.updateItem(icon, empty);
                if (icon == null || empty) {
                    setGraphic(null);
                } else {
                    imageView.setImage(icon);
                    imageView.setFitHeight(20);
                    imageView.setFitWidth(20);
                    setGraphic(imageView);
                }
            }
        });

        fileTableView.getColumns().add(0, iconColumn);
    }

    @FXML
    private void refreshView() {
        TreeItem<String> selectedItem = fileTreeView.getSelectionModel().getSelectedItem();

        if (selectedItem != null) {
            Path selectedPath = Paths.get(buildFullPath(selectedItem));

            Path parentPath = selectedPath.getParent();

            if (parentPath != null && Files.isDirectory(parentPath)) {
                loadFilesInTable(parentPath);

                TreeItem<String> parentItem = selectedItem.getParent();
                if (parentItem != null) {
                    parentItem.getChildren().clear();
                    loadDirectory(parentPath, parentItem);
                    statusLabel.setText("Refreshed: " + parentPath);
                }
            } else {
                loadFilesInTable(selectedPath);
                selectedItem.getChildren().clear();
                loadDirectory(selectedPath, selectedItem);
            }
        }
    }

    private void loadDirectory(Path path, TreeItem<String> parentItem) {
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(path)) {
            for (Path entry : stream) {
                TreeItem<String> treeItem = new TreeItem<>(entry.getFileName().toString());
                parentItem.getChildren().add(treeItem);

                if (Files.isDirectory(entry)) {
                    treeItem.getChildren().add(new TreeItem<>(""));
                    treeItem.setExpanded(false);
                    treeItem.addEventHandler(TreeItem.branchExpandedEvent(), event -> {
                        if (treeItem.getChildren().size() == 1 && treeItem.getChildren().get(0).getValue().equals("")) {
                            treeItem.getChildren().remove(0);
                            loadDirectory(entry, treeItem);
                        }
                    });
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    private void handleDirectorySelection(MouseEvent event) {
        TreeItem<String> selectedItem = fileTreeView.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            Path selectedPath = Paths.get(buildFullPath(selectedItem));

            if (Files.isDirectory(selectedPath)) {
                loadFilesInTable(selectedPath);
            } else if (Files.isRegularFile(selectedPath)) {
                String fileName = selectedPath.getFileName().toString();
                String extension = getFileExtension(fileName);

                if (TEXT_FILE_EXTENSIONS.contains(extension)) {
                    boolean openFile = showOpenTextFileDialog();
                    if (openFile) {
                        openTextFileEditor(selectedPath);
                    }
                }
            }
        }
    }

    private String buildFullPath(TreeItem<String> item) {
        StringBuilder path = new StringBuilder(item.getValue());
        TreeItem<String> parent = item.getParent();
        while (parent != null) {
            path.insert(0, parent.getValue() + File.separator);
            parent = parent.getParent();
        }
        return path.toString();
    }

    private void loadFilesInTable(Path path) {
        fileTableView.getItems().clear();

        try (Stream<Path> paths = Files.list(path)) {
            paths.forEach(filePath -> {
                try {
                    File file = filePath.toFile();
                    BasicFileAttributes attrs = Files.readAttributes(filePath, BasicFileAttributes.class);
                    FileItem fileItem = new FileItem(
                            filePath.getFileName().toString(),
                            attrs.size(),
                            Files.isDirectory(filePath) ? "Directory" : "File",
                            attrs.lastModifiedTime().toMillis(),
                            IconUtil.getFileIcon(file)
                    );
                    fileTableView.getItems().add(fileItem);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            statusLabel.setText("Loaded " + fileTableView.getItems().size() + " items.");
        } catch (IOException e) {
            e.printStackTrace();
            statusLabel.setText("Unable to load directory: " + path.toString());
        }
    }

    private void openTextFileEditor(Path filePath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("TextEditor.fxml"));
            Parent root = loader.load();

            TextEditorController controller = loader.getController();
            controller.initializeEditor(filePath);

            Stage editorStage = new Stage();
            editorStage.setTitle("Text Editor - " + filePath.getFileName());
            editorStage.setScene(new Scene(root, 600, 400));
            editorStage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showError("Error", "Could not open the text editor.");
        }
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private boolean showOpenTextFileDialog() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Open Text File");
        alert.setHeaderText("Open text file?");
        alert.setContentText("Do you want to open this file in the text editor?");

        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }

    private String getFileExtension(String fileName) {
        int index = fileName.lastIndexOf('.');
        if (index > 0 && index < fileName.length() - 1) {
            return fileName.substring(index + 1).toLowerCase();
        }
        return "";
    }

    @FXML
    private void createNewTextFile() {
        TreeItem<String> selectedItem = fileTreeView.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            Path selectedPath = Paths.get(buildFullPath(selectedItem));

            if (Files.isDirectory(selectedPath)) {
                TextInputDialog dialog = new TextInputDialog("newFile.txt");
                dialog.setTitle("Create New Text File");
                dialog.setHeaderText("Enter the name for the new text file");
                dialog.setContentText("File name:");

                Optional<String> result = dialog.showAndWait();
                if (result.isPresent()) {
                    String fileName = result.get();
                    Path newFilePath = selectedPath.resolve(fileName);

                    try {
                        Files.createFile(newFilePath);
                        showInfo("Success", "File created successfully: " + newFilePath);
                        refreshView();
                    } catch (IOException e) {
                        e.printStackTrace();
                        showError("Error", "Could not create the file.");
                    }
                }
            } else {
                showError("Invalid Selection", "Please select a directory to create a new file.");
            }
        }
    }

    @FXML
    private void createNewFolder() {
        TreeItem<String> selectedItem = fileTreeView.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            Path selectedPath = Paths.get(buildFullPath(selectedItem));

            if (Files.isDirectory(selectedPath)) {
                TextInputDialog dialog = new TextInputDialog("NewFolder");
                dialog.setTitle("Create New Folder");
                dialog.setHeaderText("Enter the name for the new folder");
                dialog.setContentText("Folder name:");

                Optional<String> result = dialog.showAndWait();
                if (result.isPresent()) {
                    String folderName = result.get();
                    Path newFolderPath = selectedPath.resolve(folderName);

                    try {
                        Files.createDirectory(newFolderPath);
                        showInfo("Success", "Folder created successfully: " + newFolderPath);
                    } catch (IOException e) {
                        e.printStackTrace();
                        showError("Error", "Could not create the folder.");
                    }
                }
            } else {
                showError("Invalid Selection", "Please select a directory to create a new folder.");
            }
        }
    }

    @FXML
    private void deleteItem() {
        TreeItem<String> selectedItem = fileTreeView.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            Path selectedPath = Paths.get(buildFullPath(selectedItem));

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Delete Item");
            alert.setHeaderText("Are you sure you want to delete this item?");
            alert.setContentText("This will permanently delete the selected file or folder.");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                try {
                    Files.walk(selectedPath)
                            .sorted(Comparator.reverseOrder())
                            .map(Path::toFile)
                            .forEach(File::delete);

                    showInfo("Success", "Item deleted successfully: " + selectedPath);
                    refreshView();
                } catch (IOException e) {
                    e.printStackTrace();
                    showError("Error", "Could not delete the item.");
                }
            }
        }
    }

    @FXML
    private void openStorageAndFileManagement() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("StorageAndFileManagement.fxml"));
            Parent root = loader.load();

            Stage storageStage = new Stage();
            storageStage.setTitle("Storage and File Management");
            storageStage.setScene(new Scene(root, 1200, 850));
            storageStage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showError("Error", "Unable to load the Storage and File Management window.");
        }
    }
}
