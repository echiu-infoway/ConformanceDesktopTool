package ca.echiu.controller;

import ca.echiu.event.PlayMediaEvent;
import ca.echiu.event.RefreshFileListEvent;
import ca.echiu.event.SaveNewFileEvent;
import ca.echiu.service.FileSystemService;
import ca.echiu.wrapper.FileWrapper;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.file.Path;

@Component
@FxmlView("fxml/FileOrganizer.fxml")
public class FileOrganizerController implements FileSystemController {

    @FXML
    private TextField fileName;
    @FXML
    private Text directoryPathText;
    @FXML
    private Text statusText;
    @FXML
    private ListView<FileWrapper> listViewOfFiles;
    @FXML
    private AnchorPane fileOrganizerPane;
    @Autowired
    private FileSystemService fileSystemService;
    @Autowired
    ApplicationEventPublisher eventPublisher;

    private Path directoryPath;

    public void copyFileToNewName(){
      eventPublisher.publishEvent(new SaveNewFileEvent(fileName.getText()));

    }

    @Override
    public void chooseDirectory() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File selectedDirectory = directoryChooser.showDialog(fileOrganizerPane.getScene().getWindow());
        directoryPath = selectedDirectory.toPath();
        directoryPathText.setText(directoryPath.toString());
        listViewOfFiles.getItems().clear();
        File[] files = fileSystemService.getListOfFiles(directoryPath);
        if (files.length == 0){statusText.setText(NO_APPLICABLE_FILES_FOUND);}
        for (File file : files) {
            listViewOfFiles.getItems().add(new FileWrapper(file));
            statusText.setText(TOTAL_NUMBER_OF_FILES + String.valueOf(files.length));
            listViewOfFiles.refresh();

    }

//    @Override
//    public void navigatorListClicked(MouseEvent mouseEvent) {
//
//        }

    }

    @EventListener
    public void refreshFileOrganizerList(RefreshFileListEvent refreshFileListEvent) {
        listViewOfFiles.getItems().clear();
        File[] files = fileSystemService.getListOfFiles(directoryPath);
        if (files.length == 0) {
            statusText.setText(NO_APPLICABLE_FILES_FOUND);
        }
        for (File file : files) {
            listViewOfFiles.getItems().add(new FileWrapper(file));
            statusText.setText(TOTAL_NUMBER_OF_FILES + String.valueOf(files.length));
            listViewOfFiles.refresh();
        }
    }
}
