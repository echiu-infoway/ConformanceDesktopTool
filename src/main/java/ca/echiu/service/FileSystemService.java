package ca.echiu.service;

import ca.echiu.controller.AlertController;
import ca.echiu.model.ReviewFileModel;
import com.opencsv.CSVWriter;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import javafx.scene.control.Alert;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.*;
import java.util.List;

@Service
public class FileSystemService {

    private File[] listOfFiles;

    private final String MP4 = ".mp4";
    private final String CSV = ".csv";
    private final String columnNames = "TIMESTAMP,COMMENTS";
    private List<ReviewFileModel> reviewFileModelList;


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

    public void copyToNewFile(File source, File destination) {

        try {
            Files.copy(source.toPath(), destination.toPath());
        } catch (FileAlreadyExistsException fileAlreadyExistsException) {
            new AlertController(Alert.AlertType.ERROR, fileAlreadyExistsException.getMessage() + " already exists");
        } catch (IOException e) {
            new AlertController(Alert.AlertType.ERROR, e.getMessage() + " could not be saved");
        }
    }

    public List<ReviewFileModel> parseReviewFile(String filePath) throws FileNotFoundException {
        List<ReviewFileModel> reviewFileModelList = new CsvToBeanBuilder<ReviewFileModel>(new FileReader(filePath)).withType(ReviewFileModel.class).build().parse();
        return reviewFileModelList;
    }

    public String getReviewFile(Path directory, String videoFileName){
        String reviewTextFileName = videoFileName.toString().toLowerCase().replace(MP4, CSV);
        String directoryString = directory.toString();
        String reviewFilePathString = directoryString + "\\" + reviewTextFileName;
        return reviewFilePathString;
    }

    public void writeNewFile(String filePath) {
        try {
            FileWriter fileWriter = new FileWriter(filePath);
            fileWriter.write(columnNames);
            fileWriter.close();
        } catch (IOException ioException){
            new AlertController(Alert.AlertType.ERROR, ioException.getMessage());
        }

    }

    public void saveReviewFile(String filePath, List<ReviewFileModel> content) throws IOException, CsvRequiredFieldEmptyException, CsvDataTypeMismatchException {
        FileWriter fileWriter = new FileWriter(filePath);
        StatefulBeanToCsv<ReviewFileModel> reviewFileModelStatefulBeanToCsv = new StatefulBeanToCsvBuilder<ReviewFileModel>(fileWriter).withSeparator(CSVWriter.DEFAULT_SEPARATOR).withApplyQuotesToAll(false).build();
        reviewFileModelStatefulBeanToCsv.write(content);
        fileWriter.close();
    }
}
