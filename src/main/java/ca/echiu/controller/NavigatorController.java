package ca.echiu.controller;

import ca.echiu.event.PlayMediaEvent;
import ca.echiu.event.RefreshFileListEvent;
import ca.echiu.event.SaveNewFileEvent;
import ca.echiu.service.FileSystemService;
import ca.echiu.wrapper.FileWrapper;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
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

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
@FxmlView("/fxml/Navigator.fxml")
public class NavigatorController implements DirectorySelectable {

    @FXML
    private ListView<FileWrapper> listViewOfFiles;
    @FXML
    private Label directoryPathText;
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
    }

    @Override
    public void chooseDirectory(){
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File selectedDirectory = directoryChooser.showDialog(navigatorPane.getScene().getWindow());
        if (selectedDirectory==null){return;}
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
    public void openDirectoryOnSystem() {
        try {
            Desktop desktop = Desktop.getDesktop();
            desktop.open(directoryPath.toFile());
        } catch (IOException e){
            new AlertController(Alert.AlertType.ERROR, e.getMessage());
        } catch (NullPointerException e){
            new AlertController(Alert.AlertType.ERROR, "No folder selected yet");
        }
    }

    @EventListener
    public void writeNewFile(SaveNewFileEvent saveNewFileEvent) throws IOException {
        if(listViewOfFiles.getSelectionModel().isEmpty()){
            new AlertController(Alert.AlertType.WARNING, PLEASE_SELECT_A_FILE);
        }
        LocalDateTime now = LocalDateTime.now();
        String todayDate = now.format(DateTimeFormatter.ofPattern("MMM dd, yyyy"));
        File sourceFile = listViewOfFiles.getSelectionModel().getSelectedItem().getFile();
        File destFile = new File(FileOrganizerController.getTargetDirectory()+ "\\" + saveNewFileEvent.getNewFileName() + " - " + todayDate + "." + FilenameUtils.getExtension(sourceFile.getName()));
        fileSystemService.copyToNewFile(sourceFile, destFile);
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
