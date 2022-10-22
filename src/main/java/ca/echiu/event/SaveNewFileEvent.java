package ca.echiu.event;

import lombok.Getter;
import lombok.Setter;

import java.io.File;

public class SaveNewFileEvent {

    @Setter
    @Getter
    private String newFileName;

    public SaveNewFileEvent(String newFileName){
        this.newFileName = newFileName;
    }
}
