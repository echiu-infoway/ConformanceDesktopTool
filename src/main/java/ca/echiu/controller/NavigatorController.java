package ca.echiu.controller;

import ca.echiu.event.PlayMediaEvent;
import ca.echiu.event.SaveNewFileEvent;
import ca.echiu.service.FileSystemService;
import ca.echiu.wrapper.FileWrapper;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import net.rgielen.fxweaver.core.FxmlView;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.regex.Pattern;

@Component
@FxmlView("/fxml/Navigator.fxml")
public class NavigatorController {

    @FXML
    private ListView<FileWrapper> listViewOfFiles;
    @FXML
    private Text directoryPathText;
    @Autowired
    private FileSystemService fileSystemService;
    @FXML
    private AnchorPane navigatorPane;
    @FXML
    private Text statusText;

    private final ApplicationEventPublisher publisher;

    private String file;
    private final String NO_APPLICABLE_FILES_FOUND = "No applicable files found";
    private final String TOTAL_NUMBER_OF_FILES = "TOTAL NUMBER OF FILES: ";

    private Path directoryPath;

    public NavigatorController(FileSystemService fileSystemService, ApplicationEventPublisher publisher) {
        this.fileSystemService = fileSystemService;
        this.publisher = publisher;
        this.listViewOfFiles = new ListView();
    }

    public void chooseDirectory(){
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File selectedDirectory = directoryChooser.showDialog(navigatorPane.getScene().getWindow());
        directoryPath = selectedDirectory.toPath();
        directoryPathText.setText(directoryPath.toString());
        File[] files = fileSystemService.getListOfFiles(directoryPath);
        if (files.length == 0){statusText.setText(NO_APPLICABLE_FILES_FOUND);}
        for (File file : files) {
            listViewOfFiles.getItems().add(new FileWrapper(file));
            statusText.setText(TOTAL_NUMBER_OF_FILES + String.valueOf(files.length));
            listViewOfFiles.refresh();
        }
    }

    @EventListener
    public void writeNewFile(SaveNewFileEvent saveNewFileEvent) throws IOException {

        File sourceFile = listViewOfFiles.getSelectionModel().getSelectedItem().getFile();
        File destFile = new File("C:\\Users\\email\\Downloads\\NewFolder\\" + saveNewFileEvent.getNewFileName() + "." + FilenameUtils.getExtension(sourceFile.getName()));
        fileSystemService.saveNewFile(sourceFile, destFile);

    }

    @FXML
    public void initialize() {
        statusText.setText(NO_APPLICABLE_FILES_FOUND);
    }

    public void navigatorListClicked(MouseEvent mouseEvent){
        if(mouseEvent.getClickCount()==2) {
            FileWrapper selectedFile = listViewOfFiles.getSelectionModel().getSelectedItem();
            if(selectedFile == null){
                new AlertController(Alert.AlertType.ERROR, "Please select a file");
                return;
            }
            System.out.println(listViewOfFiles.getSelectionModel().getSelectedItem());
            publisher.publishEvent(new PlayMediaEvent(selectedFile.getFile()));
        }

    }



}
