package ca.echiu.controller;

import ca.echiu.event.SaveNewFileEvent;
import ca.echiu.service.FileSystemService;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@FxmlView("fxml/FileOrganizer.fxml")
public class FileOrganizerController {

    @FXML
    private TextField fileName;

    @Autowired
    private FileSystemService fileSystemService;
    @Autowired
    ApplicationEventPublisher eventPublisher;



    public void copyFileToNewName(){
      eventPublisher.publishEvent(new SaveNewFileEvent(fileName.getText()));

    }


}
