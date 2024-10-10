package org.example.fileexplorerjavafx;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.chart.PieChart;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;
import java.util.*;
import java.util.function.Consumer;

public class DashboardController implements Initializable {

    @FXML
    private Button btnFiles, btnStorage, btnStopSearch;

    @FXML
    private ComboBox<Drive> drpDrives;

    @FXML
    private TabPane mainTabPane;

    @FXML
    private PieChart spaceChart;

    @FXML
    private Label lblUsed, lblFree, lblFileType, lblLoad;

    @FXML
    private HBox hboxStatus, hboxLoad;

    @FXML
    private TextField inpSearch;

    @FXML
    private TableView<FileEnhanced> listDataTableView;

    @FXML
    private TableColumn<FileEnhanced, String> colFileName;

    @FXML
    private TableColumn<FileEnhanced, String> colFileSize;

    @FXML
    private TableColumn<FileEnhanced, String> colFileType;

    @FXML
    private ImageView lnkApps, lnkDocs, lnkImages, lnkMusic, lnkVideos, lnkZip, btnMax, btnMin, btnClose;

    @FXML
    private Pagination pagination;

    private DashboardUtility utility;
    private Set<FileEnhanced> origFileEnhanceds;
    private FileType selectedFileType;
    private volatile boolean searching = true;

    private static final int ROWS_PER_PAGE = 100;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        utility = new DashboardUtility();
        origFileEnhanceds = new LinkedHashSet<>();

        colFileName.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getFileName()));
        colFileSize.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getFileSize()));
        colFileType.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getFileType().toString()));

        loadDriveData();
        initializeTabs();

        pagination.setPageFactory(this::createPage);
    }


    private Node createPage(int pageIndex) {
        List<FileEnhanced> sortedFiles = new ArrayList<>(origFileEnhanceds);
        sortedFiles.sort(Comparator.comparing(FileEnhanced::getFileName));

        int fromIndex = pageIndex * ROWS_PER_PAGE;
        int toIndex = Math.min(fromIndex + ROWS_PER_PAGE, sortedFiles.size());

        if (fromIndex >= sortedFiles.size()) {
            listDataTableView.setItems(FXCollections.observableArrayList());
        } else {
            ObservableList<FileEnhanced> currentPageItems = FXCollections.observableArrayList(
                    sortedFiles.subList(fromIndex, toIndex)
            );
            listDataTableView.setItems(currentPageItems);
        }

        return new AnchorPane();
    }

    private void loadDriveData() {
        ObservableList<Drive> drives = FXCollections.observableArrayList(DashboardUtility.getAllDrives());
        drpDrives.setItems(drives);
        hboxStatus.setVisible(false);
        hboxLoad.setVisible(false);
    }

    private void initializeTabs() {
        mainTabPane.getSelectionModel().selectedItemProperty().addListener((ObservableValue<? extends Tab> ov, Tab t, Tab t1) -> {
            if ("Files".equalsIgnoreCase(t1.getText())) {
                switchToFilesTab();
            } else if ("Storage".equalsIgnoreCase(t1.getText())) {
                switchToStorageTab();
            }
        });
    }

    private void switchToFilesTab() {
        btnStorage.setStyle("-fx-background-color:#F1F1F3;-fx-background-radius:10;-fx-text-fill:#8a8686;");
        btnFiles.setStyle("-fx-background-color:#FC7955;-fx-background-radius:10;-fx-text-fill:#FFFFFF;");
    }

    private void switchToStorageTab() {
        btnFiles.setStyle("-fx-background-color:#F1F1F3;-fx-background-radius:10;-fx-text-fill:#8a8686;");
        btnStorage.setStyle("-fx-background-color:#FC7955;-fx-background-radius:10;-fx-text-fill:#FFFFFF;");
    }

    private void updatePagination(ObservableList<FileEnhanced> files) {
        int pageCount = (int) Math.ceil((double) files.size() / ROWS_PER_PAGE);
        pagination.setPageCount(pageCount);

        pagination.setPageFactory(pageIndex -> {
            int fromIndex = pageIndex * ROWS_PER_PAGE;
            int toIndex = Math.min(fromIndex + ROWS_PER_PAGE, files.size());

            ObservableList<FileEnhanced> currentPageItems = FXCollections.observableArrayList(files.subList(fromIndex, toIndex));
            listDataTableView.setItems(currentPageItems);

            return new AnchorPane();
        });
    }

    @FXML
    public void loadFiles(MouseEvent event) {
        if (drpDrives.getSelectionModel().getSelectedItem() != null) {
            ImageView imageView = (ImageView) event.getSource();
            handleFileTypeSelection(imageView);
        } else {
            showWarning("Please select a drive.");
        }
    }

    private void loadFilesByType(FileType fileType) {
        this.selectedFileType = fileType;
        Drive selectedDrive = drpDrives.getSelectionModel().getSelectedItem();

        if (selectedDrive == null) {
            showWarning("Please select a drive.");
            return;
        }

        System.out.println("Starting file search for type: " + fileType + " on drive: " + selectedDrive.getDriveName());

        utility.reset();
        origFileEnhanceds.clear();
        hboxLoad.setVisible(true);
        lblLoad.setText("Fetching " + fileType + " files...");
        searching = true;

        Task<Void> fileLoadingTask = new Task<>() {
            @Override
            protected Void call() {
                utility.searchFilesInBatches(selectedDrive.getFile(), fileType, batch -> {
                    if (!searching) {
                        return;
                    }

                    Platform.runLater(() -> {
                        boolean isNewData = origFileEnhanceds.addAll(batch);
                        if (isNewData) {
                            int pageCount = (int) Math.ceil((double) origFileEnhanceds.size() / ROWS_PER_PAGE);
                            pagination.setPageCount(pageCount);

                        }
                        System.out.println("Displayed a batch of " + batch.size() + " files.");
                    });
                });
                return null;
            }
        };

        fileLoadingTask.setOnSucceeded(event -> {
            hboxLoad.setVisible(false);
            lblFileType.setText(fileType + " Files");
            int pageCount = (int) Math.ceil((double) origFileEnhanceds.size() / ROWS_PER_PAGE);
            pagination.setPageCount(pageCount);
            System.out.println("File search completed.");
        });

        fileLoadingTask.setOnFailed(event -> {
            hboxLoad.setVisible(false);
            showWarning("Error fetching files. Please try again.");
        });

        new Thread(fileLoadingTask).start();
    }

    @FXML
    public void stopSearch() {
        searching = false;
        hboxLoad.setVisible(false);
        lblLoad.setText("Search stopped. Displaying current files.");
        System.out.println("Search stopped by the user.");
    }

    @FXML
    public void searchFiles() {
        ObservableList<FileEnhanced> observableList = FXCollections.observableArrayList(origFileEnhanceds);
        FilteredList<FileEnhanced> filteredData = new FilteredList<>(observableList, s -> true);
        inpSearch.textProperty().addListener((obs, oldVal, newVal) -> {
            filteredData.setPredicate(file -> {
                if (newVal == null || newVal.isEmpty()) {
                    return true;
                }
                String lowerCaseFilter = newVal.toLowerCase();
                return file.getFile().getName().toLowerCase().contains(lowerCaseFilter);
            });
        });
        SortedList<FileEnhanced> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(listDataTableView.comparatorProperty());
        listDataTableView.setItems(sortedData);
    }

    private void showWarning(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Warning");
        alert.setContentText(message);
        alert.show();
    }

    @FXML
    public void loadChartData(ActionEvent event) {
        Drive selectedDrive = drpDrives.getSelectionModel().getSelectedItem();

        if (selectedDrive != null) {
            System.out.println("Selected Drive: " + selectedDrive.getDriveName());
            System.out.println("Total Space (bytes): " + selectedDrive.getTotalSpaceBytes());
            System.out.println("Used Space (bytes): " + selectedDrive.getUsedSpaceBytes());
            System.out.println("Free Space (bytes): " + selectedDrive.getFreeSpaceBytes());

            long usedSpaceBytes = selectedDrive.getUsedSpaceBytes();
            long freeSpaceBytes = selectedDrive.getFreeSpaceBytes();

            if (usedSpaceBytes == 0 && freeSpaceBytes == 0) {
                System.out.println("Warning: Both used and free space are 0. PieChart will not show anything.");
            } else {
                ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList(
                        new PieChart.Data("Used Space", usedSpaceBytes),
                        new PieChart.Data("Free Space", freeSpaceBytes)
                );
                spaceChart.setData(pieChartData);

                lblUsed.setText(selectedDrive.getUsedSpace());
                lblFree.setText(selectedDrive.getFreeSpace());

                hboxStatus.setVisible(true);
                System.out.println("PieChart and labels updated successfully.");
            }
        } else {
            System.out.println("Error: No drive selected.");
            showWarning("Please select a drive.");
        }
    }

    @FXML
    public void loadData(MouseEvent event) {
        if (drpDrives.getSelectionModel().getSelectedItem() != null) {
            ImageView imageView = (ImageView) event.getSource();
            handleFileTypeSelection(imageView);
        } else {
            showWarning("Please select a drive.");
        }
    }

    private void handleFileTypeSelection(ImageView imageView) {
        if (imageView.equals(lnkDocs)) {
            loadFilesByType(FileType.Documents);
        } else if (imageView.equals(lnkImages)) {
            loadFilesByType(FileType.Images);
        } else if (imageView.equals(lnkVideos)) {
            loadFilesByType(FileType.Videos);
        } else if (imageView.equals(lnkZip)) {
            loadFilesByType(FileType.Archives);
        } else if (imageView.equals(lnkApps)) {
            loadFilesByType(FileType.Apps);
        } else if (imageView.equals(lnkMusic)) {
            loadFilesByType(FileType.Music);
        }
    }

    @FXML
    public void minMaxClose(MouseEvent event) {
        ImageView buttonClicked = (ImageView) event.getSource();
        Stage stage = (Stage) buttonClicked.getScene().getWindow();

        if (buttonClicked.equals(btnMin)) {
            stage.setIconified(true);
        } else if (buttonClicked.equals(btnMax)) {
            stage.setMaximized(!stage.isMaximized());
        } else if (buttonClicked.equals(btnClose)) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Do you really want to exit?", ButtonType.YES, ButtonType.NO);
            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.YES) {
                    Platform.exit();
                }
            });
        }
    }

    @FXML
    public void changeTabs(ActionEvent event) {
        Button clickedButton = (Button) event.getSource();
        if (clickedButton.equals(btnStorage)) {
            mainTabPane.getSelectionModel().select(0);
            btnStorage.setStyle("-fx-background-color:#FC7955;-fx-background-radius:10;-fx-text-fill:#FFFFFF;");
            btnFiles.setStyle("-fx-background-color:#F1F1F3;-fx-background-radius:10;-fx-text-fill:#8a8686;");
        } else if (clickedButton.equals(btnFiles)) {
            mainTabPane.getSelectionModel().select(1);
            btnFiles.setStyle("-fx-background-color:#FC7955;-fx-background-radius:10;-fx-text-fill:#FFFFFF;");
            btnStorage.setStyle("-fx-background-color:#F1F1F3;-fx-background-radius:10;-fx-text-fill:#8a8686;");
        }
    }
}
