package ca.echiu.service;

import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FilenameFilter;
import java.util.List;

@Service
public class FileSystemService {

    private File[] listOfFiles;

    public String getFile(){
        return "You got a file!";
    }

    public File[] getListOfFiles(){

        File directory = new File("C:\\Users\\email\\Downloads");
        listOfFiles = directory.listFiles(new FilenameFilter(){
            public boolean accept(File dirFiles, String filename){
                boolean endsWith = filename.toLowerCase().endsWith(".mp4");
                return endsWith;
            }

        });
        return listOfFiles;

    }
}
