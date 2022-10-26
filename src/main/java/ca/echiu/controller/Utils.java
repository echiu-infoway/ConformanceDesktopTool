package ca.echiu.controller;

import javafx.util.Duration;
import org.springframework.stereotype.Component;

import java.nio.file.Path;

public class Utils {

    private final String MP4 = ".mp4";
    private final String CSV = ".csv";

    public Path getCsvFileNameFromOtherFileName(String fileName){
        Path reviewTextFileName = Path.of(fileName.toLowerCase().replace(MP4, CSV));
        return reviewTextFileName;
    }


}
