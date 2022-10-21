package ca.echiu.wrapper;

import java.io.File;

public class FileWrapper {

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
