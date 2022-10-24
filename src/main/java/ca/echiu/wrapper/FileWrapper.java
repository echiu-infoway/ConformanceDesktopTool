package ca.echiu.wrapper;

import lombok.Getter;

import java.io.File;

public class FileWrapper {

    @Getter
    private File file;

    public FileWrapper(File file) {
        this.file = file;
    }

    public String toString() {

        return file.getName();

    }

    public File getFile(){
        return file;
    }
}
