package ca.echiu.controller;

import ca.echiu.event.PlayMediaEvent;
import ca.echiu.model.ReviewFileModel;
import ca.echiu.service.FileSystemService;
import ca.echiu.wrapper.FileWrapper;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

@Component
@FxmlView("fxml/Review.fxml")
public class ReviewController implements FileSystemController {

    @FXML
    private TableView reviewTableView;
    @FXML
    private AnchorPane reviewPane;
    @FXML
    private ComboBox<FileWrapper> scenarioListComboBox;
    @FXML
    private TextArea reviewCommentsTextArea;

    @Autowired
    private FileSystemService fileSystemService;
    @Autowired
    ApplicationEventPublisher eventPublisher;

    private Path directoryPath;
    @FXML
    private Text directoryPathText;

    private String videoReviewFile;
    private String reviewTextFile;
    private List<ReviewFileModel> reviewFileModelList;

    private final String NO_FILES_FOUND = "No files found in folder";

    @FXML
    public void initialize() throws FileNotFoundException {
        TableColumn<ReviewFileModel, String> timestampColumn = new TableColumn<>("Timestamp");
        timestampColumn.setCellValueFactory(new PropertyValueFactory<>("timestamp"));
        TableColumn<ReviewFileModel, String> commentsColumn = new TableColumn<>("Comments");
        commentsColumn.setCellValueFactory(new PropertyValueFactory<>("comments"));
        reviewTableView.getColumns().add(timestampColumn);
        reviewTableView.getColumns().add(commentsColumn);


    }

    @Override
    public void chooseDirectory() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File selectedDirectory = directoryChooser.showDialog(reviewPane.getScene().getWindow());
        if (selectedDirectory == null) {
            return;
        }
        directoryPath = selectedDirectory.toPath();
        directoryPathText.setText(directoryPath.toString());
        reviewTableView.getItems().clear();
        scenarioListComboBox.getItems().clear();
        File[] files = fileSystemService.getListOfFiles(directoryPath);
        if (files.length == 0) {
            new AlertController(Alert.AlertType.WARNING, NO_FILES_FOUND);
        }
        for (File file : files) {
            scenarioListComboBox.getItems().add(new FileWrapper(file));
        }
    }

    public void setReviewForScenario() {
        setReviewTextFilePath();
        loadCsvObjectsInTable(reviewTextFile);
        eventPublisher.publishEvent(new PlayMediaEvent(scenarioListComboBox.getSelectionModel().getSelectedItem().getFile()));

    }

    private void loadCsvObjectsInTable(String filePath) {
        try {
            reviewFileModelList = fileSystemService.parseReviewFile(filePath);
            reviewFileModelList.forEach(e -> reviewTableView.getItems().add(e));
        } catch (FileNotFoundException fileNotFoundException) {
            fileSystemService.writeNewFile(filePath);
        }
    }
    private void setReviewTextFilePath(){
        videoReviewFile = scenarioListComboBox.getSelectionModel().getSelectedItem().toString();
        reviewTextFile = fileSystemService.getReviewFile(directoryPath, videoReviewFile);

    }

    public void addReviewComments(ActionEvent actionEvent) {
        try {
            reviewFileModelList = fileSystemService.parseReviewFile(reviewTextFile);
            reviewFileModelList.add(new ReviewFileModel("5:33", reviewCommentsTextArea.getText()));
            System.out.println("addReviewComments:"+reviewFileModelList);
            fileSystemService.saveReviewFile(reviewTextFile, reviewFileModelList);
        } catch (FileNotFoundException fileNotFoundException){
            new AlertController(Alert.AlertType.ERROR, fileNotFoundException.getMessage()+" is not found");

        } catch (CsvRequiredFieldEmptyException e) {
            throw new RuntimeException(e);
        } catch (CsvDataTypeMismatchException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
