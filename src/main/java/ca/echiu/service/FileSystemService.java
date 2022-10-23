package ca.echiu.service;

import ca.echiu.controller.AlertController;
import javafx.scene.control.Alert;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.URI;
import java.nio.file.*;
import java.util.List;

@Service
public class FileSystemService {

    private File[] listOfFiles;
    public String getFile() {
        return "You got a file!";
    }

    public File[] getListOfFiles(Path directoryPath) {

        File directory = new File(directoryPath.toUri());
        listOfFiles = directory.listFiles(new FilenameFilter() {
            public boolean accept(File dirFiles, String filename) {
                boolean endsWith = filename.toLowerCase().endsWith(".mp4");
                return endsWith;
            }
        });
        return listOfFiles;

    }

    public void saveNewFile(File source, File destination) throws IOException {
        try {
            Files.copy(source.toPath(), destination.toPath());
        }
        catch (FileAlreadyExistsException fileAlreadyExistsException){
            new AlertController (Alert.AlertType.ERROR, fileAlreadyExistsException.getMessage()+" already exists");
        }
        catch (IOException e){
            new AlertController(Alert.AlertType.ERROR, e.getMessage()+" could not be saved");
        }
    }
}
