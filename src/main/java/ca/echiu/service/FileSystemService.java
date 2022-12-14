package ca.echiu.service;

import ca.echiu.controller.AlertController;
import ca.echiu.model.ReviewFileModel;
import com.opencsv.CSVWriter;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.control.Alert;
import javafx.scene.image.WritableImage;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.io.*;
import java.nio.file.*;
import java.util.List;

@Service
public class FileSystemService {

    private File[] listOfFiles;

    @Setter
    @Getter
    private static Path reviewDirectoryPath;

    @Setter
    @Getter
    private static Path reviewTextFilePath;

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
            new AlertController(Alert.AlertType.ERROR, "copyToNewFile "+e.getMessage() + " could not be saved");
        }
    }

    public List<ReviewFileModel> parseReviewFile(Path filePath) throws FileNotFoundException {
        if(filePath==null){return null;}
        List<ReviewFileModel> reviewFileModelList = new CsvToBeanBuilder<ReviewFileModel>(new FileReader(filePath.toString())).withType(ReviewFileModel.class).build().parse();
        return reviewFileModelList;
    }

    public void writeNewFile(Path filePath) {
        try {
            FileWriter fileWriter = new FileWriter(filePath.toString());
            fileWriter.write(columnNames);
            fileWriter.close();
        } catch (IOException ioException){
            new AlertController(Alert.AlertType.ERROR, "writeNewFile: "+ioException.getMessage());
        }

    }

    public void saveReviewFile(List<ReviewFileModel> content) throws IOException, CsvRequiredFieldEmptyException, CsvDataTypeMismatchException {
        try {
            FileWriter fileWriter = new FileWriter(reviewTextFilePath.toString());
            StatefulBeanToCsv<ReviewFileModel> reviewFileModelStatefulBeanToCsv = new StatefulBeanToCsvBuilder<ReviewFileModel>(fileWriter).withSeparator(CSVWriter.DEFAULT_SEPARATOR).withApplyQuotesToAll(false).build();
            reviewFileModelStatefulBeanToCsv.write(content);
            fileWriter.close();
        } catch (FileNotFoundException fileNotFoundException){
            new AlertController(Alert.AlertType.ERROR, "saveReviewFile: "+ fileNotFoundException.getMessage() + "is not found");
        } catch (CsvRequiredFieldEmptyException | CsvDataTypeMismatchException e) {
            new AlertController(Alert.AlertType.ERROR, "saveReviewFile: "+ e.getMessage());
        }
    }

    public void createReviewFile(){
        createNewDirectory(reviewDirectoryPath);
        writeNewFile(reviewTextFilePath);

    }

    public void writeImageFile(WritableImage image, Path directoryPath, String imageFileName){
        try{
            createNewDirectory(directoryPath);
            String compliantImageFileName = imageFileName.replace("^\\.+", "").replaceAll("[\\\\/:*?\"<>|]", ",");
            File file = new File(directoryPath+"\\"+compliantImageFileName+".png");
            ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", file);
        } catch (IOException e){
            e.printStackTrace();
            new AlertController(Alert.AlertType.ERROR, "writeImageFile: "+e.getMessage());
        }
    }

    public void createNewDirectory(Path directoryPath){
        try{
            Files.createDirectory(directoryPath);
        } catch (FileAlreadyExistsException e){
            System.out.println(e.getMessage()+" already exists, skipping createNewDirectory");
        }
        catch (IOException e){
            e.printStackTrace();
            new AlertController(Alert.AlertType.ERROR, "createNewDirectory: "+e.getMessage());
        }
    }
}
