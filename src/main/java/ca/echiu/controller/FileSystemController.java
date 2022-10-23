package ca.echiu.controller;

import javafx.scene.input.MouseEvent;
import org.springframework.context.ApplicationEventPublisher;

import java.io.File;

public interface FileSystemController {

    public final String NO_APPLICABLE_FILES_FOUND = "No applicable files found";
    public final String TOTAL_NUMBER_OF_FILES = "TOTAL NUMBER OF FILES: ";

    public void chooseDirectory();
//    public void navigatorListClicked(MouseEvent mouseEvent);

}
