package ca.echiu.controller;

import ca.echiu.service.FileSystemService;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
@FxmlView("/fxml/Navigator.fxml")
public class NavigatorController {

    @FXML
    private ListView listViewOfFiles;
    @Autowired
    private FileSystemService fileSystemService;

    private String file;

    public NavigatorController(FileSystemService fileSystemService) {
        this.fileSystemService = fileSystemService;
        this.listViewOfFiles = new ListView();
    }

    @FXML
    public void initialize() {
        File[] files = fileSystemService.getListOfFiles();
        for (File file : files) {
            listViewOfFiles.getItems().add(file);
        }
    }


}
