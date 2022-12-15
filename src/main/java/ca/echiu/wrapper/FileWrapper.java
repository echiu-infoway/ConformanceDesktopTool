package ca.echiu.wrapper;

import lombok.Getter;
import lombok.Setter;

import java.io.File;

public class FileWrapper {

    @Getter
    private File file;
    @Setter
    private String name;
    public FileWrapper(File file) {
        this.file = file;
        this.name = file.getName();
    }

    public String toString() {

        return name;

    }
}
