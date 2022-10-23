package ca.echiu.service;

import ca.echiu.controller.AlertController;
import ca.echiu.model.ReviewFileModel;
import com.opencsv.bean.CsvToBeanBuilder;
import javafx.scene.control.Alert;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.URI;
import java.nio.file.*;
import java.util.List;

@Service
public class FileSystemService {

    private File[] listOfFiles;


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
        } catch (FileAlreadyExistsException fileAlreadyExistsException) {
            new AlertController(Alert.AlertType.ERROR, fileAlreadyExistsException.getMessage() + " already exists");
        } catch (IOException e) {
            new AlertController(Alert.AlertType.ERROR, e.getMessage() + " could not be saved");
        }
    }

    public List<ReviewFileModel> getReviewFile(String file) throws FileNotFoundException {
        List<ReviewFileModel> reviewFileModelList = new CsvToBeanBuilder<ReviewFileModel>(new FileReader(file)).withType(ReviewFileModel.class).build().parse();
        return reviewFileModelList;
    }
}
