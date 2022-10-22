package ca.echiu.controller;

import ca.echiu.event.PlayMediaEvent;
import ca.echiu.service.FileSystemService;
import ca.echiu.wrapper.FileWrapper;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
@FxmlView("/fxml/Navigator.fxml")
public class NavigatorController {

    @FXML
    private ListView<FileWrapper> listViewOfFiles;
    @Autowired
    private FileSystemService fileSystemService;

    private final ApplicationEventPublisher publisher;

    private String file;

    public NavigatorController(FileSystemService fileSystemService, ApplicationEventPublisher publisher) {
        this.fileSystemService = fileSystemService;
        this.publisher = publisher;
        this.listViewOfFiles = new ListView();
    }

    @FXML
    public void initialize() {
        File[] files = fileSystemService.getListOfFiles();
        for (File file : files) {
            listViewOfFiles.getItems().add(new FileWrapper(file));
        }
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
