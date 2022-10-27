package ca.echiu.controller;

import ca.echiu.event.PlayMediaEvent;
import ca.echiu.event.SaveSnapshotEvent;
import ca.echiu.model.ReviewFileModel;
import ca.echiu.service.FileSystemService;
import ca.echiu.wrapper.FileWrapper;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.util.Duration;
import net.rgielen.fxweaver.core.FxmlView;
import org.apache.commons.io.FilenameUtils;
import org.controlsfx.control.SearchableComboBox;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalTime;
import java.util.List;
import java.util.function.UnaryOperator;

@Component
@FxmlView("fxml/Review.fxml")
public class ReviewController implements DirectorySelectable {

    @FXML
    private TableView reviewTableView;
    @FXML
    private AnchorPane reviewPane;
    @FXML
    private SearchableComboBox<FileWrapper> scenarioListComboBox;
    @FXML
    private TextArea reviewCommentsTextArea;

    @Autowired
    ApplicationEventPublisher eventPublisher;

    @Autowired
    FileSystemService fileSystemService;

    private Utils utils = new Utils();

    @FXML
    private Text directoryPathText;
    private Path videoDirectoryPath;
    private String videoReviewFileName;
    private Path reviewTextPath;
    private List<ReviewFileModel> reviewFileModelList;
    private final String TIMESTAMP = "Timestamp";
    private final String COMMENTS = "Comments";

    private final String NO_FILES_FOUND = "No files found in folder";
    private final TableColumn.SortType sortAscending = TableColumn.SortType.ASCENDING;
    private TableColumn<ReviewFileModel, String> timestampColumn;

    public ReviewController() {
    }

    @FXML
    public void initialize(){
        setUpReviewTable();
        setUpCommentsTextArea();

    }

    private void setUpCommentsTextArea(){
        UnaryOperator<TextFormatter.Change> textFilter = change -> {
            String newText = change.getControlNewText();
            if (newText.matches("^[^.\\\\/:*?\"<>|]?[^\\\\/:*?\"<>|]*")){
                return change;
            }
            return null;
        };
        reviewCommentsTextArea.setTextFormatter(new TextFormatter<String>(textFilter));

    }

    private void setUpReviewTable(){
        timestampColumn = new TableColumn<>(TIMESTAMP);
        timestampColumn.setCellValueFactory(new PropertyValueFactory<>("timestamp"));
        TableColumn<ReviewFileModel, String> commentsColumn = new TableColumn<>(COMMENTS);
        commentsColumn.setCellValueFactory(new PropertyValueFactory<>("comments"));
        commentsColumn.setCellFactory(tc -> {
            TableCell<ReviewFileModel, String> cell = new TableCell<>();
            Text text = new Text();
            cell.setGraphic(text);
            cell.setPrefHeight(Control.USE_COMPUTED_SIZE);
            text.wrappingWidthProperty().bind(commentsColumn.widthProperty());
            text.textProperty().bind(cell.itemProperty());
            return cell;
        });
        reviewTableView.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (event.getButton().equals(MouseButton.PRIMARY) && event.getClickCount() == 2) {
                    loadReviewComment((ReviewFileModel) reviewTableView.getSelectionModel().getSelectedItem());
                }
            }
        });
        reviewTableView.getColumns().add(timestampColumn);
        reviewTableView.getColumns().add(commentsColumn);
        timestampColumn.setMaxWidth(1000);
    }

    @Override
    public void chooseDirectory() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File selectedDirectory = directoryChooser.showDialog(reviewPane.getScene().getWindow());
        if (selectedDirectory == null) {
            return;
        }
        videoDirectoryPath = selectedDirectory.toPath();
        directoryPathText.setText(videoDirectoryPath.toString());
        reviewTableView.getItems().clear();
        scenarioListComboBox.getItems().clear();
        File[] files = fileSystemService.getListOfFiles(videoDirectoryPath);
        if (files.length == 0) {
            new AlertController(Alert.AlertType.WARNING, NO_FILES_FOUND);
        }
        for (File file : files) {
            scenarioListComboBox.getItems().add(new FileWrapper(file));
        }
    }

    public void setReviewForScenario() {
        reviewTableView.getItems().clear();
        setReviewDirectoryAndFile();
        loadCsvObjectsInTable(FileSystemService.getReviewTextFilePath());
        reviewTableView.getSortOrder().add(timestampColumn);
        timestampColumn.setSortType(sortAscending);
        timestampColumn.setSortable(true);
        reviewTableView.sort();
        eventPublisher.publishEvent(new PlayMediaEvent(scenarioListComboBox.getSelectionModel().getSelectedItem().getFile()));

    }

    private void loadCsvObjectsInTable(Path filePath) {
        try {
            reviewFileModelList = fileSystemService.parseReviewFile(filePath);
            reviewFileModelList.forEach(e -> reviewTableView.getItems().add(e));
        } catch (FileNotFoundException fileNotFoundException) {
            reviewTableView.setPlaceholder(new Label("Review not yet started or not found"));
        }
    }

    private void setReviewDirectoryAndFile() {
        if (scenarioListComboBox.getSelectionModel().getSelectedItem() == null) {
            return;
        }
        videoReviewFileName = scenarioListComboBox.getSelectionModel().getSelectedItem().toString();
        FileSystemService.setReviewDirectoryPath(Path.of(videoDirectoryPath.toString() + "\\" + FilenameUtils.removeExtension(videoReviewFileName)));
        FileSystemService.setReviewTextFilePath(Path.of(FileSystemService.getReviewDirectoryPath().toString() + "\\" + utils.getCsvFileNameFromOtherFileName(videoReviewFileName)));

    }

    public void saveReviewComments(ActionEvent actionEvent) {
        do {
            try {
                reviewFileModelList = fileSystemService.parseReviewFile(FileSystemService.getReviewTextFilePath());
                reviewFileModelList.add(new ReviewFileModel(MediaPlayerController.getCurrentPlayTime(), reviewCommentsTextArea.getText()));
                fileSystemService.saveReviewFile(reviewFileModelList);
                reviewTableView.getItems().clear();
                loadCsvObjectsInTable(FileSystemService.getReviewTextFilePath());
                eventPublisher.publishEvent(new SaveSnapshotEvent(reviewCommentsTextArea.getText()));
            } catch (FileNotFoundException fileNotFoundException) {
                fileSystemService.createReviewFile();
                continue;
            } catch (CsvRequiredFieldEmptyException | CsvDataTypeMismatchException | IOException e) {
                new AlertController(Alert.AlertType.ERROR, "saveReviewComments: " + e.getMessage());
            }
            break;
        } while (true);

    }

    private void loadReviewComment(ReviewFileModel reviewFileModel) {
        String timestamp = reviewFileModel.getTimestamp();

        int countOfColons = timestamp.length() - timestamp.replace(":", "").length();
        if (countOfColons == 1){
            java.time.Duration durationTime = java.time.Duration.between(LocalTime.MIN, LocalTime.parse("00:"+reviewFileModel.getTimestamp()));
            Duration seekTime = new Duration(durationTime.toMillis());
            mediaPlayerSeekToSeekTime(seekTime);


        }
        if (countOfColons == 2){
            java.time.Duration durationTime = java.time.Duration.between(LocalTime.MIN, LocalTime.parse(reviewFileModel.getTimestamp()));
            Duration seekTime = new Duration(durationTime.toMillis());
            mediaPlayerSeekToSeekTime(seekTime);
        }

    }

    private void mediaPlayerSeekToSeekTime(Duration seekTime){
        MediaPlayerController.seekToPlayTime(seekTime);
    }
}
