package ca.echiu.controller;

import ca.echiu.event.PlayMediaEvent;
import ca.echiu.event.RefreshFileListEvent;
import ca.echiu.event.SaveNewFileEvent;
import ca.echiu.service.FileSystemService;
import ca.echiu.wrapper.FileWrapper;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.MultipleSelectionModel;
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
public class NavigatorController implements FileSystemController {

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
    private final String PLEASE_SELECT_A_FILE = "Please select a file";


    private Path directoryPath;

    public NavigatorController(FileSystemService fileSystemService, ApplicationEventPublisher publisher) {
        this.fileSystemService = fileSystemService;
        this.publisher = publisher;
        this.listViewOfFiles = new ListView();
    }

    @Override
    public void chooseDirectory(){
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File selectedDirectory = directoryChooser.showDialog(navigatorPane.getScene().getWindow());
        directoryPath = selectedDirectory.toPath();
        directoryPathText.setText(directoryPath.toString());
        listViewOfFiles.getItems().clear();
        File[] files = fileSystemService.getListOfFiles(directoryPath);
        if (files.length == 0){statusText.setText(NO_APPLICABLE_FILES_FOUND);}
        for (File file : files) {
            listViewOfFiles.getItems().add(new FileWrapper(file));
            statusText.setText(TOTAL_NUMBER_OF_FILES + files.length);
            listViewOfFiles.refresh();
        }
    }

    @EventListener
    public void writeNewFile(SaveNewFileEvent saveNewFileEvent) throws IOException {
        if(listViewOfFiles.getSelectionModel().isEmpty()){
            new AlertController(Alert.AlertType.WARNING, PLEASE_SELECT_A_FILE);
        }
        File sourceFile = listViewOfFiles.getSelectionModel().getSelectedItem().getFile();
        File destFile = new File("C:\\Users\\email\\Downloads\\NewFolder\\" + saveNewFileEvent.getNewFileName() + "." + FilenameUtils.getExtension(sourceFile.getName()));
        fileSystemService.saveNewFile(sourceFile, destFile);
        publisher.publishEvent(new RefreshFileListEvent());

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
        if(listViewOfFiles.getSelectionModel().getSelectedItem() == null){
            return;
        }

    }



}
