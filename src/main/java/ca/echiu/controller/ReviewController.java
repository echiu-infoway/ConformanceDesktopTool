package ca.echiu.controller;

import ca.echiu.model.ReviewFileModel;
import ca.echiu.service.FileSystemService;
import ca.echiu.wrapper.FileWrapper;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.swing.*;
import java.io.File;
import java.io.FileNotFoundException;
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
    private ComboBox scenarioListComboBox;
    @FXML
    private Text statusText;

    @Autowired
    private FileSystemService fileSystemService;

    private List<ReviewFileModel> reviewFileModelList;
    private Path directoryPath;
    @FXML
    private Text directoryPathText;

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
        directoryPath = selectedDirectory.toPath();
        directoryPathText.setText(directoryPath.toString());
        reviewTableView.getItems().clear();
        File[] files = fileSystemService.getListOfFiles(directoryPath);
        if (files.length == 0) {
            statusText.setText(NO_APPLICABLE_FILES_FOUND);
        }
        for (File file : files) {
            scenarioListComboBox.getItems().add(new FileWrapper(file));
            statusText.setText(TOTAL_NUMBER_OF_FILES + files.length);
        }
    }
    public void loadCsvObjectsInTable(String filePath) throws FileNotFoundException {
        try {
            reviewFileModelList = fileSystemService.getReviewFile(filePath);
            reviewFileModelList.forEach(e -> reviewTableView.getItems().add(e));
        }
        catch (FileNotFoundException fileNotFoundException){
            new AlertController(Alert.AlertType.ERROR, fileNotFoundException.getMessage()+" could not load file");
        }
    }
}
